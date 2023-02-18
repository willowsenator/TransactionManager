package com.willowsenator.test.TransactionManager.service.impl;

import com.willowsenator.test.TransactionManager.entity.Account;
import com.willowsenator.test.TransactionManager.exception.AccountException;
import com.willowsenator.test.TransactionManager.repository.AccountRepository;
import com.willowsenator.test.TransactionManager.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;
    @Override
    public void updateBalance(String iban, Double transactionTotalAmount) throws AccountException {
        var account = accountRepository.findAccountByIban(iban);
        checkIfExistsAccount(account);
        var newBalance = calculateNewAccountBalance(account, transactionTotalAmount);
        checkIfZeroBalanceAfterTransaction(newBalance);
        account.setBalance(newBalance);

        accountRepository.save(account);
    }

    @Override
    public Account findAccountByIban(String iban) {
        return accountRepository.findAccountByIban(iban);
    }

    @Override
    public void createAccount(Account account) {
      accountRepository.save(account);
    }

    @Override
    public List<Account> getAllAccounts() {
        return StreamSupport.stream(accountRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    private Double calculateNewAccountBalance(Account account, Double transactionTotalAmount) {
        return account.getBalance() + transactionTotalAmount;
    }



    private void checkIfZeroBalanceAfterTransaction(Double newBalance) throws AccountException {
        if( newBalance <= 0) throw new AccountException(AccountException.TRANSACTION_TOTAL_BALANCE_BELOW_ZERO);
    }

    private static void checkIfExistsAccount(Account account) throws AccountException {
        if(Optional.ofNullable(account).isEmpty()){
            throw new AccountException(AccountException.ACCOUNT_NOT_EXIST);
        }
    }
}
