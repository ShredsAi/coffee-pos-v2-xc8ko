package ai.shreds.product_management_availability_shred_g5ds.shared;

public enum SharedEnumProductType {
    BEVERAGE("Beverage"),
    FOOD("Food"),
    MERCHANDISE("Merchandise");

    private final String displayName;

    SharedEnumProductType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static SharedEnumProductType fromString(String name) {
        for (SharedEnumProductType type : values()) {
            if (type.name().equalsIgnoreCase(name) || type.getDisplayName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown product type: " + name);
    }
}