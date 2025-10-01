package com.sanedge.ecommerce.service.transaction.stats;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.transactions.MonthAmountTransactionRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.transaction.TransactionMonthlyAmountFailedResponse;
import com.sanedge.ecommerce.domain.responses.transaction.TransactionMonthlyAmountSuccessResponse;
import com.sanedge.ecommerce.domain.responses.transaction.TransactionYearlyAmountFailedResponse;
import com.sanedge.ecommerce.domain.responses.transaction.TransactionYearlyAmountSuccessResponse;

public interface TransactionAmountService {
    ApiResponse<List<TransactionMonthlyAmountSuccessResponse>> findMonthlyAmountSuccess(
            MonthAmountTransactionRequest req);

    ApiResponse<List<TransactionYearlyAmountSuccessResponse>> findYearlyAmountSuccess(Integer year);

    ApiResponse<List<TransactionMonthlyAmountFailedResponse>> findMonthlyAmountFailed(
            MonthAmountTransactionRequest req);

    ApiResponse<List<TransactionYearlyAmountFailedResponse>> findYearlyAmountFailed(Integer year);
}