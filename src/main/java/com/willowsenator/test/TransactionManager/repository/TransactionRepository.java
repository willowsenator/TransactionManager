package com.willowsenator.test.TransactionManager.repository;

import com.willowsenator.test.TransactionManager.entity.Account;
import com.willowsenator.test.TransactionManager.entity.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface TransactionRepository extends CrudRepository<Transaction, String> {
    List<Transaction> findTransactionsByAccountIbanOrderByAmountDesc(Account iban);
    List<Transaction> findTransactionsByAccountIbanOrderByAmountAsc(Account iban);
    Transaction findTransactionByReference(String reference);
    @Query("select tr.reference from Transaction tr order by tr.reference desc limit 1")
    String findLastTransactionEntry();
}
