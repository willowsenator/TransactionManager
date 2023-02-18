package com.willowsenator.test.TransactionManager.mapper;

import com.willowsenator.test.TransactionManager.domain.request.TransactionCreationRequest;
import com.willowsenator.test.TransactionManager.entity.Transaction;

import java.time.OffsetDateTime;
import java.util.Optional;

public class TransactionMapperImpl implements TransactionMapper{
    @Override
    public Transaction transactionCreationRequestToTransaction(TransactionCreationRequest transactionCreationRequest) {
        return Transaction.builder()
                .date(Optional.ofNullable(transactionCreationRequest.getDate()).isEmpty() ? OffsetDateTime.now()
                        : transactionCreationRequest.getDate())
                .description(transactionCreationRequest.getDescription())
                .reference(transactionCreationRequest.getReference()).amount(transactionCreationRequest.getAmount())
                .fee(transactionCreationRequest.getFee()).build();
    }
}
