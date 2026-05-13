package com.sanedge.ecommerce.repository.order.stats;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.order.Order;
import com.sanedge.ecommerce.models.order.OrderMonthly;
import com.sanedge.ecommerce.models.order.OrderYearly;

@Repository
public interface OrderSoldOutRepository extends JpaRepository<Order, Long> {
    @Query(value = """
            WITH date_range AS (
                SELECT
                    PARSEDATETIME(CAST(:yearMonth AS VARCHAR) || CASE WHEN LENGTH(CAST(:yearMonth AS VARCHAR)) = 4 THEN '0101' ELSE '01' END, 'yyyyMMdd') AS start_date,
                    DATEADD('DAY', -1, DATEADD('YEAR', 1, PARSEDATETIME(CAST(:yearMonth AS VARCHAR) || CASE WHEN LENGTH(CAST(:yearMonth AS VARCHAR)) = 4 THEN '0101' ELSE '01' END, 'yyyyMMdd'))) AS end_date
            ),
            monthly_orders AS (
                SELECT
                    PARSEDATETIME(FORMATDATETIME(o.created_at, 'yyyy-MM-01'), 'yyyy-MM-dd') AS activity_month,
                    CAST(COUNT(o.order_id) AS INTEGER) AS order_count,
                    CAST(SUM(o.total_price) AS BIGINT) AS total_revenue,
                    CAST(SUM(oi.quantity) AS INTEGER) AS total_items_sold
                FROM orders o
                JOIN order_items oi ON o.order_id = oi.order_id
                WHERE o.deleted_at IS NULL
                  AND oi.deleted_at IS NULL
                  AND o.created_at BETWEEN (SELECT start_date FROM date_range)
                                       AND (SELECT end_date FROM date_range)
                GROUP BY PARSEDATETIME(FORMATDATETIME(o.created_at, 'yyyy-MM-01'), 'yyyy-MM-dd')
            )
            SELECT
                FORMATDATETIME(mo.activity_month, 'MMM') AS "month",
                mo.order_count AS orderCount,
                mo.total_revenue AS totalRevenue,
                mo.total_items_sold AS totalItemsSold
            FROM monthly_orders mo
            ORDER BY mo.activity_month
            """, nativeQuery = true)
    List<OrderMonthly> findMonthlyOrders(@Param("yearMonth") Integer yearMonth);

    @Query(value = """
            WITH last_five_years AS (
                SELECT
                    CAST(EXTRACT(YEAR FROM o.created_at) AS VARCHAR) AS "year",
                    CAST(COUNT(o.order_id) AS INTEGER) AS order_count,
                    CAST(SUM(o.total_price) AS BIGINT) AS total_revenue,
                    CAST(SUM(oi.quantity) AS INTEGER) AS total_items_sold,
                    CAST(COUNT(DISTINCT o.user_id) AS INTEGER) AS active_cashiers,
                    CAST(COUNT(DISTINCT oi.product_id) AS INTEGER) AS unique_products_sold
                FROM orders o
                JOIN order_items oi ON o.order_id = oi.order_id
                WHERE o.deleted_at IS NULL
                  AND oi.deleted_at IS NULL
                  AND EXTRACT(YEAR FROM o.created_at) BETWEEN EXTRACT(YEAR FROM PARSEDATETIME(CAST(:yearMonth AS VARCHAR) || CASE WHEN LENGTH(CAST(:yearMonth AS VARCHAR)) = 4 THEN '0101' ELSE '01' END, 'yyyyMMdd')) - 4
                                                           AND EXTRACT(YEAR FROM PARSEDATETIME(CAST(:yearMonth AS VARCHAR) || CASE WHEN LENGTH(CAST(:yearMonth AS VARCHAR)) = 4 THEN '0101' ELSE '01' END, 'yyyyMMdd'))
                GROUP BY EXTRACT(YEAR FROM o.created_at)
            )
            SELECT
                "year" AS "year",
                order_count AS orderCount,
                total_revenue AS totalRevenue,
                total_items_sold AS totalItemsSold,
                active_cashiers AS activeCashiers,
                unique_products_sold AS uniqueProductsSold
            FROM last_five_years
            ORDER BY "year"
            """, nativeQuery = true)
    List<OrderYearly> findYearlyOrders(@Param("yearMonth") Integer yearMonth);
}