package com.willowsenator.test.TransactionManager.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "ACCOUNT")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
public class Account {
    @Id
    @Column(name = "IBAN", nullable = false)
    @NonNull
    @Length(max = 24)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String iban;

    @Column(name = "BALANCE", nullable = false)
    @JdbcTypeCode(SqlTypes.DOUBLE)
    @NonNull
    private Double balance;
}
