package com.willowsenator.test.TransactionManager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.willowsenator.test.TransactionManager.domain.enums.Status;
import com.willowsenator.test.TransactionManager.domain.request.TransactionCreationRequest;
import com.willowsenator.test.TransactionManager.domain.request.TransactionSearchRequest;
import com.willowsenator.test.TransactionManager.domain.request.TransactionStatusRequest;
import com.willowsenator.test.TransactionManager.entity.Account;
import com.willowsenator.test.TransactionManager.service.AccountService;
import com.willowsenator.test.TransactionManager.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TransactionControllerTest
{
    private static final String ROOT_PATH = "/transactions";
    private static final String IBAN = "ES9820385778983000760236";
    private static final Double BALANCE = 10_000.0;
    private static final Double AMOUNT = 200.0;
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Test
    void createTransactionOk() throws Exception {
        var account = Account.builder().iban(IBAN).balance(BALANCE).build();
        accountService.createAccount(account);

        var transactionRequest = TransactionCreationRequest.builder().accountIban(IBAN).amount(AMOUNT).build();
        var json = mapper.writeValueAsString(transactionRequest);

        var result = mockMvc.perform(MockMvcRequestBuilders.post(ROOT_PATH + "/create").content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        assertNotNull(result.andReturn().getResponse());

    }

    @Test
    void searchTransactionsOk() throws Exception {
        var account = Account.builder().iban(IBAN).balance(BALANCE).build();
        accountService.createAccount(account);

        var transactionCreationRequest = TransactionCreationRequest.builder().accountIban(IBAN).amount(AMOUNT).build();
        transactionService.createTransaction(transactionCreationRequest);

        var transactionSearchRequest = TransactionSearchRequest.builder().accountIban(IBAN).build();
        var json = mapper.writeValueAsString(transactionSearchRequest);

        var result = mockMvc.perform(MockMvcRequestBuilders.post(ROOT_PATH + "/search")
                .content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                        .andExpect(jsonPath("$").isArray());

        assertNotNull(result.andReturn().getResponse());

    }

    @Test
    void statusTransactionsOk() throws Exception {
        var account = Account.builder().iban(IBAN).balance(BALANCE).build();
        accountService.createAccount(account);

        var transactionCreationRequest = TransactionCreationRequest.builder().accountIban(IBAN).amount(AMOUNT).build();
        transactionService.createTransaction(transactionCreationRequest);

        var transactionSearchRequest = TransactionSearchRequest.builder().accountIban(IBAN).build();
        var transactions = transactionService.searchTransactionsByAccountIban(transactionSearchRequest);
        var firstTransaction = transactions.get(0);

        var transactionStatusRequest = TransactionStatusRequest.builder()
                .reference(firstTransaction.getReference()).build();
        var json = mapper.writeValueAsString(transactionStatusRequest);


       var result = mockMvc.perform(MockMvcRequestBuilders.post(ROOT_PATH + "/status")
                        .content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.reference", is(firstTransaction.getReference())))
               .andExpect(jsonPath("$.status", is(Status.PENDING.name())));

        assertNotNull(result.andReturn().getResponse());

    }

    @Test
    void getAllTransactionsOk() throws Exception {
        var account = Account.builder().iban(IBAN).balance(BALANCE).build();
        accountService.createAccount(account);

        var transactionCreationRequest = TransactionCreationRequest.builder().accountIban(IBAN).amount(AMOUNT).build();
        transactionService.createTransaction(transactionCreationRequest);


        var result = mockMvc.perform(MockMvcRequestBuilders.get(ROOT_PATH + "/getAll")
                       .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        assertNotNull(result.andReturn().getResponse());

    }
}