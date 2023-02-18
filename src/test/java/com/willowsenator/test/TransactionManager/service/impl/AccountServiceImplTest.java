package com.willowsenator.test.TransactionManager.service.impl;

import com.willowsenator.test.TransactionManager.entity.Account;
import com.willowsenator.test.TransactionManager.exception.AccountException;
import com.willowsenator.test.TransactionManager.repository.AccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Spliterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    private static final String IBAN = "ES9820385778983000760236";
    private static final Double POSITIVE_TRANSACTION_TOTAL_AMOUNT = 196.0;

    private static final Double NEGATIVE_TRANSACTION_TOTAL_AMOUNT = -196.0;

    private static final Double BALANCE = 10_000.0;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private Iterable<Account> accountIterable;

    @Mock
    private Spliterator<Account> accountSpliterator;

    private AccountServiceImpl accountService;

    @Captor
    private ArgumentCaptor<Account> accountArgumentCaptor;

    @BeforeEach
    void setUp(){
        accountService = new AccountServiceImpl(accountRepository);
    }

    @Test
    void updateBalanceExceptionWhenNotExistsAccount() {
        AccountException ex = assertThrows(AccountException.class,
                ()-> accountService.updateBalance(IBAN, POSITIVE_TRANSACTION_TOTAL_AMOUNT));

        assertEquals(ex.getMessage(), AccountException.ACCOUNT_NOT_EXIST);
    }

    @Test
    void updateBalanceExceptionWhenZeroBalanceAfterTransaction() {
        when(accountRepository.findAccountByIban(any())).thenReturn(createAccount());

        AccountException ex = assertThrows(AccountException.class,
                ()-> accountService.updateBalance(IBAN, NEGATIVE_TRANSACTION_TOTAL_AMOUNT));

        assertEquals(ex.getMessage(), AccountException.TRANSACTION_TOTAL_BALANCE_BELOW_ZERO);
    }

    @Test
    void updateBalanceOk() throws AccountException {
        when(accountRepository.findAccountByIban(any())).thenReturn(createAccount());

        accountService.updateBalance(IBAN, POSITIVE_TRANSACTION_TOTAL_AMOUNT);

        verify(accountRepository).save(accountArgumentCaptor.capture());
        var account = accountArgumentCaptor.getValue();
        assertEquals(account.getBalance(), POSITIVE_TRANSACTION_TOTAL_AMOUNT * 2);
        assertEquals(account.getIban(), IBAN);
    }

    @Test
    void findAccountByIbanOk(){
        accountService.findAccountByIban(IBAN);

        verify(accountRepository).findAccountByIban(IBAN);
    }

    @Test
    void createAccountOk(){
        var account = Account.builder().iban(IBAN).balance(BALANCE).build();

        accountService.createAccount(account);

        verify(accountRepository).save(account);
    }

    @Test
    void getAllAccountsOk(){
        doReturn(accountIterable).when(accountRepository).findAll();
        when(accountIterable.spliterator()).thenReturn(accountSpliterator);

        var accounts = accountService.getAllAccounts();

        verify(accountRepository).findAll();
        assertNotNull(accounts);
    }

    private Account createAccount(){
        return Account.builder().iban(IBAN).balance(POSITIVE_TRANSACTION_TOTAL_AMOUNT).build();
    }
}