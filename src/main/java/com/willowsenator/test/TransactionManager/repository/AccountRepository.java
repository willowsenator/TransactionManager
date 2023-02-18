package com.willowsenator.test.TransactionManager.repository;


import com.willowsenator.test.TransactionManager.entity.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, String> {
    Account findAccountByIban(String iban);
}
