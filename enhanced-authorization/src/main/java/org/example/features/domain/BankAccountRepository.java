package org.example.features.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    @Modifying
    @Query("UPDATE BankAccount a SET a.balance = a.balance + :amount WHERE a.id = :id AND a.owner = ?#{principal?.username}")
    int updateBankAccount(Long id, BigDecimal amount);
}
