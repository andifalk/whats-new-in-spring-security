package org.example.features.domain;

import org.example.features.security.PostReadBankAccount;
import org.example.features.security.PreGetBankAccounts;
import org.example.features.security.PreWriteBankAccount;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @PreGetBankAccounts
    List<BankAccount> findAll() {
        return bankAccountRepository.findAll();
    }

    @PostReadBankAccount
    BankAccount findById(long id) {
        return bankAccountRepository.findById(id).orElse(null);
    }

    @PreWriteBankAccount("#toSave")
    BankAccount save(BankAccount toSave) {
        return bankAccountRepository.save(toSave);
    }

    @PreWriteBankAccount("#toUpdate")
    BankAccount update(BankAccount toUpdate) {
        return bankAccountRepository.save(toUpdate);
    }
}
