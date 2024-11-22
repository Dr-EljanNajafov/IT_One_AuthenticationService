package com.itone.it_one_authenticationservice.dto.mapper;

import com.itone.it_one_authenticationservice.dto.AccountDto;
import com.itone.it_one_authenticationservice.entity.Account;
import org.springframework.stereotype.Service;
import java.util.function.Function;

@Service
public class AccountDtoMapper implements Function<Account, AccountDto> {
    @Override
    public AccountDto apply(Account account) {
        return new AccountDto(
                account.getId(),
                account.getMail(),
                account.getLastName(),
                account.getFirstName(),
                account.getRole()
        );
    }
}
