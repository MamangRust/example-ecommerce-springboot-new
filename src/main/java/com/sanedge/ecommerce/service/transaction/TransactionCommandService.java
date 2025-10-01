package com.sanedge.ecommerce.service.transaction;

import com.sanedge.ecommerce.domain.requests.transactions.CreateTransactionRequest;
import com.sanedge.ecommerce.domain.requests.transactions.UpdateTransactionRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.transaction.TransactionResponse;
import com.sanedge.ecommerce.domain.responses.transaction.TransactionResponseDeleteAt;

public interface TransactionCommandService {
    ApiResponse<TransactionResponse> create(CreateTransactionRequest request);

    ApiResponse<TransactionResponse> update(UpdateTransactionRequest request);

    ApiResponse<TransactionResponseDeleteAt> trash(Integer id);

    ApiResponse<TransactionResponseDeleteAt> restore(Integer id);

    ApiResponse<Boolean> delete(Integer id);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}
