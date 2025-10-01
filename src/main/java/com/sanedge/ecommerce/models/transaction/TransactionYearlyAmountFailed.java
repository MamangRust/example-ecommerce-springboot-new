package com.sanedge.ecommerce.models.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionYearlyAmountFailed {
    private String year;
    private Integer totalFailed;
    private Integer totalAmount;
}