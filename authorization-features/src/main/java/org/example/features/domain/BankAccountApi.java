package org.example.features.domain;

import org.example.features.security.PreWriteBankAccount;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class BankAccountApi {

    private final BankAccountService bankAccountService;

    public BankAccountApi(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping
    List<BankAccount> findAll() {
        return bankAccountService.findAll();
    }

    @GetMapping("/{id}")
    ResponseEntity<BankAccount> findById(@PathVariable("id") long id) {
        BankAccount bankAccount = bankAccountService.findById(id);
        return bankAccount != null ? ResponseEntity.ok(bankAccount) : ResponseEntity.notFound().build();
    }

    @PreWriteBankAccount("#toSave")
    BankAccount save(BankAccount toSave) {
        return bankAccountService.save(toSave);
    }


    @PreWriteBankAccount("#toUpdate")
    BankAccount update(BankAccount toUpdate) {
        return bankAccountService.save(toUpdate);
    }

}
