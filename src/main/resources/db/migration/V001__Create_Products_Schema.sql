-- Create products table with comprehensive product information
-- This table stores the core product data including pricing, nutritional info, and metadata

CREATE TABLE products (
    product_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    product_type VARCHAR(20) NOT NULL CHECK (product_type IN ('BEVERAGE', 'FOOD', 'MERCHANDISE')),
    category_id UUID NOT NULL,
    base_price_amount DECIMAL(10,2) NOT NULL CHECK (base_price_amount >= 0),
    base_price_currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    nutritional_calories INTEGER CHECK (nutritional_calories >= 0),
    nutritional_total_fat DECIMAL(5,2) CHECK (nutritional_total_fat >= 0),
    nutritional_caffeine DECIMAL(5,2) CHECK (nutritional_caffeine >= 0),
    nutritional_allergens TEXT[],
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT check_product_type_valid CHECK (product_type IN ('BEVERAGE', 'FOOD', 'MERCHANDISE')),
    CONSTRAINT check_base_price_positive CHECK (base_price_amount > 0),
    CONSTRAINT check_currency_format CHECK (LENGTH(base_price_currency) = 3)
);

-- Add comments for documentation
COMMENT ON TABLE products IS 'Core product information including pricing, nutritional data, and categorization';
COMMENT ON COLUMN products.product_id IS 'Unique identifier for the product';
COMMENT ON COLUMN products.name IS 'Product display name, must be unique within category';
COMMENT ON COLUMN products.description IS 'Detailed product description for customers';
COMMENT ON COLUMN products.product_type IS 'Product classification: BEVERAGE, FOOD, or MERCHANDISE';
COMMENT ON COLUMN products.category_id IS 'Foreign key reference to product_categories table';
COMMENT ON COLUMN products.base_price_amount IS 'Base price before customizations and variants';
COMMENT ON COLUMN products.base_price_currency IS 'ISO currency code for pricing';
COMMENT ON COLUMN products.nutritional_calories IS 'Base nutritional calories per serving';
COMMENT ON COLUMN products.nutritional_total_fat IS 'Total fat content in grams';
COMMENT ON COLUMN products.nutritional_caffeine IS 'Caffeine content in milligrams';
COMMENT ON COLUMN products.nutritional_allergens IS 'Array of allergen strings';
COMMENT ON COLUMN products.is_active IS 'Product availability status for ordering';
COMMENT ON COLUMN products.created_at IS 'Product creation timestamp';
COMMENT ON COLUMN products.last_modified IS 'Last modification timestamp';

-- Create trigger to automatically update last_modified timestamp
CREATE OR REPLACE FUNCTION update_last_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.last_modified = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_products_last_modified
    BEFORE UPDATE ON products
    FOR EACH ROW
    EXECUTE FUNCTION update_last_modified_column();

-- Create partial index for active products (performance optimization)
CREATE INDEX idx_products_active ON products (category_id, is_active) WHERE is_active = true;

-- Create index for product name searches within categories
CREATE INDEX idx_products_name_category ON products (category_id, name);

-- Create index for product type filtering
CREATE INDEX idx_products_type ON products (product_type, is_active);

-- Create index for timestamp-based queries
CREATE INDEX idx_products_timestamps ON products (created_at, last_modified);