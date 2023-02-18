package com.willowsenator.test.TransactionManager.controller;

import com.willowsenator.test.TransactionManager.domain.request.TransactionCreationRequest;
import com.willowsenator.test.TransactionManager.domain.request.TransactionSearchRequest;
import com.willowsenator.test.TransactionManager.domain.request.TransactionStatusRequest;
import com.willowsenator.test.TransactionManager.domain.response.TransactionStatusResponse;
import com.willowsenator.test.TransactionManager.entity.Transaction;
import com.willowsenator.test.TransactionManager.exception.AccountException;
import com.willowsenator.test.TransactionManager.service.impl.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("transactions")
public class TransactionController {

    private final TransactionServiceImpl transactionService;

    @Autowired
    public TransactionController(TransactionServiceImpl transactionService){
        this.transactionService = transactionService;
    }
    @PostMapping("create")
    public void createTransaction(@RequestBody TransactionCreationRequest transactionCreationRequest) throws AccountException {
        transactionService.createTransaction(transactionCreationRequest);
    }

    @PostMapping("search")
    public List<Transaction> searchTransaction(@RequestBody TransactionSearchRequest transactionSearchRequest){
        return transactionService.searchTransactionsByAccountIban(transactionSearchRequest);
    }

    @PostMapping("status")
    public TransactionStatusResponse getTransactionStatus(@RequestBody TransactionStatusRequest transactionStatusRequest){
        return transactionService.getTransactionStatus(transactionStatusRequest);
    }

    @GetMapping("getAll")
    public List<Transaction> getAllTransactions(){
        return transactionService.getAllTransactions();
    }
}
