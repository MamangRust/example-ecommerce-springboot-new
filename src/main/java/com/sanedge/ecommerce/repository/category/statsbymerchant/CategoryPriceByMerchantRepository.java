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
            WITH date_range AS (
                SELECT
                    date_trunc('year', make_date(:year, 1, 1)) AS start_date,
                    date_trunc('year', make_date(:year, 1, 1)) + interval '1 year' - interval '1 day' AS end_date
            ),
            monthly_category_stats AS (
                SELECT
                    c.category_id,
                    c.name AS category_name,
                    date_trunc('month', o.created_at) AS activity_month,
                    COUNT(DISTINCT o.order_id) AS order_count,
                    SUM(oi.quantity) AS items_sold,
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
                    AND o.created_at BETWEEN (SELECT start_date FROM date_range)
                                        AND (SELECT end_date FROM date_range)
                GROUP BY
                    c.category_id, c.name, activity_month
            )
            SELECT
                TO_CHAR(mcs.activity_month, 'Mon') AS month,
                mcs.category_id,
                mcs.category_name,
                mcs.order_count,
                mcs.items_sold,
                mcs.totalRevenue
            FROM
                monthly_category_stats mcs
            ORDER BY
                mcs.activity_month, mcs.totalRevenue DESC
            """, nativeQuery = true)
    List<CategoriesMonthPrice> findMonthlyCategoryStatsByMerchant(
            @Param("merchantId") Integer merchantId,
            @Param("year") Integer year);

    @Query(value = """
            WITH last_five_years AS (
                SELECT
                    c.category_id,
                    c.name AS category_name,
                    EXTRACT(YEAR FROM o.created_at)::text AS year,
                    COUNT(DISTINCT o.order_id) AS order_count,
                    SUM(oi.quantity) AS items_sold,
                    COALESCE(SUM(o.total_price), 0)::bigint AS totalRevenue,
                    COUNT(DISTINCT oi.product_id) AS unique_products_sold
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
                    AND EXTRACT(YEAR FROM o.created_at) BETWEEN (:year - 4) AND :year
                GROUP BY
                    c.category_id, c.name, EXTRACT(YEAR FROM o.created_at)
            )
            SELECT
                year,
                category_id,
                category_name,
                order_count,
                items_sold,
                totalRevenue,
                unique_products_sold
            FROM
                last_five_years
            ORDER BY
                year, totalRevenue DESC
            """, nativeQuery = true)
    List<CategoriesYearPrice> findYearlyCategoryStatsByMerchant(
            @Param("merchantId") Integer merchantId,
            @Param("year") Integer year);
}