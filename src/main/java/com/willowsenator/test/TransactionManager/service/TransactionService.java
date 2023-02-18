package com.willowsenator.test.TransactionManager.service;

import com.willowsenator.test.TransactionManager.domain.request.TransactionCreationRequest;
import com.willowsenator.test.TransactionManager.domain.request.TransactionSearchRequest;
import com.willowsenator.test.TransactionManager.domain.request.TransactionStatusRequest;
import com.willowsenator.test.TransactionManager.domain.response.TransactionStatusResponse;
import com.willowsenator.test.TransactionManager.entity.Transaction;
import com.willowsenator.test.TransactionManager.exception.AccountException;

import java.util.List;

public interface TransactionService {
    void createTransaction(TransactionCreationRequest transactionCreationRequest) throws AccountException;
    List<Transaction> searchTransactionsByAccountIban(TransactionSearchRequest transactionSearchRequest);

    TransactionStatusResponse getTransactionStatus(TransactionStatusRequest transactionStatusRequest);

    List<Transaction> getAllTransactions();
}
