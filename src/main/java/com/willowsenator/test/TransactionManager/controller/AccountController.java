package com.willowsenator.test.TransactionManager.controller;

import com.willowsenator.test.TransactionManager.entity.Account;
import com.willowsenator.test.TransactionManager.service.impl.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountServiceImpl accountService;
    @Autowired
    public AccountController(AccountServiceImpl accountService){
        this.accountService = accountService;
    }

    @PostMapping("create")
    public void createAccount(@RequestBody Account account){
        accountService.createAccount(account);
    }

    @GetMapping("getAll")
    public List<Account> getAllAccounts(){
        return accountService.getAllAccounts();
    }
}
