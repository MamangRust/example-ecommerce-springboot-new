package com.sanedge.ecommerce.models.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionMonthlyAmountFailed {
    private String year;
    private String month;
    private Integer totalFailed;
    private Integer totalAmount;
}