package com.sanedge.ecommerce.repository.order.statsbymerchant;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.order.Order;
import com.sanedge.ecommerce.models.order.OrderMonthlyTotalRevenue;
import com.sanedge.ecommerce.models.order.OrderYearlyTotalRevenue;

@Repository
public interface OrderTotalRevenueByMerchantRepository extends JpaRepository<Order, Long> {

    @Query(value = """
            WITH
                monthly_revenue AS (
                    SELECT
                        CAST(EXTRACT(YEAR FROM o.created_at) AS INTEGER) AS "year",
                        CAST(EXTRACT(MONTH FROM o.created_at) AS INTEGER) AS month_num,
                        CAST(COALESCE(SUM(o.total_price), 0) AS BIGINT) AS total_revenue
                    FROM orders o
                    JOIN order_items oi ON o.order_id = oi.order_id
                    WHERE
                        o.deleted_at IS NULL
                        AND oi.deleted_at IS NULL
                        AND o.merchant_id = :merchantId
                        AND (
                            (EXTRACT(YEAR FROM o.created_at) = :year1 AND EXTRACT(MONTH FROM o.created_at) = :month1)
                            OR (EXTRACT(YEAR FROM o.created_at) = :year2 AND EXTRACT(MONTH FROM o.created_at) = :month2)
                        )
                    GROUP BY
                        CAST(EXTRACT(YEAR FROM o.created_at) AS INTEGER),
                        CAST(EXTRACT(MONTH FROM o.created_at) AS INTEGER)
                ),
                all_months AS (
                    SELECT
                        CAST(:year1 AS VARCHAR) AS "year",
                        :month1 AS month_num,
                        FORMATDATETIME(PARSEDATETIME('2000-' || LPAD(CAST(:month1 AS VARCHAR), 2, '0') || '-01', 'yyyy-MM-dd'), 'MMMM') AS month_name
                    UNION
                    SELECT
                        CAST(:year2 AS VARCHAR) AS "year",
                        :month2 AS month_num,
                        FORMATDATETIME(PARSEDATETIME('2000-' || LPAD(CAST(:month2 AS VARCHAR), 2, '0') || '-01', 'yyyy-MM-dd'), 'MMMM') AS month_name
                )
            SELECT
                am."year" AS "year",
                am.month_name AS "month",
                CAST(COALESCE(mr.total_revenue, 0) AS BIGINT) AS totalRevenue
            FROM all_months am
            LEFT JOIN monthly_revenue mr
                   ON CAST(am."year" AS INTEGER) = mr."year"
                  AND am.month_num = mr.month_num
            ORDER BY am."year" DESC, am.month_num DESC
            """, nativeQuery = true)
    List<OrderMonthlyTotalRevenue> findMonthlyTotalRevenueByMerchant(
            @Param("merchantId") Long merchantId,
            @Param("year1") Integer year1,
            @Param("month1") Integer month1,
            @Param("year2") Integer year2,
            @Param("month2") Integer month2);

    @Query(value = """
            WITH
                yearly_revenue AS (
                    SELECT
                        CAST(EXTRACT(YEAR FROM o.created_at) AS INTEGER) AS "year",
                        CAST(COALESCE(SUM(o.total_price), 0) AS BIGINT) AS total_revenue
                    FROM orders o
                    JOIN order_items oi ON o.order_id = oi.order_id
                    WHERE
                        o.deleted_at IS NULL
                        AND oi.deleted_at IS NULL
                        AND o.merchant_id = :merchantId
                        AND (
                            EXTRACT(YEAR FROM o.created_at) = :year
                            OR EXTRACT(YEAR FROM o.created_at) = :year - 1
                        )
                    GROUP BY CAST(EXTRACT(YEAR FROM o.created_at) AS INTEGER)
                ),
                all_years AS (
                    SELECT :year AS "year"
                    UNION
                    SELECT :year - 1 AS "year"
                )
            SELECT
                CAST(ay."year" AS VARCHAR) AS "year",
                CAST(COALESCE(yr.total_revenue, 0) AS BIGINT) AS totalRevenue
            FROM all_years ay
            LEFT JOIN yearly_revenue yr ON ay."year" = yr."year"
            ORDER BY ay."year" DESC
            """, nativeQuery = true)
    List<OrderYearlyTotalRevenue> findYearlyTotalRevenueByMerchant(
            @Param("merchantId") Long merchantId,
            @Param("year") Integer year);
}
