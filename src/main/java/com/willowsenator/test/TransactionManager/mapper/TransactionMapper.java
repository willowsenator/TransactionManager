package com.willowsenator.test.TransactionManager.mapper;

import com.willowsenator.test.TransactionManager.domain.request.TransactionCreationRequest;
import com.willowsenator.test.TransactionManager.entity.Transaction;
import org.mapstruct.Mapper;

@Mapper
public interface TransactionMapper {
    Transaction transactionCreationRequestToTransaction(TransactionCreationRequest transactionCreationRequest);
}
