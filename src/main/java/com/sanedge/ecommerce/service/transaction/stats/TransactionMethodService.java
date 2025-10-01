package com.sanedge.ecommerce.service.transaction.stats;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.transactions.MonthMethodTransactionRequest;

import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.transaction.TransactionMonthlyMethodResponse;
import com.sanedge.ecommerce.domain.responses.transaction.TransactionYearlyMethodResponse;

public interface TransactionMethodService {
    ApiResponse<List<TransactionMonthlyMethodResponse>> findMonthlyMethodSuccess(MonthMethodTransactionRequest req);

    ApiResponse<List<TransactionYearlyMethodResponse>> findYearlyMethodSuccess(Integer year);

    ApiResponse<List<TransactionMonthlyMethodResponse>> findMonthlyMethodFailed(MonthMethodTransactionRequest req);

    ApiResponse<List<TransactionYearlyMethodResponse>> findYearlyMethodFailed(Integer year);
}