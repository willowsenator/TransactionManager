package com.willowsenator.test.TransactionManager.service;

import com.willowsenator.test.TransactionManager.entity.Account;
import com.willowsenator.test.TransactionManager.exception.AccountException;

import java.util.List;

public interface AccountService {
    void updateBalance(String iban, Double transactionTotalAmount) throws AccountException;
    Account findAccountByIban(String iban);
    void createAccount(Account account);
    List<Account> getAllAccounts();
}
