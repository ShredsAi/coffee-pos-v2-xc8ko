-- Additional performance optimization indexes for the Product Management & Availability Shred
-- These indexes support high-frequency queries and improve overall system performance

-- =====================================================
-- COMPOSITE INDEXES FOR COMPLEX QUERIES
-- =====================================================

-- Index for paginated product listing with category filtering
CREATE INDEX IF NOT EXISTS idx_products_category_active_name ON products (category_id, is_active, name)
WHERE is_active = true;

-- Index for product search with type and category filtering
CREATE INDEX IF NOT EXISTS idx_products_type_category_active ON products (product_type, category_id, is_active)
WHERE is_active = true;

-- Index for recent product updates (for cache invalidation)
CREATE INDEX IF NOT EXISTS idx_products_last_modified_desc ON products (last_modified DESC, is_active)
WHERE is_active = true;

-- =====================================================
-- CATEGORY HIERARCHY OPTIMIZATION INDEXES
-- =====================================================

-- Index for category hierarchy traversal (parent to children)
CREATE INDEX IF NOT EXISTS idx_categories_parent_display_active ON product_categories (parent_category_id, display_order, is_active)
WHERE is_active = true;

-- Index for root categories (frequently accessed)
CREATE INDEX IF NOT EXISTS idx_categories_root_display ON product_categories (display_order, name)
WHERE parent_category_id IS NULL AND is_active = true;

-- Index for category name lookups (case-insensitive)
CREATE INDEX IF NOT EXISTS idx_categories_name_lower ON product_categories (LOWER(name))
WHERE is_active = true;

-- =====================================================
-- VARIANT OPTIMIZATION INDEXES
-- =====================================================

-- Index for product variant listing with availability
CREATE INDEX IF NOT EXISTS idx_variants_product_available_size ON product_variants (product_id, is_available, size_name);

-- Index for price-based variant queries
CREATE INDEX IF NOT EXISTS idx_variants_product_price_modifier ON product_variants (product_id, price_modifier_amount)
WHERE is_available = true;

-- Index for volume-based variant filtering
CREATE INDEX IF NOT EXISTS idx_variants_volume_available ON product_variants (volume_in_oz, volume_in_ml, is_available)
WHERE is_available = true AND (volume_in_oz IS NOT NULL OR volume_in_ml IS NOT NULL);

-- =====================================================
-- AVAILABILITY OPTIMIZATION INDEXES
-- =====================================================

-- Index for real-time availability checks (most frequently used)
CREATE INDEX IF NOT EXISTS idx_availability_location_product_status ON product_availability (location_id, product_id, is_available, estimated_quantity);

-- Index for availability dashboard queries
CREATE INDEX IF NOT EXISTS idx_availability_location_available_updated ON product_availability (location_id, is_available, last_updated DESC);

-- Index for restock management queries
CREATE INDEX IF NOT EXISTS idx_availability_restock_location ON product_availability (estimated_restock_time, location_id)
WHERE estimated_restock_time IS NOT NULL;

-- Index for inventory sync operations
CREATE INDEX IF NOT EXISTS idx_availability_product_locations ON product_availability (product_id, location_id, last_updated);

-- Index for out-of-stock reporting
CREATE INDEX IF NOT EXISTS idx_availability_unavailable_reason_location ON product_availability (unavailable_reason, location_id)
WHERE is_available = false;

-- =====================================================
-- CROSS-TABLE RELATIONSHIP INDEXES
-- =====================================================

-- Index for product-category-availability joins (dashboard queries)
CREATE INDEX IF NOT EXISTS idx_products_category_for_availability ON products (category_id, product_id, is_active)
WHERE is_active = true;

-- Index for variant-product joins with availability
CREATE INDEX IF NOT EXISTS idx_variants_product_available_join ON product_variants (product_id, variant_id, is_available);

-- =====================================================
-- FULL-TEXT SEARCH INDEXES (for product search)
-- =====================================================

-- GIN index for product name and description search
CREATE INDEX IF NOT EXISTS idx_products_fulltext_search ON products 
USING gin(to_tsvector('english', name || ' ' || COALESCE(description, '')))
WHERE is_active = true;

-- =====================================================
-- PARTIAL INDEXES FOR SPECIFIC USE CASES
-- =====================================================

-- Index for active products only (reduces index size significantly)
CREATE INDEX IF NOT EXISTS idx_products_active_only ON products (product_id, name, category_id)
WHERE is_active = true;

-- Index for available variants only
CREATE INDEX IF NOT EXISTS idx_variants_available_only ON product_variants (variant_id, product_id, size_name)
WHERE is_available = true;

-- Index for products with availability issues (monitoring)
CREATE INDEX IF NOT EXISTS idx_availability_issues ON product_availability (product_id, location_id, unavailable_reason, last_updated)
WHERE is_available = false;

-- =====================================================
-- PERFORMANCE STATISTICS UPDATES
-- =====================================================

-- Update table statistics for better query planning
ANALYZE products;
ANALYZE product_categories;
ANALYZE product_variants;
ANALYZE product_availability;

-- =====================================================
-- INDEX USAGE MONITORING VIEW
-- =====================================================

-- Create view to monitor index usage (helpful for performance tuning)
CREATE OR REPLACE VIEW v_index_usage_stats AS
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_tup_read,
    idx_tup_fetch,
    idx_scan,
    CASE 
        WHEN idx_scan = 0 THEN 'UNUSED'
        WHEN idx_scan < 10 THEN 'LOW_USAGE'
        WHEN idx_scan < 100 THEN 'MODERATE_USAGE'
        ELSE 'HIGH_USAGE'
    END AS usage_category
FROM pg_stat_user_indexes
WHERE schemaname = 'public'
AND (tablename LIKE 'product%' OR tablename LIKE '%availability%')
ORDER BY idx_scan DESC;

COMMENT ON VIEW v_index_usage_stats IS 'Monitoring view for index usage statistics to optimize database performance';

-- =====================================================
-- MAINTENANCE RECOMMENDATIONS
-- =====================================================

-- Create a maintenance function for regular index optimization
CREATE OR REPLACE FUNCTION maintain_product_indexes()
RETURNS TEXT AS $$
DECLARE
    result TEXT := 'Index maintenance completed: ';
BEGIN
    -- Reindex most frequently used indexes
    REINDEX INDEX CONCURRENTLY idx_products_category_active_name;
    REINDEX INDEX CONCURRENTLY idx_availability_location_product_status;
    REINDEX INDEX CONCURRENTLY idx_variants_product_available_size;
    
    result := result || 'Critical indexes reindexed, ';
    
    -- Update statistics
    ANALYZE products;
    ANALYZE product_availability;
    ANALYZE product_variants;
    
    result := result || 'Statistics updated.';
    
    RETURN result;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 'Index maintenance failed: ' || SQLERRM;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION maintain_product_indexes() IS 'Maintenance function to reindex critical indexes and update statistics';