package ai.shreds.product_management_availability_shred_g5ds.shared;

public enum SharedEnumUnavailableReason {
    OUT_OF_STOCK("Out of Stock"),
    EQUIPMENT_DOWN("Equipment Down"),
    SEASONAL("Seasonal Unavailable"),
    DISCONTINUED("Product Discontinued"),
    SUPPLY_CHAIN_ISSUE("Supply Chain Issue"),
    MAINTENANCE("Under Maintenance");

    private final String displayName;

    SharedEnumUnavailableReason(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static SharedEnumUnavailableReason fromString(String name) {
        for (SharedEnumUnavailableReason reason : values()) {
            if (reason.name().equalsIgnoreCase(name) || reason.getDisplayName().equalsIgnoreCase(name)) {
                return reason;
            }
        }
        throw new IllegalArgumentException("Unknown unavailable reason: " + name);
    }
}