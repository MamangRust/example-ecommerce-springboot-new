package com.sanedge.ecommerce.service.transaction.statsbymerchant;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.transactions.MonthMethodTransactionMerchantRequest;
import com.sanedge.ecommerce.domain.requests.transactions.YearMethodTransactionMerchantRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.transaction.TransactionMonthlyMethodResponse;
import com.sanedge.ecommerce.domain.responses.transaction.TransactionYearlyMethodResponse;

public interface TransactionMethodByMerchantService {
    ApiResponse<List<TransactionMonthlyMethodResponse>> findMonthlyMethodByMerchantSuccess(
            MonthMethodTransactionMerchantRequest req);

    ApiResponse<List<TransactionMonthlyMethodResponse>> findMonthlyMethodByMerchantFailed(
            MonthMethodTransactionMerchantRequest req);

    ApiResponse<List<TransactionYearlyMethodResponse>> findYearlyMethodByMerchantSuccess(
            YearMethodTransactionMerchantRequest req);

    ApiResponse<List<TransactionYearlyMethodResponse>> findYearlyMethodByMerchantFailed(
            YearMethodTransactionMerchantRequest req);
}