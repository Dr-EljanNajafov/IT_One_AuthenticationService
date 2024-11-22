package com.itone.it_one_authenticationservice.controller;

import com.itone.it_one_authenticationservice.auth.JwtService;
import com.itone.it_one_authenticationservice.dto.AccountDto;
import com.itone.it_one_authenticationservice.entity.register.AuthenticationRequest;
import com.itone.it_one_authenticationservice.entity.register.RegisterRequest;
import com.itone.it_one_authenticationservice.entity.register.UpdateRequest;
import com.itone.it_one_authenticationservice.entity.response.AuthenticationResponse;
import com.itone.it_one_authenticationservice.service.AccountService;
import com.itone.it_one_authenticationservice.service.BlacklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Authentication")
public class AccountController {
    private final AccountService accountService;
    private final JwtService jwtService;
    private final BlacklistService blacklistService;
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Operation(summary = "Получение данных о текущем аккаунте")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/Me")
    public AccountDto me(HttpServletRequest request) {
        return jwtService.accessUser(request, accountService::getAccountInfo);
    }

    @Operation(summary = "Получение новой пары jwt пользователя")
    @PostMapping("/SignIn")
    public AuthenticationResponse signIn(@RequestBody AuthenticationRequest request) {
        logger.info("Получен токен для валидации: {}", request.getMail() + ":" + request.getPassword());
        return accountService.signIn(request);
    }

    @Operation(summary = "Регистрация нового аккаунта")
    @PostMapping("/SignUp")
    public AuthenticationResponse singUp(@RequestBody RegisterRequest request) {
        return accountService.signUp(request);
    }

    @Operation(summary = "Обновление своего аккаунта")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/Update")
    public AuthenticationResponse update(HttpServletRequest request, @RequestBody UpdateRequest updateRequest) {

        // Получаем имя пользователя из текущего токена
        return jwtService.accessUser(request, username -> {
            // Обновляем аккаунт и получаем ответ
            AuthenticationResponse response = accountService.update(username, updateRequest);

            // Извлекаем текущий токен из запроса
            String token = jwtService.extractToken(request).orElseThrow(() -> new RuntimeException("Token not found"));

            // Получаем дату истечения срока действия токена
            Date expiration = jwtService.extractClaim(token, Claims::getExpiration);

            // Добавляем токен в блэклист
            blacklistService.addToBlacklist(token, expiration);

            // Возвращаем новый токен
            return response;
        });
    }

    @Operation(summary = "Выход из аккаунта")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {

        String token = jwtService.extractToken(request).orElseThrow(() -> new RuntimeException("Token not found"));

        // Проверяем валидность токена
        if (!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        // Получаем дату истечения срока действия токена
        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);

        blacklistService.addToBlacklist(token, expiration);


        return ResponseEntity.ok("Logged out successfully");
    }
}