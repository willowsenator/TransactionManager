package com.willowsenator.test.TransactionManager.exception;

public class AccountException extends Exception{
    public static final String ACCOUNT_NOT_EXIST = "Account not found";
    public static final String TRANSACTION_TOTAL_BALANCE_BELOW_ZERO
            = "Transaction that leaves the total account balance bellow 0 is not allowed";
    public AccountException(String message){
        super(message);
    }
}
