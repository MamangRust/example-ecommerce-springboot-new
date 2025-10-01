package com.sanedge.ecommerce.domain.responses.transaction;

import com.sanedge.ecommerce.models.transaction.TransactionYearlyAmountFailed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionYearlyAmountFailedResponse {
    private String year;
    private Integer totalFailed;
    private Integer totalAmount;

    public static TransactionYearlyAmountFailedResponse from(TransactionYearlyAmountFailed entity) {
        return TransactionYearlyAmountFailedResponse.builder()
                .year(entity.getYear())
                .totalFailed(entity.getTotalFailed())
                .totalAmount(entity.getTotalAmount())
                .build();
    }
}