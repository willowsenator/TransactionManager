-- Create and Delete tables if exists
DROP TABLE IF EXISTS TRANSACTION;
DROP TABLE IF EXISTS ACCOUNT;

CREATE TABLE ACCOUNT (
                         IBAN VARCHAR(24) PRIMARY KEY,
                         BALANCE FLOAT NOT NULL
);

CREATE TABLE TRANSACTION(
                            REFERENCE VARCHAR(10) PRIMARY KEY,
                            DATE TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
                            AMOUNT FLOAT NOT NULL,
                            FEE FLOAT NOT NULL,
                            DESCRIPTION VARCHAR(120),
                            ACCOUNT_IBAN VARCHAR(24),
                            FOREIGN KEY (ACCOUNT_IBAN) REFERENCES ACCOUNT(IBAN)
);