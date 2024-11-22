package com.itone.it_one_authenticationservice.service;

import com.itone.it_one_authenticationservice.auth.JwtService;
import com.itone.it_one_authenticationservice.dto.AccountDto;
import com.itone.it_one_authenticationservice.entity.Account;
import com.itone.it_one_authenticationservice.entity.Role;
import com.itone.it_one_authenticationservice.entity.register.AuthenticationRequest;
import com.itone.it_one_authenticationservice.entity.register.RegisterRequest;
import com.itone.it_one_authenticationservice.entity.register.UpdateRequest;
import com.itone.it_one_authenticationservice.entity.response.AuthenticationResponse;
import com.itone.it_one_authenticationservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Retrieve user info by username
    public AccountDto getAccountInfo(String username) {

        Account user = getUser(username);
        return new AccountDto(
                user.getId(),
                user.getUsername(),
                user.getLastName(),
                user.getFirstName(),
                user.getRole()
        );
    }

    // User sign-in (authentication)
    public AuthenticationResponse signIn(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getMail(),
                        request.getPassword()
                )
        );

        Account user = accountRepository.findByMail(request.getMail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        String jwtToken = jwtService.generateToken(user);

        return new AuthenticationResponse(jwtToken);
    }

    // User sign-up (registration)
    public AuthenticationResponse signUp(RegisterRequest request) {
        checkUsername(request.getMail());

        Account user = Account.builder()
                .mail(request.getMail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.STAFF)
                .build();

        accountRepository.save(user);

        String jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }

    // Update user information
    public AuthenticationResponse update(String username, UpdateRequest request) {
        // Получаем текущего пользователя
        Account user = getUser(username);

        // Флаг для проверки, был ли обновлен пароль
        boolean isPasswordUpdated = false;

        // Обновляем поля пользователя
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            isPasswordUpdated = true;
        }
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        accountRepository.save(user);

        // Если пароль обновлен, возвращаем новый токен
        if (isPasswordUpdated) {
            return signIn(new AuthenticationRequest(username, request.getPassword()));
        }

        // Если пароль не обновлен, возвращаем старый токен
        return new AuthenticationResponse(jwtService.generateToken(user));
    }

    // Retrieve user by mail
    public Account getUser(String username) {
        Account user = accountRepository.findByMail(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User with username %s doesn't exist".formatted(username)
                ));

        return user;
    }

    // Check if username is already in use
    public void checkUsername(String username, String username1) {
        Optional<Account> userOptional = accountRepository.findByMail(username);
        if (userOptional.isPresent() && !userOptional.get().getUsername().equals(username1)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Username '%s' is already in use".formatted(username)
            );
        }
    }

    public void checkUsername(String username) {
        if (accountRepository.findByMail(username).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Username '%s' is already in use".formatted(username)
            );
        }
    }
}