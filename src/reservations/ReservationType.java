package reservations;

public enum ReservationType {
    STANDARD, ADVANCE_PURCHASE;

    @Override
    public String toString()
    {
        return switch (this.ordinal()) {
            case 0 -> "Standard";
            case 1 -> "Advance Purchase";
            default -> "";
        };
    }

    public static ReservationType fromString(String text) {
        for (ReservationType b : ReservationType.values()) {
            if (b.toString().equalsIgnoreCase(text)) {
                return b;
            }
        }
        // return STANDARD as default if no match
        return ReservationType.STANDARD;
    }
}
