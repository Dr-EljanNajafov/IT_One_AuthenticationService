package com.itone.it_one_authenticationservice.entity.default_loader;


import com.itone.it_one_authenticationservice.entity.Account;
import com.itone.it_one_authenticationservice.entity.Role;
import com.itone.it_one_authenticationservice.repository.AccountRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultAdminLoader {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void loadDefaultAdmin() {
        Optional<Account> adminAccount = accountRepository.findByMail("admin@mail.ru");

        if (adminAccount.isEmpty()) {
            registerDefaultAdmin();
        }
    }

    private void registerDefaultAdmin() {
        Account admin = Account.builder()
                .role(Role.ADMIN)
                .mail("admin@mail.ru")
                .firstName("admin")
                .lastName("admin")
                .password(passwordEncoder.encode("admin")) // Пароль администратора (нужно заменить на безопасный)
                .build();

        accountRepository.save(admin); // Сохраняем администратора в БД
    }
}