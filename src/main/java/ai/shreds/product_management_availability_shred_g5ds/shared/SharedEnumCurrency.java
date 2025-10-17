package ai.shreds.product_management_availability_shred_g5ds.shared;

public enum SharedEnumCurrency {
    USD("US Dollar", "$"),
    EUR("Euro", "€"),
    CAD("Canadian Dollar", "C$"),
    GBP("British Pound", "£");

    private final String name;
    private final String symbol;

    SharedEnumCurrency(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public static SharedEnumCurrency fromString(String name) {
        for (SharedEnumCurrency currency : values()) {
            if (currency.name().equalsIgnoreCase(name) || currency.getName().equalsIgnoreCase(name)) {
                return currency;
            }
        }
        throw new IllegalArgumentException("Unknown currency: " + name);
    }
}