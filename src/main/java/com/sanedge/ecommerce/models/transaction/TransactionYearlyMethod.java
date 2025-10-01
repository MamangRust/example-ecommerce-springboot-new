package com.sanedge.ecommerce.models.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionYearlyMethod {
    private String year;
    private String paymentMethod;
    private Integer totalTransactions;
    private Long totalAmount;
}