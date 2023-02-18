package com.willowsenator.test.TransactionManager.service.impl;

import com.willowsenator.test.TransactionManager.domain.enums.Channel;
import com.willowsenator.test.TransactionManager.domain.enums.Status;
import com.willowsenator.test.TransactionManager.domain.request.TransactionCreationRequest;
import com.willowsenator.test.TransactionManager.domain.request.TransactionSearchRequest;
import com.willowsenator.test.TransactionManager.domain.request.TransactionStatusRequest;
import com.willowsenator.test.TransactionManager.domain.response.TransactionStatusResponse;
import com.willowsenator.test.TransactionManager.entity.Transaction;
import com.willowsenator.test.TransactionManager.exception.AccountException;
import com.willowsenator.test.TransactionManager.mapper.TransactionMapper;
import com.willowsenator.test.TransactionManager.repository.TransactionRepository;
import com.willowsenator.test.TransactionManager.service.AccountService;
import com.willowsenator.test.TransactionManager.service.TransactionService;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {
    // By default, we assume a fee of 2% if not informed
    public static final Double DEFAULT_FEE_PERCENTAGE = 0.02;

    // By default, we assume a reference 1111A if not have any transaction in the db
    public static final String DEFAULT_REFERENCE = "1111A";
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Autowired
    public TransactionServiceImpl(AccountService accountService, TransactionRepository transactionRepository){
        this.accountService = accountService;
        this.transactionRepository = transactionRepository;

        transactionMapper = Mappers.getMapper(TransactionMapper.class);
    }

    @Override
    public void createTransaction(TransactionCreationRequest transactionCreationRequest) throws AccountException {

        if(Optional.ofNullable(transactionCreationRequest.getFee()).isEmpty()) {
            transactionCreationRequest.setFee(calculateTransactionFee(transactionCreationRequest.getAmount()));
        }

        if(Optional.ofNullable(transactionCreationRequest.getReference()).isEmpty()){
            transactionCreationRequest.setReference(generateReference());
        }

        accountService.updateBalance(transactionCreationRequest.getAccountIban(), calculateTransactionTotal(transactionCreationRequest));
        var account = accountService.findAccountByIban(transactionCreationRequest.getAccountIban());


        var transaction = transactionMapper.transactionCreationRequestToTransaction(transactionCreationRequest);
        transaction.setAccountIban(account);

        transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> searchTransactionsByAccountIban(TransactionSearchRequest transactionSearchRequest) {
        List<Transaction> transactions;
        var account = accountService.findAccountByIban(transactionSearchRequest.getAccountIban());
        if(Optional.ofNullable(transactionSearchRequest.getIsSortByAmountAscending()).isPresent()
                && transactionSearchRequest.getIsSortByAmountAscending()){
            transactions = transactionRepository.findTransactionsByAccountIbanOrderByAmountAsc(account);
        }
        else{
            transactions = transactionRepository.findTransactionsByAccountIbanOrderByAmountDesc(account);
        }
       return transactions;
    }


    @Override
    public TransactionStatusResponse getTransactionStatus(TransactionStatusRequest transactionStatusRequest) {
        var transaction = transactionRepository.findTransactionByReference(transactionStatusRequest.getReference());
        if(Optional.ofNullable(transaction).isPresent()){
            var transactionDateAtStartOfDay =  transaction.getDate().toLocalDate().atStartOfDay().toLocalDate();
            var currentDate = LocalDate.now();
            if(transactionDateAtStartOfDay.isBefore(currentDate)){
                return generateStatusResponseBeforeToday(transaction, transactionStatusRequest);

            }else if(transactionDateAtStartOfDay.isEqual(currentDate)){
                return generateStatusResponseEqualToToday(transaction, transactionStatusRequest);

            }else if(transactionDateAtStartOfDay.isAfter(currentDate)){
                return generateStatusResponseAfterToday(transaction, transactionStatusRequest);
            }
        }
        return TransactionStatusResponse.builder().status(Status.INVALID)
                .reference(transactionStatusRequest.getReference()).build();
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return StreamSupport.stream(transactionRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    private TransactionStatusResponse generateStatusResponseBeforeToday(Transaction transaction,
                                                                        TransactionStatusRequest transactionStatusRequest){
        if(isInternalChannel(transactionStatusRequest)){
            return TransactionStatusResponse.builder().reference(transaction.getReference())
                    .status(Status.SETTLED).amount(transaction.getAmount()).fee(transaction.getFee()).build();
        }
        else{
            var settledAmount = transaction.getAmount() - transaction.getFee();
            return TransactionStatusResponse.builder().reference(transaction.getReference())
                    .status(Status.SETTLED).amount(settledAmount).build();
        }
    }

    private TransactionStatusResponse generateStatusResponseEqualToToday(Transaction transaction, TransactionStatusRequest transactionStatusRequest){
        if(isInternalChannel(transactionStatusRequest)){
            return TransactionStatusResponse.builder().reference(transaction.getReference())
                    .status(Status.PENDING).amount(transaction.getAmount()).fee(transaction.getFee()).build();
        }
        else{
            var settledAmount = transaction.getAmount() - transaction.getFee();
            return TransactionStatusResponse.builder().reference(transaction.getReference())
                    .status(Status.PENDING).amount(settledAmount).build();
        }
    }

    private TransactionStatusResponse generateStatusResponseAfterToday(Transaction transaction, TransactionStatusRequest transactionStatusRequest){
        if(isInternalChannel(transactionStatusRequest)){
            return TransactionStatusResponse.builder().reference(transaction.getReference())
                    .status(Status.FUTURE).amount(transaction.getAmount()).fee(transaction.getFee()).build();
        }
        else if(isATMChannel(transactionStatusRequest)){
            var settledAmount = transaction.getAmount() - transaction.getFee();
            return TransactionStatusResponse.builder().reference(transaction.getReference())
                    .status(Status.PENDING).amount(settledAmount).build();
        }
        else{
            var settledAmount = transaction.getAmount() - transaction.getFee();
            return TransactionStatusResponse.builder().reference(transaction.getReference())
                    .status(Status.FUTURE).amount(settledAmount).build();
        }
    }

    private Boolean isATMChannel(TransactionStatusRequest transactionStatusRequest){
        return Optional.ofNullable(transactionStatusRequest.getChannel()).isPresent() && transactionStatusRequest.getChannel().equals(Channel.ATM);
    }

    private Boolean isInternalChannel(TransactionStatusRequest transactionStatusRequest){
        return Optional.ofNullable(transactionStatusRequest.getChannel()).isPresent() && transactionStatusRequest.getChannel().equals(Channel.INTERNAL);
    }

    private Double calculateTransactionTotal(TransactionCreationRequest transactionCreationRequest){
        return transactionCreationRequest.getAmount() - transactionCreationRequest.getFee();
    }


    private Double calculateTransactionFee(Double amount) {
        return Math.abs(amount) * DEFAULT_FEE_PERCENTAGE;
    }

    private String generateReference(){
        var reference = transactionRepository.findLastTransactionEntry();
        var result = DEFAULT_REFERENCE;

        if(Optional.ofNullable(reference).isPresent()){
            var hexReference = Integer.parseInt(reference, 16);
            hexReference++;
            result = String.valueOf(hexReference);
        }
        return result;
    }

}
