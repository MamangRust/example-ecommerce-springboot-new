package com.sanedge.ecommerce.service.transaction.statsbymerchant;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.transactions.MonthAmountTransactionMerchant;
import com.sanedge.ecommerce.domain.requests.transactions.YearAmountTransactionMerchant;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.transaction.TransactionMonthlyAmountFailedResponse;
import com.sanedge.ecommerce.domain.responses.transaction.TransactionMonthlyAmountSuccessResponse;
import com.sanedge.ecommerce.domain.responses.transaction.TransactionYearlyAmountFailedResponse;
import com.sanedge.ecommerce.domain.responses.transaction.TransactionYearlyAmountSuccessResponse;

public interface TransactionAmountByMerchantService {
    ApiResponse<List<TransactionMonthlyAmountSuccessResponse>> findMonthlyAmountSuccessByMerchant(
            MonthAmountTransactionMerchant req);

    ApiResponse<List<TransactionYearlyAmountSuccessResponse>> findYearlyAmountSuccessByMerchant(
            YearAmountTransactionMerchant req);

    ApiResponse<List<TransactionMonthlyAmountFailedResponse>> findMonthlyAmountFailedByMerchant(
            MonthAmountTransactionMerchant req);

    ApiResponse<List<TransactionYearlyAmountFailedResponse>> findYearlyAmountFailedByMerchant(
            YearAmountTransactionMerchant req);
}