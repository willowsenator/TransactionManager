package com.willowsenator.test.TransactionManager.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;


import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TransactionCreationRequest {
    private String reference;
    @JsonProperty("account_iban")
    @NonNull
    private String accountIban;
    private OffsetDateTime date;
    @NonNull
    private Double amount;

    private Double fee;
    private String description;
}
