package com.sanedge.ecommerce.repository.transaction.stats;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.transaction.Transaction;
import com.sanedge.ecommerce.models.transaction.TransactionMonthlyMethod;
import com.sanedge.ecommerce.models.transaction.TransactionYearlyMethod;

@Repository
public interface TransactionMethodRepository extends JpaRepository<Transaction, Long> {

    @Query(value = """
            WITH
                date_ranges AS (
                    SELECT
                        PARSEDATETIME(CAST(:year1 AS VARCHAR) || '-' || CAST(:month1 AS VARCHAR) || '-01', 'yyyy-M-dd') AS range1_start,
                        DATEADD('MONTH', 1, PARSEDATETIME(CAST(:year1 AS VARCHAR) || '-' || CAST(:month1 AS VARCHAR) || '-01', 'yyyy-M-dd')) AS range1_end,
                        PARSEDATETIME(CAST(:year2 AS VARCHAR) || '-' || CAST(:month2 AS VARCHAR) || '-01', 'yyyy-M-dd') AS range2_start,
                        DATEADD('MONTH', 1, PARSEDATETIME(CAST(:year2 AS VARCHAR) || '-' || CAST(:month2 AS VARCHAR) || '-01', 'yyyy-M-dd')) AS range2_end
                ),
                payment_methods AS (
                    SELECT DISTINCT payment_method
                    FROM transactions
                    WHERE deleted_at IS NULL
                ),
                all_months AS (
                    SELECT range1_start AS activity_month FROM date_ranges
                    UNION
                    SELECT range2_start FROM date_ranges
                ),
                all_combinations AS (
                    SELECT am.activity_month, pm.payment_method
                    FROM all_months am
                    CROSS JOIN payment_methods pm
                ),
                monthly_transactions AS (
                    SELECT
                        PARSEDATETIME(FORMATDATETIME(t.created_at, 'yyyy-MM-01'), 'yyyy-MM-dd') AS activity_month,
                        t.payment_method,
                        COUNT(t.transaction_id) AS total_transactions,
                        CAST(COALESCE(SUM(t.amount), 0) AS BIGINT) AS total_amount
                    FROM transactions t
                    JOIN date_ranges dr ON (
                        (t.created_at >= dr.range1_start AND t.created_at < dr.range1_end)
                        OR
                        (t.created_at >= dr.range2_start AND t.created_at < dr.range2_end)
                    )
                    WHERE t.deleted_at IS NULL AND t.payment_status = 'SUCCESS'
                    GROUP BY PARSEDATETIME(FORMATDATETIME(t.created_at, 'yyyy-MM-01'), 'yyyy-MM-dd'), t.payment_method
                )
            SELECT
                FORMATDATETIME(ac.activity_month, 'MMM') AS "month",
                ac.payment_method AS paymentMethod,
                CAST(COALESCE(mt.total_transactions, 0) AS BIGINT) AS totalTransactions,
                CAST(COALESCE(mt.total_amount, 0) AS BIGINT) AS totalAmount
            FROM all_combinations ac
            LEFT JOIN monthly_transactions mt
                   ON ac.activity_month = mt.activity_month
                  AND ac.payment_method = mt.payment_method
            ORDER BY ac.activity_month, ac.payment_method
            """, nativeQuery = true)
    List<TransactionMonthlyMethod> findMonthlyMethodsSuccess(
            @Param("year1") Integer year1,
            @Param("month1") Integer month1,
            @Param("year2") Integer year2,
            @Param("month2") Integer month2);

    @Query(value = """
            WITH
                date_ranges AS (
                    SELECT
                        PARSEDATETIME(CAST(:year1 AS VARCHAR) || '-' || CAST(:month1 AS VARCHAR) || '-01', 'yyyy-M-dd') AS range1_start,
                        DATEADD('MONTH', 1, PARSEDATETIME(CAST(:year1 AS VARCHAR) || '-' || CAST(:month1 AS VARCHAR) || '-01', 'yyyy-M-dd')) AS range1_end,
                        PARSEDATETIME(CAST(:year2 AS VARCHAR) || '-' || CAST(:month2 AS VARCHAR) || '-01', 'yyyy-M-dd') AS range2_start,
                        DATEADD('MONTH', 1, PARSEDATETIME(CAST(:year2 AS VARCHAR) || '-' || CAST(:month2 AS VARCHAR) || '-01', 'yyyy-M-dd')) AS range2_end
                ),
                payment_methods AS (
                    SELECT DISTINCT payment_method
                    FROM transactions
                    WHERE deleted_at IS NULL
                ),
                all_months AS (
                    SELECT range1_start AS activity_month FROM date_ranges
                    UNION
                    SELECT range2_start FROM date_ranges
                ),
                all_combinations AS (
                    SELECT am.activity_month, pm.payment_method
                    FROM all_months am
                    CROSS JOIN payment_methods pm
                ),
                monthly_transactions AS (
                    SELECT
                        PARSEDATETIME(FORMATDATETIME(t.created_at, 'yyyy-MM-01'), 'yyyy-MM-dd') AS activity_month,
                        t.payment_method,
                        COUNT(t.transaction_id) AS total_transactions,
                        CAST(COALESCE(SUM(t.amount), 0) AS BIGINT) AS total_amount
                    FROM transactions t
                    JOIN date_ranges dr ON (
                        (t.created_at >= dr.range1_start AND t.created_at < dr.range1_end)
                        OR
                        (t.created_at >= dr.range2_start AND t.created_at < dr.range2_end)
                    )
                    WHERE t.deleted_at IS NULL AND t.payment_status = 'FAILED'
                    GROUP BY PARSEDATETIME(FORMATDATETIME(t.created_at, 'yyyy-MM-01'), 'yyyy-MM-dd'), t.payment_method
                )
            SELECT
                FORMATDATETIME(ac.activity_month, 'MMM') AS "month",
                ac.payment_method AS paymentMethod,
                CAST(COALESCE(mt.total_transactions, 0) AS BIGINT) AS totalTransactions,
                CAST(COALESCE(mt.total_amount, 0) AS BIGINT) AS totalAmount
            FROM all_combinations ac
            LEFT JOIN monthly_transactions mt
                ON ac.activity_month = mt.activity_month
                AND ac.payment_method = mt.payment_method
            ORDER BY ac.activity_month, ac.payment_method
            """, nativeQuery = true)
    List<TransactionMonthlyMethod> findMonthlyMethodsFailed(
            @Param("year1") Integer year1,
            @Param("month1") Integer month1,
            @Param("year2") Integer year2,
            @Param("month2") Integer month2);

    @Query(value = """
            WITH
                year_range AS (
                    SELECT
                        CAST(:year AS INTEGER) - 1 AS start_year,
                        CAST(:year AS INTEGER) AS end_year
                ),
                payment_methods AS (
                    SELECT DISTINCT payment_method
                    FROM transactions
                    WHERE deleted_at IS NULL
                ),
                all_years AS (
                    SELECT (SELECT start_year FROM year_range) AS "year"
                    UNION
                    SELECT (SELECT end_year FROM year_range) AS "year"
                ),
                all_combinations AS (
                    SELECT CAST(ay."year" AS VARCHAR) AS "year", pm.payment_method
                    FROM all_years ay
                    CROSS JOIN payment_methods pm
                ),
                yearly_transactions AS (
                    SELECT
                        CAST(EXTRACT(YEAR FROM t.created_at) AS VARCHAR) AS "year",
                        t.payment_method,
                        COUNT(t.transaction_id) AS total_transactions,
                        CAST(COALESCE(SUM(t.amount), 0) AS BIGINT) AS total_amount
                    FROM transactions t
                    WHERE
                        t.deleted_at IS NULL
                        AND t.payment_status = 'SUCCESS'
                        AND EXTRACT(YEAR FROM t.created_at) BETWEEN (SELECT start_year FROM year_range) AND (SELECT end_year FROM year_range)
                    GROUP BY EXTRACT(YEAR FROM t.created_at), t.payment_method
                )
            SELECT
                ac."year" AS "year",
                ac.payment_method AS paymentMethod,
                CAST(COALESCE(yt.total_transactions, 0) AS BIGINT) AS totalTransactions,
                CAST(COALESCE(yt.total_amount, 0) AS BIGINT) AS totalAmount
            FROM all_combinations ac
            LEFT JOIN yearly_transactions yt
                ON ac."year" = yt."year"
                AND ac.payment_method = yt.payment_method
            ORDER BY ac."year", ac.payment_method
            """, nativeQuery = true)
    List<TransactionYearlyMethod> findYearlyMethodsSuccess(@Param("year") Integer year);

    @Query(value = """
            WITH
                year_range AS (
                    SELECT
                        CAST(:year AS INTEGER) - 1 AS start_year,
                        CAST(:year AS INTEGER) AS end_year
                ),
                payment_methods AS (
                    SELECT DISTINCT payment_method
                    FROM transactions
                    WHERE deleted_at IS NULL
                ),
                all_years AS (
                    SELECT (SELECT start_year FROM year_range) AS "year"
                    UNION
                    SELECT (SELECT end_year FROM year_range) AS "year"
                ),
                all_combinations AS (
                    SELECT CAST(ay."year" AS VARCHAR) AS "year", pm.payment_method
                    FROM all_years ay
                    CROSS JOIN payment_methods pm
                ),
                yearly_transactions AS (
                    SELECT
                        CAST(EXTRACT(YEAR FROM t.created_at) AS VARCHAR) AS "year",
                        t.payment_method,
                        COUNT(t.transaction_id) AS total_transactions,
                        CAST(COALESCE(SUM(t.amount), 0) AS BIGINT) AS total_amount
                    FROM transactions t
                    WHERE
                        t.deleted_at IS NULL
                        AND t.payment_status = 'FAILED'
                        AND EXTRACT(YEAR FROM t.created_at) BETWEEN (SELECT start_year FROM year_range) AND (SELECT end_year FROM year_range)
                    GROUP BY EXTRACT(YEAR FROM t.created_at), t.payment_method
                )
            SELECT
                ac."year" AS "year",
                ac.payment_method AS paymentMethod,
                CAST(COALESCE(yt.total_transactions, 0) AS BIGINT) AS totalTransactions,
                CAST(COALESCE(yt.total_amount, 0) AS BIGINT) AS totalAmount
            FROM all_combinations ac
            LEFT JOIN yearly_transactions yt
                ON ac."year" = yt."year"
                AND ac.payment_method = yt.payment_method
            ORDER BY ac."year", ac.payment_method
            """, nativeQuery = true)
    List<TransactionYearlyMethod> findYearlyMethodsFailed(@Param("year") Integer year);
}
