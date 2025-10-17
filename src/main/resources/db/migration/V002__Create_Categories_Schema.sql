-- Create product_categories table for hierarchical product organization
-- This table supports nested categories with parent-child relationships

CREATE TABLE product_categories (
    category_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    parent_category_id UUID,
    display_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    
    -- Self-referencing foreign key for hierarchical structure
    CONSTRAINT fk_parent_category FOREIGN KEY (parent_category_id) 
        REFERENCES product_categories(category_id) ON DELETE SET NULL,
    
    -- Ensure display order is non-negative
    CONSTRAINT check_display_order_positive CHECK (display_order >= 0),
    
    -- Prevent self-referencing (category cannot be its own parent)
    CONSTRAINT check_no_self_reference CHECK (category_id != parent_category_id)
);

-- Add comments for documentation
COMMENT ON TABLE product_categories IS 'Hierarchical product category organization with parent-child relationships';
COMMENT ON COLUMN product_categories.category_id IS 'Unique category identifier';
COMMENT ON COLUMN product_categories.name IS 'Category display name, globally unique';
COMMENT ON COLUMN product_categories.description IS 'Category description for administrative purposes';
COMMENT ON COLUMN product_categories.parent_category_id IS 'Reference to parent category for hierarchy';
COMMENT ON COLUMN product_categories.display_order IS 'Ordering for UI display purposes';
COMMENT ON COLUMN product_categories.is_active IS 'Category availability status';

-- Create indexes for performance optimization
CREATE INDEX idx_categories_parent ON product_categories (parent_category_id, display_order);
CREATE INDEX idx_categories_active ON product_categories (is_active, display_order);
CREATE INDEX idx_categories_name ON product_categories (name) WHERE is_active = true;

-- Create index for hierarchy traversal
CREATE INDEX idx_categories_hierarchy ON product_categories (parent_category_id, is_active, display_order);

-- Add foreign key constraint from products to categories
-- (This will be applied after products table exists)
ALTER TABLE products 
ADD CONSTRAINT fk_product_category 
FOREIGN KEY (category_id) REFERENCES product_categories(category_id) ON DELETE RESTRICT;

-- Add unique constraint for product name within category
ALTER TABLE products 
ADD CONSTRAINT uk_product_name_category UNIQUE (category_id, name);

-- Function to prevent circular references in category hierarchy
CREATE OR REPLACE FUNCTION check_category_circular_reference()
RETURNS TRIGGER AS $$
DECLARE
    current_parent UUID;
    check_id UUID;
BEGIN
    -- If no parent is being set, no need to check
    IF NEW.parent_category_id IS NULL THEN
        RETURN NEW;
    END IF;
    
    -- Start with the proposed parent and traverse up the hierarchy
    current_parent := NEW.parent_category_id;
    
    WHILE current_parent IS NOT NULL LOOP
        -- If we encounter the current category ID in the parent chain, it's circular
        IF current_parent = NEW.category_id THEN
            RAISE EXCEPTION 'Circular reference detected: category cannot be ancestor of itself';
        END IF;
        
        -- Move to the next parent in the chain
        SELECT parent_category_id INTO current_parent
        FROM product_categories
        WHERE category_id = current_parent;
    END LOOP;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to prevent circular references
CREATE TRIGGER trigger_check_category_circular_reference
    BEFORE INSERT OR UPDATE OF parent_category_id ON product_categories
    FOR EACH ROW
    EXECUTE FUNCTION check_category_circular_reference();

-- Insert default root categories
INSERT INTO product_categories (category_id, name, description, parent_category_id, display_order, is_active) VALUES
('00000000-0000-0000-0000-000000000001', 'Beverages', 'All beverage products including coffee, tea, and cold drinks', NULL, 1, true),
('00000000-0000-0000-0000-000000000002', 'Food', 'Food items including pastries, sandwiches, and snacks', NULL, 2, true),
('00000000-0000-0000-0000-000000000003', 'Merchandise', 'Coffee shop merchandise including mugs, beans, and accessories', NULL, 3, true);

-- Insert beverage subcategories
INSERT INTO product_categories (category_id, name, description, parent_category_id, display_order, is_active) VALUES
('00000000-0000-0000-0000-000000000011', 'Hot Coffee', 'Hot coffee beverages including espresso-based drinks', '00000000-0000-0000-0000-000000000001', 1, true),
('00000000-0000-0000-0000-000000000012', 'Cold Coffee', 'Iced coffee beverages and cold brew options', '00000000-0000-0000-0000-000000000001', 2, true),
('00000000-0000-0000-0000-000000000013', 'Tea', 'Hot and iced tea selections', '00000000-0000-0000-0000-000000000001', 3, true),
('00000000-0000-0000-0000-000000000014', 'Non-Coffee Drinks', 'Hot chocolate, smoothies, and other non-coffee beverages', '00000000-0000-0000-0000-000000000001', 4, true);

-- Insert food subcategories
INSERT INTO product_categories (category_id, name, description, parent_category_id, display_order, is_active) VALUES
('00000000-0000-0000-0000-000000000021', 'Pastries', 'Fresh baked pastries, muffins, and sweet treats', '00000000-0000-0000-0000-000000000002', 1, true),
('00000000-0000-0000-0000-000000000022', 'Sandwiches', 'Fresh sandwiches and wraps', '00000000-0000-0000-0000-000000000002', 2, true),
('00000000-0000-0000-0000-000000000023', 'Snacks', 'Light snacks and grab-and-go items', '00000000-0000-0000-0000-000000000002', 3, true);