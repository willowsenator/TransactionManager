package com.willowsenator.test.TransactionManager.domain.request;

import com.willowsenator.test.TransactionManager.domain.enums.Channel;
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
public class TransactionStatusRequest {
    @NonNull
    private String reference;
    private Channel channel;
}
