package com.willowsenator.test.TransactionManager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.willowsenator.test.TransactionManager.entity.Account;
import com.willowsenator.test.TransactionManager.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AccountControllerTest {
    private static final String ROOT_PATH = "/accounts";
    private static final String IBAN = "ES9820385778983000760236";
    private static final Double BALANCE = 10_000.0;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private AccountService accountService;

    @Test
    void createAccountOk() throws Exception {

        var account = Account.builder().iban(IBAN).balance(BALANCE).build();
        var json = mapper.writeValueAsString(account);

        var result = mockMvc.perform(post(ROOT_PATH + "/create").content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        assertNotNull(result.andReturn().getResponse());
    }

    @Test
    void getAllAccountsOk() throws Exception {
        var account = Account.builder().iban(IBAN).balance(BALANCE).build();
        accountService.createAccount(account);

        var result = mockMvc.perform(get(ROOT_PATH + "/getAll").
                contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        assertNotNull(result.andReturn().getResponse());
    }
}