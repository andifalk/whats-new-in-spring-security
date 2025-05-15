package org.example.features;

import org.example.features.domain.BankAccount;
import org.example.features.domain.BankAccountRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements ApplicationRunner {

    private final BankAccountRepository bankAccountRepository;

    public DataInitializer(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        BankAccount bankAccount1 = new BankAccount(1L, "user", "1111", BigDecimal.valueOf(1000L));
        bankAccountRepository.save(bankAccount1);
        BankAccount bankAccount2 = new BankAccount(2L, "user", "2222", BigDecimal.valueOf(5000L));
        bankAccountRepository.save(bankAccount2);
        BankAccount bankAccount3 = new BankAccount(3L, "accountant", "3333", BigDecimal.valueOf(500L));
        bankAccountRepository.save(bankAccount3);
    }
}
