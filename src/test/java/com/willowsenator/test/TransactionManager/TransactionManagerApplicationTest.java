package com.willowsenator.test.TransactionManager;

import com.willowsenator.test.TransactionManager.controller.AccountController;
import com.willowsenator.test.TransactionManager.controller.TransactionController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class TransactionManagerApplicationTest {
    @Autowired
    private AccountController accountController;

    @Autowired
    private TransactionController transactionController;

    @Test
    public void contextLoads() {
        assertNotNull(accountController);
        assertNotNull(transactionController);
    }
}