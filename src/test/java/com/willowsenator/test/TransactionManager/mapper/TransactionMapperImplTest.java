package com.willowsenator.test.TransactionManager.mapper;


import com.willowsenator.test.TransactionManager.domain.request.TransactionCreationRequest;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionMapperImplTest {

    private static final String IBAN = "ES9820385778983000760236";
    private static final double AMOUNT = 200.0;
    private static final double FEE = 2.5;
    private static final String DESCRIPTION = "description";
    private static final String REFERENCE = "12345A";

    private static final OffsetDateTime DATE = OffsetDateTime.now();
    private final TransactionMapper mapper = Mappers.getMapper(TransactionMapper.class);

    @Test
    void transactionCreationRequestToTransactionWhenOk(){
        var transactionCreationRequest = TransactionCreationRequest.builder().accountIban(IBAN)
                .amount(AMOUNT).fee(FEE).date(DATE).description(DESCRIPTION).reference(REFERENCE).build();

        var transaction = mapper.transactionCreationRequestToTransaction(transactionCreationRequest);

        assertEquals(transaction.getFee(), transactionCreationRequest.getFee());
        assertEquals(transaction.getAmount(), transactionCreationRequest.getAmount());
        assertEquals(transaction.getDescription(), transactionCreationRequest.getDescription());
        assertEquals(transaction.getReference(), transactionCreationRequest.getReference());
        assertEquals(transaction.getDate().toString(), transactionCreationRequest.getDate().toString());
    }
}