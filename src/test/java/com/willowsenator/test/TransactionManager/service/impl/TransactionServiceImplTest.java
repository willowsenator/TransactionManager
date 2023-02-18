package com.willowsenator.test.TransactionManager.service.impl;

import com.willowsenator.test.TransactionManager.domain.enums.Channel;
import com.willowsenator.test.TransactionManager.domain.enums.Status;
import com.willowsenator.test.TransactionManager.domain.request.TransactionCreationRequest;
import com.willowsenator.test.TransactionManager.domain.request.TransactionSearchRequest;
import com.willowsenator.test.TransactionManager.domain.request.TransactionStatusRequest;
import com.willowsenator.test.TransactionManager.entity.Account;
import com.willowsenator.test.TransactionManager.entity.Transaction;
import com.willowsenator.test.TransactionManager.exception.AccountException;
import com.willowsenator.test.TransactionManager.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Spliterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    private static final String REFERENCE = "23434B";
    private static final String IBAN = "ES9820385778983000760236";
    private static final Double AMOUNT = 200.0;

    private static final Double FEE = Math.abs(AMOUNT) * TransactionServiceImpl.DEFAULT_FEE_PERCENTAGE;
    private static final Double SETTLED_AMOUNT = AMOUNT - FEE;
    private TransactionServiceImpl transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountServiceImpl accountService;

    @Mock
    private Iterable<Account> transactionIterable;

    @Mock
    private Spliterator<Account> transactionSpliterator;

    @Captor
    private ArgumentCaptor<Transaction> transactionArgumentCaptor;

    @BeforeEach
    void setup(){
        transactionService = new TransactionServiceImpl(accountService, transactionRepository);
    }

    @Test
    void createTransactionOkWithMandatoryFields()  throws AccountException {
        var transactionCreationRequest = TransactionCreationRequest.builder().accountIban(IBAN).amount(AMOUNT).build();
        when(transactionRepository.save(any())).thenReturn(mock(Transaction.class));
        transactionService.createTransaction(transactionCreationRequest);

        verify(transactionRepository).save(transactionArgumentCaptor.capture());

        var transaction = transactionArgumentCaptor.getValue();
        assertEquals(transaction.getFee(), transactionCreationRequest.getAmount() * TransactionServiceImpl.DEFAULT_FEE_PERCENTAGE);
        assertEquals(transaction.getReference(), TransactionServiceImpl.DEFAULT_REFERENCE);
    }

    @Test
    void createTransactionOkWhenReferenceExistsInDB() throws AccountException {
        var transactionCreationRequest = TransactionCreationRequest.builder().accountIban(IBAN).amount(AMOUNT).build();
        when(transactionRepository.findLastTransactionEntry()).thenReturn(REFERENCE);
        when(transactionRepository.save(any())).thenReturn(mock(Transaction.class));
        var hexInt = Integer.parseInt(REFERENCE, 16);
        hexInt++;


        transactionService.createTransaction(transactionCreationRequest);

        verify(transactionRepository).save(transactionArgumentCaptor.capture());
        var transaction = transactionArgumentCaptor.getValue();
        assertEquals(transaction.getFee(), transactionCreationRequest.getAmount() * TransactionServiceImpl.DEFAULT_FEE_PERCENTAGE);
        assertEquals(transaction.getReference(), String.valueOf(hexInt));
    }

    @Test
    void searchTransactionsByAccountIbanAscendingAmountOK(){
        var transactionSearchRequest = TransactionSearchRequest.builder().accountIban(IBAN).isSortByAmountAscending(true).build();

        var transactions = transactionService.searchTransactionsByAccountIban(transactionSearchRequest);

        verify(transactionRepository).findTransactionsByAccountIbanOrderByAmountAsc(any());
        assertNotNull(transactions);
    }

    @Test
    void searchTransactionsByAccountIbanDescendingAmountOK(){
        var transactionSearchRequest = TransactionSearchRequest.builder().accountIban(IBAN).build();

        var transactions = transactionService.searchTransactionsByAccountIban(transactionSearchRequest);

        verify(transactionRepository).findTransactionsByAccountIbanOrderByAmountDesc(any());
        assertNotNull(transactions);
    }

    @Test
    void getTransactionStatusInvalidWhenNotFoundReference(){
        TransactionStatusRequest transactionStatusRequest = TransactionStatusRequest.builder()
                .reference(REFERENCE).build();

        when(transactionRepository.findTransactionByReference(REFERENCE)).thenReturn(null);

        var transactionStatusResponse = transactionService.getTransactionStatus(transactionStatusRequest);

        assertEquals(transactionStatusResponse.getStatus(), Status.INVALID);
        assertEquals(transactionStatusResponse.getReference(), REFERENCE);
    }

    @Test
    void getTransactionStatusSettledWhenChannelIsClientAndDateBeforeToday(){
        TransactionStatusRequest transactionStatusRequest = TransactionStatusRequest.builder()
                .reference(REFERENCE).build();
        when(transactionRepository.findTransactionByReference(REFERENCE)).thenReturn(createTransactionWithDateBeforeToday());

        var transactionStatusResponse = transactionService.getTransactionStatus(transactionStatusRequest);

        assertEquals(transactionStatusResponse.getStatus(), Status.SETTLED);
        assertEquals(transactionStatusResponse.getReference(), REFERENCE);
        assertEquals(transactionStatusResponse.getAmount(), SETTLED_AMOUNT);
    }

    @Test
    void getTransactionStatusSettledWhenChannelIsInternalAndDateBeforeToday(){
        TransactionStatusRequest transactionStatusRequest = TransactionStatusRequest.builder()
                .reference(REFERENCE).channel(Channel.INTERNAL).build();
        when(transactionRepository.findTransactionByReference(REFERENCE))
                .thenReturn(createTransactionWithDateBeforeToday());

        var transactionStatusResponse = transactionService.getTransactionStatus(transactionStatusRequest);


        assertEquals(transactionStatusResponse.getStatus(), Status.SETTLED);
        assertEquals(transactionStatusResponse.getReference(), REFERENCE);
        assertEquals(transactionStatusResponse.getAmount(), AMOUNT);
        assertEquals(transactionStatusResponse.getFee(), FEE);
    }

    @Test
    void getTransactionStatusPendingWhenChannelIsATMAndDateIsEqualToToday(){
        TransactionStatusRequest transactionStatusRequest = TransactionStatusRequest.builder()
                .reference(REFERENCE).channel(Channel.ATM).build();

        when(transactionRepository.findTransactionByReference(REFERENCE)).thenReturn(createTransactionWithDateEqualToToday());

        var transactionStatusResponse = transactionService.getTransactionStatus(transactionStatusRequest);

        assertEquals(transactionStatusResponse.getStatus(), Status.PENDING);
        assertEquals(transactionStatusResponse.getReference(), REFERENCE);
        assertEquals(transactionStatusResponse.getAmount(), SETTLED_AMOUNT);
    }

    @Test
    void getTransactionStatusPendingWhenChannelIsInternalAndDateEqualToToday(){
        TransactionStatusRequest transactionStatusRequest = TransactionStatusRequest.builder()
                .reference(REFERENCE).channel(Channel.INTERNAL).build();
        when(transactionRepository.findTransactionByReference(REFERENCE))
                .thenReturn(createTransactionWithDateEqualToToday());

        var transactionStatusResponse = transactionService.getTransactionStatus(transactionStatusRequest);


        assertEquals(transactionStatusResponse.getStatus(), Status.PENDING);
        assertEquals(transactionStatusResponse.getReference(), REFERENCE);
        assertEquals(transactionStatusResponse.getAmount(), AMOUNT);
        assertEquals(transactionStatusResponse.getFee(), FEE);
    }

    @Test
    void getTransactionStatusFutureWhenChannelIsClientAndDateGreaterThanToday(){
        TransactionStatusRequest transactionStatusRequest = TransactionStatusRequest.builder()
                .reference(REFERENCE).channel(Channel.CLIENT).build();
        when(transactionRepository.findTransactionByReference(REFERENCE)).thenReturn(createTransactionWithDateGreaterThanToday());

        var transactionStatusResponse = transactionService.getTransactionStatus(transactionStatusRequest);

        assertEquals(transactionStatusResponse.getStatus(), Status.FUTURE);
        assertEquals(transactionStatusResponse.getReference(), REFERENCE);
        assertEquals(transactionStatusResponse.getAmount(), SETTLED_AMOUNT);
    }

    @Test
    void getTransactionStatusFutureWhenChannelIsATMAndDateGreaterThanToday(){
        TransactionStatusRequest transactionStatusRequest = TransactionStatusRequest.builder()
                .reference(REFERENCE).channel(Channel.ATM).build();
        when(transactionRepository.findTransactionByReference(REFERENCE)).thenReturn(createTransactionWithDateGreaterThanToday());

        var transactionStatusResponse = transactionService.getTransactionStatus(transactionStatusRequest);

        assertEquals(transactionStatusResponse.getStatus(), Status.PENDING);
        assertEquals(transactionStatusResponse.getReference(), REFERENCE);
        assertEquals(transactionStatusResponse.getAmount(), SETTLED_AMOUNT);
    }

    @Test
    void getTransactionStatusFutureWhenChannelIsInternalAndDateGreaterThanToday(){
        TransactionStatusRequest transactionStatusRequest = TransactionStatusRequest.builder()
                .reference(REFERENCE).channel(Channel.INTERNAL).build();
        when(transactionRepository.findTransactionByReference(REFERENCE))
                .thenReturn(createTransactionWithDateGreaterThanToday());

        var transactionStatusResponse = transactionService.getTransactionStatus(transactionStatusRequest);


        assertEquals(transactionStatusResponse.getStatus(), Status.FUTURE);
        assertEquals(transactionStatusResponse.getReference(), REFERENCE);
        assertEquals(transactionStatusResponse.getAmount(), AMOUNT);
        assertEquals(transactionStatusResponse.getFee(), FEE);
    }

    @Test
    void getAllTransactionsOk(){
        doReturn(transactionIterable).when(transactionRepository).findAll();
        when(transactionIterable.spliterator()).thenReturn(transactionSpliterator);

        var transactions = transactionService.getAllTransactions();

        verify(transactionRepository).findAll();
        assertNotNull(transactions);
    }

    private Transaction createTransactionWithDateBeforeToday(){
        return Transaction.builder()
                .reference(REFERENCE).date(OffsetDateTime.now().minusDays(1))
                .amount(AMOUNT).fee(FEE).build();
    }

    private Transaction createTransactionWithDateEqualToToday(){
        return Transaction.builder()
                .reference(REFERENCE).date(OffsetDateTime.now())
                .amount(AMOUNT).fee(FEE).build();
    }

    private Transaction createTransactionWithDateGreaterThanToday(){
        return Transaction.builder()
                .reference(REFERENCE).date(OffsetDateTime.now().plusDays(1))
                .amount(AMOUNT).fee(FEE).build();
    }
}