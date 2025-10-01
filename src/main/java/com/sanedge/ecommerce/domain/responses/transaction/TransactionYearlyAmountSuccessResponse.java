package com.sanedge.ecommerce.domain.responses.transaction;

import com.sanedge.ecommerce.models.transaction.TransactionYearlyAmountSuccess;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionYearlyAmountSuccessResponse {
    private String year;
    private Integer totalSuccess;
    private Integer totalAmount;

    public static TransactionYearlyAmountSuccessResponse from(TransactionYearlyAmountSuccess entity) {
        return TransactionYearlyAmountSuccessResponse.builder()
                .year(entity.getYear())
                .totalSuccess(entity.getTotalSuccess())
                .totalAmount(entity.getTotalAmount())
                .build();
    }
}