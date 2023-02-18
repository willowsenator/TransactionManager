package com.willowsenator.test.TransactionManager.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.validator.constraints.Length;

import java.time.OffsetDateTime;

@Entity
@Table(name = "TRANSACTION")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
public class Transaction {
    @Id
    @Column(name = "REFERENCE",nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Length(max = 10)
    @NonNull
    private String reference;

    @Column(name = "DATE", nullable = false)
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    @CreationTimestamp
    private OffsetDateTime date;

    @Column(name = "AMOUNT", nullable = false)
    @JdbcTypeCode(SqlTypes.DOUBLE)
    private Double amount;

    @Column(name="FEE", nullable = false)
    @JdbcTypeCode(SqlTypes.DOUBLE)
    private Double fee;

    @Column(name = "DESCRIPTION")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Length(max = 120)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountIban", referencedColumnName = "IBAN")
    private Account accountIban;
}
