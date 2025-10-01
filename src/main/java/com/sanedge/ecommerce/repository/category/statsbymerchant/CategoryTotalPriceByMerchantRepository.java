package com.sanedge.ecommerce.repository.category.statsbymerchant;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.category.CategoriesMonthlyTotalPrice;
import com.sanedge.ecommerce.models.category.CategoriesYearlyTotalPrice;
import com.sanedge.ecommerce.models.category.Category;

@Repository
public interface CategoryTotalPriceByMerchantRepository extends JpaRepository<Category, Long> {

    @Query(value = """
            WITH monthly_totals AS (
                    SELECT
                        EXTRACT(YEAR FROM o.created_at)::TEXT AS year,
                        EXTRACT(MONTH FROM o.created_at)::INTEGER AS month_num,
                        TO_CHAR(o.created_at, 'FMMonth') AS month_name,
                        COALESCE(SUM(o.total_price), 0)::bigint AS totalRevenue
                    FROM orders o
                    JOIN order_items oi ON o.order_id = oi.order_id
                    JOIN products p ON oi.product_id = p.product_id
                    JOIN categories c ON p.category_id = c.category_id
                    WHERE
                        o.deleted_at IS NULL
                        AND oi.deleted_at IS NULL
                        AND o.merchant_id = :merchantId
                        AND (
                            (EXTRACT(YEAR FROM o.created_at) = :startYear AND EXTRACT(MONTH FROM o.created_at) = :startMonth)
                            OR
                            (EXTRACT(YEAR FROM o.created_at) = :endYear AND EXTRACT(MONTH FROM o.created_at) = :endMonth)
                        )
                    GROUP BY year, month_num, month_name
                ),
                all_months AS (
                    SELECT :startYear::TEXT AS year, :startMonth::INTEGER AS month_num,
                           TO_CHAR(MAKE_DATE(:startYear, :startMonth, 1), 'FMMonth') AS month_name
                    UNION
                    SELECT :endYear::TEXT AS year, :endMonth::INTEGER AS month_num,
                           TO_CHAR(MAKE_DATE(:endYear, :endMonth, 1), 'FMMonth') AS month_name
                )
                SELECT
                    am.year AS year,
                    am.month AS month,
                    COALESCE(mt.totalRevenue, 0) AS totalRevenue
                FROM all_months am
                LEFT JOIN monthly_totals mt
                    ON am.year = mt.year AND am.month_num = mt.month_num
                ORDER BY am.year::INT DESC, am.month_num DESC
                """, nativeQuery = true)
    List<CategoriesMonthlyTotalPrice> findMonthlyTotalPriceByMerchant(
            @Param("merchantId") Integer merchantId,
            @Param("startYear") Integer startYear,
            @Param("startMonth") Integer startMonth,
            @Param("endYear") Integer endYear,
            @Param("endMonth") Integer endMonth);

    @Query(value = """
            WITH yearly_data AS (
                SELECT
                    EXTRACT(YEAR FROM o.created_at)::text AS year,
                    COALESCE(SUM(o.total_price), 0)::bigint AS totalRevenue
                FROM
                    orders o
                JOIN
                    order_items oi ON o.order_id = oi.order_id
                JOIN
                    products p ON oi.product_id = p.product_id
                JOIN
                    categories c ON p.category_id = c.category_id
                WHERE
                    o.deleted_at IS NULL
                    AND oi.deleted_at IS NULL
                    AND p.deleted_at IS NULL
                    AND c.deleted_at IS NULL
                    AND o.merchant_id = :merchantId
                    AND (
                        EXTRACT(YEAR FROM o.created_at) = :year
                        OR EXTRACT(YEAR FROM o.created_at) = :year - 1
                    )
                GROUP BY
                    EXTRACT(YEAR FROM o.created_at)
            ),
            all_years AS (
                SELECT :year AS year
                UNION
                SELECT :year - 1 AS year
            )
            SELECT
                a.year::text AS year,
                COALESCE(yd.totalRevenue, 0)::bigint AS totalRevenue
            FROM
                all_years a
            LEFT JOIN
                yearly_data yd ON a.year = yd.year
            ORDER BY
                a.year DESC
            """, nativeQuery = true)
    List<CategoriesYearlyTotalPrice> findYearlyTotalPriceByMerchant(
            @Param("merchantId") Integer merchantId,
            @Param("year") Integer year);
}
