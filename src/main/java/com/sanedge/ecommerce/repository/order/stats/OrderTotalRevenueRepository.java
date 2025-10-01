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
public interface OrderTotalRevenueRepository extends JpaRepository<Order, Long> {
    @Query(value = """
                WITH monthly_stats AS (
                    SELECT
                        TO_CHAR(o.created_at, 'FMMonth') AS month,
                        COUNT(DISTINCT o.order_id)::INT AS orderCount,
                        SUM(o.total_price)::BIGINT AS totalRevenue,
                        SUM(oi.quantity)::INT AS totalItemsSold
                    FROM orders o
                    JOIN order_items oi ON o.order_id = oi.order_id
                    WHERE o.deleted_at IS NULL
                      AND oi.deleted_at IS NULL
                      AND (
                          (EXTRACT(YEAR FROM o.created_at)::INT * 100 + EXTRACT(MONTH FROM o.created_at)::INT BETWEEN :start1 AND :end1)
                          OR (EXTRACT(YEAR FROM o.created_at)::INT * 100 + EXTRACT(MONTH FROM o.created_at)::INT BETWEEN :start2 AND :end2)
                      )
                    GROUP BY TO_CHAR(o.created_at, 'FMMonth')
                )
                SELECT month, orderCount, totalRevenue, totalItemsSold
                FROM monthly_stats
                ORDER BY TO_DATE(month, 'FMMonth')
            """, nativeQuery = true)
    List<OrderMonthly> findMonthlyStats(
            @Param("start1") Integer start1,
            @Param("end1") Integer end1,
            @Param("start2") Integer start2,
            @Param("end2") Integer end2);

    @Query(value = """
                WITH yearly_stats AS (
                    SELECT
                        EXTRACT(YEAR FROM o.created_at)::TEXT AS year,
                        COUNT(DISTINCT o.order_id) AS "orderCount",
                        SUM(o.total_price)::BIGINT AS "totalRevenue",
                        SUM(oi.quantity) AS "totalItemsSold",
                        COUNT(DISTINCT o.user_id) AS "activeCashiers",
                        COUNT(DISTINCT oi.product_id) AS "uniqueProductsSold"
                    FROM orders o
                    JOIN order_items oi ON o.order_id = oi.order_id
                    WHERE o.deleted_at IS NULL
                      AND oi.deleted_at IS NULL
                      AND (EXTRACT(YEAR FROM o.created_at) = :year
                           OR EXTRACT(YEAR FROM o.created_at) = :year - 1)
                    GROUP BY EXTRACT(YEAR FROM o.created_at)
                )
                SELECT year, "orderCount", "totalRevenue", "totalItemsSold", "activeCashiers", "uniqueProductsSold"
                FROM yearly_stats
                ORDER BY year DESC
            """, nativeQuery = true)
    List<OrderYearly> findYearlyStats(@Param("year") Integer year);
}
