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
            WITH
                monthly_totals AS (
                    SELECT
                        CAST(EXTRACT(YEAR FROM o.created_at) AS VARCHAR) AS "year",
                        CAST(EXTRACT(MONTH FROM o.created_at) AS INTEGER) AS month_num,
                        FORMATDATETIME(PARSEDATETIME('2000-' || LPAD(CAST(EXTRACT(MONTH FROM o.created_at) AS VARCHAR), 2, '0') || '-01', 'yyyy-MM-dd'), 'MMMM') AS month_name,
                        CAST(COALESCE(SUM(o.total_price), 0) AS BIGINT) AS totalRevenue
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
                    GROUP BY
                        CAST(EXTRACT(YEAR FROM o.created_at) AS VARCHAR),
                        CAST(EXTRACT(MONTH FROM o.created_at) AS INTEGER),
                        FORMATDATETIME(PARSEDATETIME('2000-' || LPAD(CAST(EXTRACT(MONTH FROM o.created_at) AS VARCHAR), 2, '0') || '-01', 'yyyy-MM-dd'), 'MMMM')
                ),
                all_months AS (
                    SELECT
                        CAST(:startYear AS VARCHAR) AS "year",
                        CAST(:startMonth AS INTEGER) AS month_num,
                        FORMATDATETIME(PARSEDATETIME('2000-' || LPAD(CAST(:startMonth AS VARCHAR), 2, '0') || '-01', 'yyyy-MM-dd'), 'MMMM') AS month_name
                    UNION
                    SELECT
                        CAST(:endYear AS VARCHAR) AS "year",
                        CAST(:endMonth AS INTEGER) AS month_num,
                        FORMATDATETIME(PARSEDATETIME('2000-' || LPAD(CAST(:endMonth AS VARCHAR), 2, '0') || '-01', 'yyyy-MM-dd'), 'MMMM') AS month_name
                )
            SELECT
                am."year" AS "year",
                am.month_name AS "month",
                CAST(COALESCE(mt.totalRevenue, 0) AS BIGINT) AS totalRevenue
            FROM all_months am
            LEFT JOIN monthly_totals mt
                   ON am."year" = mt."year" AND am.month_num = mt.month_num
            ORDER BY CAST(am."year" AS INTEGER) DESC, am.month_num DESC
            """, nativeQuery = true)
    List<CategoriesMonthlyTotalPrice> findMonthlyTotalPriceByMerchant(
            @Param("merchantId") Integer merchantId,
            @Param("startYear") Integer startYear,
            @Param("startMonth") Integer startMonth,
            @Param("endYear") Integer endYear,
            @Param("endMonth") Integer endMonth);

    @Query(value = """
            WITH
                yearly_data AS (
                    SELECT
                        CAST(EXTRACT(YEAR FROM o.created_at) AS VARCHAR) AS "year",
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
                        AND (
                            EXTRACT(YEAR FROM o.created_at) = :year
                            OR EXTRACT(YEAR FROM o.created_at) = :year - 1
                        )
                    GROUP BY EXTRACT(YEAR FROM o.created_at)
                ),
                all_years AS (
                    SELECT :year AS "year"
                    UNION
                    SELECT :year - 1 AS "year"
                )
            SELECT
                CAST(a."year" AS VARCHAR) AS "year",
                CAST(COALESCE(yd.totalRevenue, 0) AS BIGINT) AS totalRevenue
            FROM all_years a
            LEFT JOIN yearly_data yd ON CAST(a."year" AS VARCHAR) = yd."year"
            ORDER BY a."year" DESC
            """, nativeQuery = true)
    List<CategoriesYearlyTotalPrice> findYearlyTotalPriceByMerchant(
            @Param("merchantId") Integer merchantId,
            @Param("year") Integer year);
}
