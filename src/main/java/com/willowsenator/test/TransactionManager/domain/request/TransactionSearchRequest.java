package com.willowsenator.test.TransactionManager.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TransactionSearchRequest {
    @JsonProperty("account_iban")
    @NonNull
    private String accountIban;

    private Boolean isSortByAmountAscending;
}
