package com.sanedge.ecommerce.repository.category.statsbymerchant;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.category.CategoriesMonthPrice;
import com.sanedge.ecommerce.models.category.CategoriesYearPrice;
import com.sanedge.ecommerce.models.category.Category;

@Repository
public interface CategoryPriceByMerchantRepository extends JpaRepository<Category, Long> {

    @Query(value = """
            WITH
                date_range AS (
                    SELECT
                        PARSEDATETIME(CAST(:year AS VARCHAR) || '-01-01', 'yyyy-MM-dd') AS start_date,
                        DATEADD('DAY', -1, DATEADD('YEAR', 1, PARSEDATETIME(CAST(:year AS VARCHAR) || '-01-01', 'yyyy-MM-dd'))) AS end_date
                ),
                monthly_category_stats AS (
                    SELECT
                        c.category_id,
                        c.name AS category_name,
                        PARSEDATETIME(FORMATDATETIME(o.created_at, 'yyyy-MM-01'), 'yyyy-MM-dd') AS activity_month,
                        COUNT(DISTINCT o.order_id) AS order_count,
                        SUM(oi.quantity) AS items_sold,
                        CAST(COALESCE(SUM(o.total_price), 0) AS BIGINT) AS totalRevenue
                    FROM orders o
                    JOIN order_items oi ON o.order_id = oi.order_id
                    JOIN products p ON oi.product_id = p.product_id
                    JOIN categories c ON p.category_id = c.category_id
                    WHERE
                        o.deleted_at IS NULL
                        AND oi.deleted_at IS NULL
                        AND p.deleted_at IS NULL
                        AND c.deleted_at IS NULL
                        AND o.merchant_id = :merchantId
                        AND o.created_at BETWEEN (SELECT start_date FROM date_range) AND (SELECT end_date FROM date_range)
                    GROUP BY c.category_id, c.name, PARSEDATETIME(FORMATDATETIME(o.created_at, 'yyyy-MM-01'), 'yyyy-MM-dd')
                )
            SELECT
                FORMATDATETIME(mcs.activity_month, 'MMM') AS "month",
                mcs.category_id AS categoryId,
                mcs.category_name AS categoryName,
                mcs.order_count AS orderCount,
                mcs.items_sold AS itemsSold,
                mcs.totalRevenue AS totalRevenue
            FROM monthly_category_stats mcs
            ORDER BY mcs.activity_month, mcs.totalRevenue DESC
            """, nativeQuery = true)
    List<CategoriesMonthPrice> findMonthlyCategoryStatsByMerchant(
            @Param("merchantId") Integer merchantId,
            @Param("year") Integer year);

    @Query(value = """
            WITH last_five_years AS (
                SELECT
                    c.category_id,
                    c.name AS category_name,
                    CAST(EXTRACT(YEAR FROM o.created_at) AS VARCHAR) AS "year",
                    COUNT(DISTINCT o.order_id) AS order_count,
                    SUM(oi.quantity) AS items_sold,
                    CAST(COALESCE(SUM(o.total_price), 0) AS BIGINT) AS totalRevenue,
                    COUNT(DISTINCT oi.product_id) AS unique_products_sold
                FROM orders o
                JOIN order_items oi ON o.order_id = oi.order_id
                JOIN products p ON oi.product_id = p.product_id
                JOIN categories c ON p.category_id = c.category_id
                WHERE
                    o.deleted_at IS NULL
                    AND oi.deleted_at IS NULL
                    AND p.deleted_at IS NULL
                    AND c.deleted_at IS NULL
                    AND o.merchant_id = :merchantId
                    AND EXTRACT(YEAR FROM o.created_at) BETWEEN (:year - 4) AND :year
                GROUP BY c.category_id, c.name, CAST(EXTRACT(YEAR FROM o.created_at) AS VARCHAR)
            )
            SELECT
                "year" AS "year",
                category_id AS categoryId,
                category_name AS categoryName,
                order_count AS orderCount,
                items_sold AS itemsSold,
                totalRevenue AS totalRevenue,
                unique_products_sold AS uniqueProductsSold
            FROM last_five_years
            ORDER BY "year", totalRevenue DESC
            """, nativeQuery = true)
    List<CategoriesYearPrice> findYearlyCategoryStatsByMerchant(
            @Param("merchantId") Integer merchantId,
            @Param("year") Integer year);
}