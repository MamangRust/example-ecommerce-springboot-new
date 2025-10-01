package com.sanedge.ecommerce.repository.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.transaction.Transaction;

@Repository
public interface TransactionCommandRepository
        extends JpaRepository<Transaction, Long>, TransactionCommandRepositoryCustom {
}