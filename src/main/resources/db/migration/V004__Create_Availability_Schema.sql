-- Create product_availability table for location-specific product availability tracking
-- This table maintains real-time availability status across different store locations

CREATE TABLE product_availability (
    availability_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL,
    location_id UUID NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT true,
    estimated_quantity INTEGER NOT NULL DEFAULT 0 CHECK (estimated_quantity >= 0),
    unavailable_reason VARCHAR(30) CHECK (unavailable_reason IN ('OUT_OF_STOCK', 'EQUIPMENT_DOWN', 'SEASONAL', 'DISCONTINUED', 'MAINTENANCE')),
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estimated_restock_time TIMESTAMP,
    
    -- Foreign key constraint to products table
    CONSTRAINT fk_availability_product FOREIGN KEY (product_id) 
        REFERENCES products(product_id) ON DELETE CASCADE,
    
    -- Ensure unavailable reason is provided when product is not available
    CONSTRAINT check_unavailable_reason_required CHECK (
        (is_available = true) OR 
        (is_available = false AND unavailable_reason IS NOT NULL)
    ),
    
    -- Ensure restock time is in the future when provided
    CONSTRAINT check_restock_time_future CHECK (
        estimated_restock_time IS NULL OR estimated_restock_time > CURRENT_TIMESTAMP
    ),
    
    -- Unique constraint for product-location combination
    CONSTRAINT uk_product_location_availability UNIQUE (product_id, location_id)
);

-- Add comments for documentation
COMMENT ON TABLE product_availability IS 'Location-specific product availability tracking with real-time status updates';
COMMENT ON COLUMN product_availability.availability_id IS 'Unique identifier for availability record';
COMMENT ON COLUMN product_availability.product_id IS 'Foreign key reference to products table';
COMMENT ON COLUMN product_availability.location_id IS 'Store location identifier for multi-location tracking';
COMMENT ON COLUMN product_availability.is_available IS 'Current availability status at the specific location';
COMMENT ON COLUMN product_availability.estimated_quantity IS 'Estimated available quantity (non-negative)';
COMMENT ON COLUMN product_availability.unavailable_reason IS 'Reason code when product is unavailable';
COMMENT ON COLUMN product_availability.last_updated IS 'Timestamp of last status update';
COMMENT ON COLUMN product_availability.estimated_restock_time IS 'Expected restock time when unavailable';

-- Create indexes for high-performance availability queries
CREATE INDEX idx_availability_location ON product_availability (location_id, is_available);
CREATE INDEX idx_availability_product ON product_availability (product_id, is_available);
CREATE INDEX idx_availability_location_product ON product_availability (location_id, product_id);
CREATE INDEX idx_availability_updated ON product_availability (last_updated, location_id);
CREATE INDEX idx_availability_restock ON product_availability (estimated_restock_time) WHERE estimated_restock_time IS NOT NULL;

-- Create partial index for available products only (performance optimization)
CREATE INDEX idx_availability_available_products ON product_availability (location_id, product_id) 
WHERE is_available = true;

-- Create partial index for unavailable products with reasons
CREATE INDEX idx_availability_unavailable_reasons ON product_availability (location_id, unavailable_reason) 
WHERE is_available = false;

-- Create trigger to automatically update last_updated timestamp
CREATE TRIGGER update_availability_last_updated
    BEFORE UPDATE ON product_availability
    FOR EACH ROW
    EXECUTE FUNCTION update_last_modified_column();

-- Function to log availability changes for audit purposes
CREATE OR REPLACE FUNCTION log_availability_change()
RETURNS TRIGGER AS $$
BEGIN
    -- Log significant changes (availability status or quantity changes > 10%)
    IF (TG_OP = 'UPDATE' AND (
        OLD.is_available != NEW.is_available OR
        OLD.unavailable_reason IS DISTINCT FROM NEW.unavailable_reason OR
        ABS(OLD.estimated_quantity - NEW.estimated_quantity) > GREATEST(OLD.estimated_quantity * 0.1, 1)
    )) THEN
        -- This would integrate with an audit log table in a real implementation
        -- For now, we'll use NOTICE for demonstration
        RAISE NOTICE 'Availability change for product % at location %: available % -> %, quantity % -> %',
            NEW.product_id, NEW.location_id, OLD.is_available, NEW.is_available, 
            OLD.estimated_quantity, NEW.estimated_quantity;
    END IF;
    
    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

-- Create trigger for availability change logging
CREATE TRIGGER trigger_log_availability_change
    AFTER INSERT OR UPDATE OR DELETE ON product_availability
    FOR EACH ROW
    EXECUTE FUNCTION log_availability_change();

-- Function to automatically set unavailable_reason to NULL when product becomes available
CREATE OR REPLACE FUNCTION clear_unavailable_reason_when_available()
RETURNS TRIGGER AS $$
BEGIN
    -- Clear unavailable reason and restock time when product becomes available
    IF NEW.is_available = true THEN
        NEW.unavailable_reason := NULL;
        NEW.estimated_restock_time := NULL;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to clear unavailable reason when product becomes available
CREATE TRIGGER trigger_clear_unavailable_reason
    BEFORE INSERT OR UPDATE ON product_availability
    FOR EACH ROW
    EXECUTE FUNCTION clear_unavailable_reason_when_available();

-- Create a view for quick availability overview across all locations
CREATE VIEW v_product_availability_summary AS
SELECT 
    p.product_id,
    p.name AS product_name,
    p.product_type,
    pc.name AS category_name,
    COUNT(*) AS total_locations,
    COUNT(CASE WHEN pa.is_available = true THEN 1 END) AS available_locations,
    COUNT(CASE WHEN pa.is_available = false THEN 1 END) AS unavailable_locations,
    SUM(pa.estimated_quantity) AS total_estimated_quantity,
    AVG(pa.estimated_quantity) AS avg_estimated_quantity,
    MAX(pa.last_updated) AS last_status_update
FROM products p
JOIN product_categories pc ON p.category_id = pc.category_id
LEFT JOIN product_availability pa ON p.product_id = pa.product_id
WHERE p.is_active = true
GROUP BY p.product_id, p.name, p.product_type, pc.name;

COMMENT ON VIEW v_product_availability_summary IS 'Summary view showing product availability across all locations with aggregate statistics';