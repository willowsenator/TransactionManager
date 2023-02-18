package com.willowsenator.test.TransactionManager.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.willowsenator.test.TransactionManager.domain.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TransactionStatusResponse {
    private String reference;
    private Status status;
    private Double amount;
    @JsonIgnore
    private Double fee;
}
