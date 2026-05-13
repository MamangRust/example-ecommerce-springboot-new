package com.sanedge.ecommerce.repository.transaction.statsbymerchant;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.transaction.Transaction;
import com.sanedge.ecommerce.models.transaction.TransactionMonthlyAmountFailed;
import com.sanedge.ecommerce.models.transaction.TransactionMonthlyAmountSuccess;
import com.sanedge.ecommerce.models.transaction.TransactionYearlyAmountFailed;
import com.sanedge.ecommerce.models.transaction.TransactionYearlyAmountSuccess;

@Repository
public interface TransactionAmountByMerchantRepository extends JpaRepository<Transaction, Long> {

    @Query(value = """
            WITH
                monthly_data AS (
                    SELECT
                        CAST(EXTRACT(YEAR FROM t.created_at) AS INTEGER) AS "year",
                        CAST(EXTRACT(MONTH FROM t.created_at) AS INTEGER) AS "month",
                        CAST(COUNT(*) AS BIGINT) AS total_success,
                        CAST(COALESCE(SUM(t.amount), 0) AS BIGINT) AS total_amount
                    FROM transactions t
                    WHERE
                        t.deleted_at IS NULL
                        AND t.payment_status = 'SUCCESS'
                        AND t.merchant_id = :merchantId
                        AND (
                            (t.created_at >= PARSEDATETIME(CAST(:year AS VARCHAR) || '-' || LPAD(CAST(:month AS VARCHAR), 2, '0') || '-01', 'yyyy-MM-dd')
                             AND t.created_at < DATEADD('MONTH', 1, PARSEDATETIME(CAST(:year AS VARCHAR) || '-' || LPAD(CAST(:month AS VARCHAR), 2, '0') || '-01', 'yyyy-MM-dd')))
                            OR
                            (t.created_at >= PARSEDATETIME(CAST(:prevYear AS VARCHAR) || '-' || LPAD(CAST(:prevMonth AS VARCHAR), 2, '0') || '-01', 'yyyy-MM-dd')
                             AND t.created_at < DATEADD('MONTH', 1, PARSEDATETIME(CAST(:prevYear AS VARCHAR) || '-' || LPAD(CAST(:prevMonth AS VARCHAR), 2, '0') || '-01', 'yyyy-MM-dd')))
                        )
                    GROUP BY CAST(EXTRACT(YEAR FROM t.created_at) AS INTEGER), CAST(EXTRACT(MONTH FROM t.created_at) AS INTEGER)
                ),
                formatted_data AS (
                    SELECT
                        CAST("year" AS VARCHAR) AS "year",
                        FORMATDATETIME(PARSEDATETIME('2000-' || LPAD(CAST("month" AS VARCHAR), 2, '0') || '-01', 'yyyy-MM-dd'), 'MMM') AS "month",
                        total_success AS totalSuccess,
                        total_amount AS totalAmount
                    FROM monthly_data
                    UNION ALL
                    SELECT
                        CAST(:year AS VARCHAR),
                        FORMATDATETIME(PARSEDATETIME('2000-' || LPAD(CAST(:month AS VARCHAR), 2, '0') || '-01', 'yyyy-MM-dd'), 'MMM'),
                        0, 0
                    WHERE NOT EXISTS (
                        SELECT 1 FROM monthly_data
                        WHERE "year" = :year AND "month" = :month
                    )
                    UNION ALL
                    SELECT
                        CAST(:prevYear AS VARCHAR),
                        FORMATDATETIME(PARSEDATETIME('2000-' || LPAD(CAST(:prevMonth AS VARCHAR), 2, '0') || '-01', 'yyyy-MM-dd'), 'MMM'),
                        0, 0
                    WHERE NOT EXISTS (
                        SELECT 1 FROM monthly_data
                        WHERE "year" = :prevYear AND "month" = :prevMonth
                    )
                )
            SELECT * FROM formatted_data
            ORDER BY "year" DESC, "month" DESC
            """, nativeQuery = true)
    List<TransactionMonthlyAmountSuccess> findMonthlySuccessByMerchant(
            @Param("merchantId") Long merchantId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("prevYear") Integer prevYear,
            @Param("prevMonth") Integer prevMonth);

    @Query(value = """
            WITH
                yearly_data AS (
                    SELECT
                        CAST(EXTRACT(YEAR FROM t.created_at) AS INTEGER) AS "year",
                        CAST(COUNT(*) AS BIGINT) AS total_success,
                        CAST(COALESCE(SUM(t.amount), 0) AS BIGINT) AS total_amount
                    FROM transactions t
                    WHERE
                        t.deleted_at IS NULL
                        AND t.payment_status = 'SUCCESS'
                        AND t.merchant_id = :merchantId
                        AND (EXTRACT(YEAR FROM t.created_at) = :year
                             OR EXTRACT(YEAR FROM t.created_at) = :year - 1)
                    GROUP BY CAST(EXTRACT(YEAR FROM t.created_at) AS INTEGER)
                ),
                formatted_data AS (
                    SELECT
                        CAST("year" AS VARCHAR) AS "year",
                        total_success AS totalSuccess,
                        total_amount AS totalAmount
                    FROM yearly_data
                    UNION ALL
                    SELECT CAST(:year AS VARCHAR), 0, 0 WHERE NOT EXISTS (SELECT 1 FROM yearly_data WHERE "year" = :year)
                    UNION ALL
                    SELECT CAST((:year - 1) AS VARCHAR), 0, 0 WHERE NOT EXISTS (SELECT 1 FROM yearly_data WHERE "year" = :year - 1)
                )
            SELECT * FROM formatted_data
            ORDER BY "year" DESC
            """, nativeQuery = true)
    List<TransactionYearlyAmountSuccess> findYearlySuccessByMerchant(
            @Param("merchantId") Long merchantId,
            @Param("year") Integer year);

    @Query(value = """
            WITH
                monthly_data AS (
                    SELECT
                        CAST(EXTRACT(YEAR FROM t.created_at) AS INTEGER) AS "year",
                        CAST(EXTRACT(MONTH FROM t.created_at) AS INTEGER) AS "month",
                        CAST(COUNT(*) AS BIGINT) AS total_failed,
                        CAST(COALESCE(SUM(t.amount), 0) AS BIGINT) AS total_amount
                    FROM transactions t
                    WHERE
                        t.deleted_at IS NULL
                        AND t.payment_status = 'FAILED'
                        AND t.merchant_id = :merchantId
                        AND (
                            (t.created_at >= PARSEDATETIME(CAST(:year AS VARCHAR) || '-' || LPAD(CAST(:month AS VARCHAR), 2, '0') || '-01', 'yyyy-MM-dd')
                             AND t.created_at < DATEADD('MONTH', 1, PARSEDATETIME(CAST(:year AS VARCHAR) || '-' || LPAD(CAST(:month AS VARCHAR), 2, '0') || '-01', 'yyyy-MM-dd')))
                            OR
                            (t.created_at >= PARSEDATETIME(CAST(:prevYear AS VARCHAR) || '-' || LPAD(CAST(:prevMonth AS VARCHAR), 2, '0') || '-01', 'yyyy-MM-dd')
                             AND t.created_at < DATEADD('MONTH', 1, PARSEDATETIME(CAST(:prevYear AS VARCHAR) || '-' || LPAD(CAST(:prevMonth AS VARCHAR), 2, '0') || '-01', 'yyyy-MM-dd')))
                        )
                    GROUP BY CAST(EXTRACT(YEAR FROM t.created_at) AS INTEGER), CAST(EXTRACT(MONTH FROM t.created_at) AS INTEGER)
                ),
                formatted_data AS (
                    SELECT
                        CAST("year" AS VARCHAR) AS "year",
                        FORMATDATETIME(PARSEDATETIME('2000-' || LPAD(CAST("month" AS VARCHAR), 2, '0') || '-01', 'yyyy-MM-dd'), 'MMM') AS "month",
                        total_failed AS totalFailed,
                        total_amount AS totalAmount
                    FROM monthly_data
                    UNION ALL
                    SELECT
                        CAST(:year AS VARCHAR),
                        FORMATDATETIME(PARSEDATETIME('2000-' || LPAD(CAST(:month AS VARCHAR), 2, '0') || '-01', 'yyyy-MM-dd'), 'MMM'),
                        0, 0
                    WHERE NOT EXISTS (
                        SELECT 1 FROM monthly_data
                        WHERE "year" = :year AND "month" = :month
                    )
                    UNION ALL
                    SELECT
                        CAST(:prevYear AS VARCHAR),
                        FORMATDATETIME(PARSEDATETIME('2000-' || LPAD(CAST(:prevMonth AS VARCHAR), 2, '0') || '-01', 'yyyy-MM-dd'), 'MMM'),
                        0, 0
                    WHERE NOT EXISTS (
                        SELECT 1 FROM monthly_data
                        WHERE "year" = :prevYear AND "month" = :prevMonth
                    )
                )
            SELECT * FROM formatted_data
            ORDER BY "year" DESC, "month" DESC
            """, nativeQuery = true)
    List<TransactionMonthlyAmountFailed> findMonthlyFailedByMerchant(
            @Param("merchantId") Long merchantId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("prevYear") Integer prevYear,
            @Param("prevMonth") Integer prevMonth);

    @Query(value = """
            WITH
                yearly_data AS (
                    SELECT
                        CAST(EXTRACT(YEAR FROM t.created_at) AS INTEGER) AS "year",
                        CAST(COUNT(*) AS BIGINT) AS total_failed,
                        CAST(COALESCE(SUM(t.amount), 0) AS BIGINT) AS total_amount
                    FROM transactions t
                    WHERE
                        t.deleted_at IS NULL
                        AND t.payment_status = 'FAILED'
                        AND t.merchant_id = :merchantId
                        AND (EXTRACT(YEAR FROM t.created_at) = :year
                             OR EXTRACT(YEAR FROM t.created_at) = :year - 1)
                    GROUP BY CAST(EXTRACT(YEAR FROM t.created_at) AS INTEGER)
                ),
                formatted_data AS (
                    SELECT
                        CAST("year" AS VARCHAR) AS "year",
                        total_failed AS totalFailed,
                        total_amount AS totalAmount
                    FROM yearly_data
                    UNION ALL
                    SELECT CAST(:year AS VARCHAR), 0, 0 WHERE NOT EXISTS (SELECT 1 FROM yearly_data WHERE "year" = :year)
                    UNION ALL
                    SELECT CAST((:year - 1) AS VARCHAR), 0, 0 WHERE NOT EXISTS (SELECT 1 FROM yearly_data WHERE "year" = :year - 1)
                )
            SELECT * FROM formatted_data
            ORDER BY "year" DESC
            """, nativeQuery = true)
    List<TransactionYearlyAmountFailed> findYearlyFailedByMerchant(
            @Param("merchantId") Long merchantId,
            @Param("year") Integer year);
}
