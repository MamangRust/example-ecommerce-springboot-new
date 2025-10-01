package com.sanedge.ecommerce.repository.transaction;

import com.sanedge.ecommerce.models.transaction.Transaction;

public interface TransactionCommandRepositoryCustom {
    Transaction trashed(Long transactionId);

    Transaction restore(Long transactionId);

    Transaction deletePermanent(Long transactionId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}