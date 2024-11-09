package hotel;

public enum RoomType {
    SINGLE,
    STANDARD_DOUBLE,
    STANDARD_TWIN,
    TRIPLE,
    DELUXE_DOUBLE,
    STUDIO_APARTMENT,
    EXECUTIVE_SUITE;


    @Override
    public String toString()
    {
        return switch (this.ordinal()) {
            case 0 -> "Single Room (1 Single Bed)";
            case 1 -> "Standard Double Room";
            case 2 -> "Standard Twin Room (2 Single Beds)";
            case 3 -> "Triple Room (3 Single Beds)";
            case 4 -> "Deluxe Double Room";
            case 5 -> "Studio Apartment";
            case 6 -> "Executive Suite";
            default -> "";
        };
    }
}

