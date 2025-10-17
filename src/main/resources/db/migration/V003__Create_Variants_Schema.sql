-- Create product_variants table for product size and customization options
-- This table stores variant-specific information including pricing modifiers and nutritional data

CREATE TABLE product_variants (
    variant_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL,
    size_name VARCHAR(50) NOT NULL,
    volume_in_oz DECIMAL(5,2) CHECK (volume_in_oz > 0),
    volume_in_ml DECIMAL(7,2) CHECK (volume_in_ml > 0),
    size_abbreviation VARCHAR(5),
    price_modifier_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    price_modifier_currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    nutritional_calories INTEGER CHECK (nutritional_calories >= 0),
    nutritional_total_fat DECIMAL(5,2) CHECK (nutritional_total_fat >= 0),
    nutritional_caffeine DECIMAL(5,2) CHECK (nutritional_caffeine >= 0),
    nutritional_allergens TEXT[],
    is_available BOOLEAN NOT NULL DEFAULT true,
    
    -- Foreign key constraint to products table
    CONSTRAINT fk_variant_product FOREIGN KEY (product_id) 
        REFERENCES products(product_id) ON DELETE CASCADE,
    
    -- Ensure currency format is valid
    CONSTRAINT check_variant_currency_format CHECK (LENGTH(price_modifier_currency) = 3),
    
    -- Ensure at least one volume measurement is provided
    CONSTRAINT check_volume_provided CHECK (volume_in_oz IS NOT NULL OR volume_in_ml IS NOT NULL),
    
    -- Unique constraint for size name per product
    CONSTRAINT uk_variant_size_per_product UNIQUE (product_id, size_name)
);

-- Add comments for documentation
COMMENT ON TABLE product_variants IS 'Product variants with size-specific pricing and nutritional information';
COMMENT ON COLUMN product_variants.variant_id IS 'Unique identifier for the product variant';
COMMENT ON COLUMN product_variants.product_id IS 'Foreign key reference to products table';
COMMENT ON COLUMN product_variants.size_name IS 'Display name for the size (e.g., Small, Medium, Large)';
COMMENT ON COLUMN product_variants.volume_in_oz IS 'Volume measurement in fluid ounces';
COMMENT ON COLUMN product_variants.volume_in_ml IS 'Volume measurement in milliliters';
COMMENT ON COLUMN product_variants.size_abbreviation IS 'Short abbreviation for display (e.g., S, M, L)';
COMMENT ON COLUMN product_variants.price_modifier_amount IS 'Price adjustment from base product price (can be negative)';
COMMENT ON COLUMN product_variants.price_modifier_currency IS 'Currency code for price modifier';
COMMENT ON COLUMN product_variants.nutritional_calories IS 'Size-specific calorie content';
COMMENT ON COLUMN product_variants.nutritional_total_fat IS 'Size-specific total fat in grams';
COMMENT ON COLUMN product_variants.nutritional_caffeine IS 'Size-specific caffeine content in milligrams';
COMMENT ON COLUMN product_variants.nutritional_allergens IS 'Size-specific allergen information';
COMMENT ON COLUMN product_variants.is_available IS 'Variant availability status';

-- Create indexes for performance optimization
CREATE INDEX idx_variants_product ON product_variants (product_id, is_available);
CREATE INDEX idx_variants_available ON product_variants (is_available, size_name);
CREATE INDEX idx_variants_size_order ON product_variants (product_id, size_name);

-- Create index for volume-based queries
CREATE INDEX idx_variants_volume ON product_variants (volume_in_oz, volume_in_ml) WHERE is_available = true;

-- Function to validate that final price (base + modifier) is non-negative
CREATE OR REPLACE FUNCTION check_variant_final_price_positive()
RETURNS TRIGGER AS $$
DECLARE
    base_price DECIMAL(10,2);
    final_price DECIMAL(10,2);
BEGIN
    -- Get the base price from the parent product
    SELECT base_price_amount INTO base_price
    FROM products
    WHERE product_id = NEW.product_id;
    
    -- Calculate final price
    final_price := base_price + NEW.price_modifier_amount;
    
    -- Ensure final price is not negative
    IF final_price < 0 THEN
        RAISE EXCEPTION 'Final price (base: % + modifier: % = %) cannot be negative for variant %', 
            base_price, NEW.price_modifier_amount, final_price, NEW.size_name;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to validate final price
CREATE TRIGGER trigger_check_variant_final_price
    BEFORE INSERT OR UPDATE OF price_modifier_amount ON product_variants
    FOR EACH ROW
    EXECUTE FUNCTION check_variant_final_price_positive();

-- Function to ensure at least one variant exists for active products
CREATE OR REPLACE FUNCTION check_product_has_variants()
RETURNS TRIGGER AS $$
DECLARE
    variant_count INTEGER;
    product_active BOOLEAN;
BEGIN
    -- Get product active status
    SELECT is_active INTO product_active
    FROM products
    WHERE product_id = OLD.product_id;
    
    -- Only check if product is active
    IF product_active THEN
        -- Count remaining variants after deletion
        SELECT COUNT(*) INTO variant_count
        FROM product_variants
        WHERE product_id = OLD.product_id
        AND variant_id != OLD.variant_id;
        
        -- Prevent deletion if it would leave no variants for active product
        IF variant_count = 0 THEN
            RAISE EXCEPTION 'Cannot delete last variant for active product. Deactivate product first.';
        END IF;
    END IF;
    
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to prevent deletion of last variant for active products
CREATE TRIGGER trigger_check_product_has_variants
    BEFORE DELETE ON product_variants
    FOR EACH ROW
    EXECUTE FUNCTION check_product_has_variants();