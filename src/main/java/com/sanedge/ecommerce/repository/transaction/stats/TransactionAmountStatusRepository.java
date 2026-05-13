package com.sanedge.ecommerce.repository.transaction.stats;

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
public interface TransactionAmountStatusRepository extends JpaRepository<Transaction, Long> {
    @Query(value = """
            WITH monthly_data AS (
                SELECT
                    CAST(EXTRACT(YEAR FROM t.created_at) AS INTEGER) AS "year",
                    CAST(EXTRACT(MONTH FROM t.created_at) AS INTEGER) AS "month",
                    COUNT(*) AS total_success,
                    CAST(COALESCE(SUM(t.amount), 0) AS BIGINT) AS total_amount
                FROM transactions t
                WHERE
                    t.deleted_at IS NULL
                    AND t.payment_status = 'SUCCESS'
                    AND (
                        (t.created_at >= PARSEDATETIME(CAST(:year AS VARCHAR) || '-' || CAST(:month AS VARCHAR) || '-01', 'yyyy-M-dd')
                         AND t.created_at < DATEADD('MONTH', 1, PARSEDATETIME(CAST(:year AS VARCHAR) || '-' || CAST(:month AS VARCHAR) || '-01', 'yyyy-M-dd')))
                        OR
                        (t.created_at >= PARSEDATETIME(CAST(:prevYear AS VARCHAR) || '-' || CAST(:prevMonth AS VARCHAR) || '-01', 'yyyy-M-dd')
                         AND t.created_at < DATEADD('MONTH', 1, PARSEDATETIME(CAST(:prevYear AS VARCHAR) || '-' || CAST(:prevMonth AS VARCHAR) || '-01', 'yyyy-M-dd')))
                    )
                GROUP BY CAST(EXTRACT(YEAR FROM t.created_at) AS INTEGER), CAST(EXTRACT(MONTH FROM t.created_at) AS INTEGER)
            ),
            formatted_data AS (
                SELECT
                    CAST("year" AS VARCHAR) AS "year",
                    FORMATDATETIME(PARSEDATETIME('2000-' || LPAD(CAST("month" AS VARCHAR), 2, '0') || '-01', 'yyyy-MM-dd'), 'MMM') AS "month",
                    CAST(total_success AS BIGINT) AS totalSuccess,
                    CAST(total_amount AS BIGINT) AS totalAmount,
                    "year" AS "sort_year",
                    "month" AS "sort_month"
                FROM monthly_data
                UNION ALL
                SELECT CAST(:year AS VARCHAR),
                       FORMATDATETIME(PARSEDATETIME(CAST(:year AS VARCHAR) || '-' || CAST(:month AS VARCHAR) || '-01', 'yyyy-M-dd'), 'MMM'),
                       CAST(0 AS BIGINT), CAST(0 AS BIGINT), :year, :month
                WHERE NOT EXISTS (
                    SELECT 1 FROM monthly_data
                    WHERE "year" = :year AND "month" = :month
                )
                UNION ALL
                SELECT CAST(:prevYear AS VARCHAR),
                       FORMATDATETIME(PARSEDATETIME(CAST(:prevYear AS VARCHAR) || '-' || CAST(:prevMonth AS VARCHAR) || '-01', 'yyyy-M-dd'), 'MMM'),
                       CAST(0 AS BIGINT), CAST(0 AS BIGINT), :prevYear, :prevMonth
                WHERE NOT EXISTS (
                    SELECT 1 FROM monthly_data
                    WHERE "year" = :prevYear AND "month" = :prevMonth
                )
            )
            SELECT "year", "month", totalSuccess, totalAmount FROM formatted_data
            ORDER BY "sort_year" DESC, "sort_month" DESC
            """, nativeQuery = true)
    List<TransactionMonthlyAmountSuccess> findMonthlyTransactionSuccess(
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("prevYear") Integer prevYear,
            @Param("prevMonth") Integer prevMonth);

    @Query(value = """
            WITH yearly_data AS (
                SELECT
                    CAST(EXTRACT(YEAR FROM t.created_at) AS INTEGER) AS "year",
                    COUNT(*) AS total_success,
                    CAST(COALESCE(SUM(t.amount), 0) AS BIGINT) AS total_amount
                FROM transactions t
                WHERE
                    t.deleted_at IS NULL
                    AND t.payment_status = 'SUCCESS'
                    AND (EXTRACT(YEAR FROM t.created_at) = :year
                         OR EXTRACT(YEAR FROM t.created_at) = :year - 1)
                GROUP BY CAST(EXTRACT(YEAR FROM t.created_at) AS INTEGER)
            ),
            formatted_data AS (
                SELECT
                    CAST("year" AS VARCHAR) AS "year",
                    CAST(total_success AS BIGINT) AS totalSuccess,
                    CAST(total_amount AS BIGINT) AS totalAmount
                FROM yearly_data
                UNION ALL
                SELECT CAST(:year AS VARCHAR), CAST(0 AS BIGINT), CAST(0 AS BIGINT) WHERE NOT EXISTS (SELECT 1 FROM yearly_data WHERE "year" = :year)
                UNION ALL
                SELECT CAST((:year - 1) AS VARCHAR), CAST(0 AS BIGINT), CAST(0 AS BIGINT) WHERE NOT EXISTS (SELECT 1 FROM yearly_data WHERE "year" = :year - 1)
            )
            SELECT * FROM formatted_data
            ORDER BY "year" DESC
            """, nativeQuery = true)
    List<TransactionYearlyAmountSuccess> findYearlyTransactionSuccess(@Param("year") Integer year);

    @Query(value = """
            WITH monthly_data AS (
                SELECT
                    CAST(EXTRACT(YEAR FROM t.created_at) AS INTEGER) AS "year",
                    CAST(EXTRACT(MONTH FROM t.created_at) AS INTEGER) AS "month",
                    COUNT(*) AS total_failed,
                    CAST(COALESCE(SUM(t.amount), 0) AS BIGINT) AS total_amount
                FROM transactions t
                WHERE
                    t.deleted_at IS NULL
                    AND t.payment_status = 'FAILED'
                    AND (
                        (t.created_at >= PARSEDATETIME(CAST(:year AS VARCHAR) || '-' || CAST(:month AS VARCHAR) || '-01', 'yyyy-M-dd')
                         AND t.created_at < DATEADD('MONTH', 1, PARSEDATETIME(CAST(:year AS VARCHAR) || '-' || CAST(:month AS VARCHAR) || '-01', 'yyyy-M-dd')))
                        OR
                        (t.created_at >= PARSEDATETIME(CAST(:prevYear AS VARCHAR) || '-' || CAST(:prevMonth AS VARCHAR) || '-01', 'yyyy-M-dd')
                         AND t.created_at < DATEADD('MONTH', 1, PARSEDATETIME(CAST(:prevYear AS VARCHAR) || '-' || CAST(:prevMonth AS VARCHAR) || '-01', 'yyyy-M-dd')))
                    )
                GROUP BY CAST(EXTRACT(YEAR FROM t.created_at) AS INTEGER), CAST(EXTRACT(MONTH FROM t.created_at) AS INTEGER)
            ),
            formatted_data AS (
                SELECT
                    CAST("year" AS VARCHAR) AS "year",
                    FORMATDATETIME(PARSEDATETIME('2000-' || LPAD(CAST("month" AS VARCHAR), 2, '0') || '-01', 'yyyy-MM-dd'), 'MMM') AS "month",
                    CAST(total_failed AS BIGINT) AS totalFailed,
                    CAST(total_amount AS BIGINT) AS totalAmount,
                    "year" AS "sort_year",
                    "month" AS "sort_month"
                FROM monthly_data
                UNION ALL
                SELECT CAST(:year AS VARCHAR),
                       FORMATDATETIME(PARSEDATETIME(CAST(:year AS VARCHAR) || '-' || CAST(:month AS VARCHAR) || '-01', 'yyyy-M-dd'), 'MMM'),
                       CAST(0 AS BIGINT), CAST(0 AS BIGINT), :year, :month
                WHERE NOT EXISTS (
                    SELECT 1 FROM monthly_data
                    WHERE "year" = :year AND "month" = :month
                )
                UNION ALL
                SELECT CAST(:prevYear AS VARCHAR),
                       FORMATDATETIME(PARSEDATETIME(CAST(:prevYear AS VARCHAR) || '-' || CAST(:prevMonth AS VARCHAR) || '-01', 'yyyy-M-dd'), 'MMM'),
                       CAST(0 AS BIGINT), CAST(0 AS BIGINT), :prevYear, :prevMonth
                WHERE NOT EXISTS (
                    SELECT 1 FROM monthly_data
                    WHERE "year" = :prevYear AND "month" = :prevMonth
                )
            )
            SELECT "year", "month", totalFailed, totalAmount FROM formatted_data
            ORDER BY "sort_year" DESC, "sort_month" DESC
            """, nativeQuery = true)
    List<TransactionMonthlyAmountFailed> findMonthlyTransactionFailed(
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("prevYear") Integer prevYear,
            @Param("prevMonth") Integer prevMonth);

    @Query(value = """
            WITH yearly_data AS (
                SELECT
                    CAST(EXTRACT(YEAR FROM t.created_at) AS INTEGER) AS "year",
                    COUNT(*) AS total_failed,
                    CAST(COALESCE(SUM(t.amount), 0) AS BIGINT) AS total_amount
                FROM transactions t
                WHERE
                    t.deleted_at IS NULL
                    AND t.payment_status = 'FAILED'
                    AND (EXTRACT(YEAR FROM t.created_at) = :year
                         OR EXTRACT(YEAR FROM t.created_at) = :year - 1)
                GROUP BY CAST(EXTRACT(YEAR FROM t.created_at) AS INTEGER)
            ),
            formatted_data AS (
                SELECT
                    CAST("year" AS VARCHAR) AS "year",
                    CAST(total_failed AS BIGINT) AS totalFailed,
                    CAST(total_amount AS BIGINT) AS totalAmount
                FROM yearly_data
                UNION ALL
                SELECT CAST(:year AS VARCHAR), CAST(0 AS BIGINT), CAST(0 AS BIGINT) WHERE NOT EXISTS (SELECT 1 FROM yearly_data WHERE "year" = :year)
                UNION ALL
                SELECT CAST((:year - 1) AS VARCHAR), CAST(0 AS BIGINT), CAST(0 AS BIGINT) WHERE NOT EXISTS (SELECT 1 FROM yearly_data WHERE "year" = :year - 1)
            )
            SELECT * FROM formatted_data
            ORDER BY "year" DESC
            """, nativeQuery = true)
    List<TransactionYearlyAmountFailed> findYearlyTransactionFailed(@Param("year") Integer year);
}
