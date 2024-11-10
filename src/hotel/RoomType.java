package hotel;

public enum RoomType {
    SINGLE(1),
    STANDARD_DOUBLE(2),
    STANDARD_TWIN(2),
    TRIPLE(3),
    DELUXE_DOUBLE(3),
    STUDIO_APARTMENT(4),
    EXECUTIVE_SUITE(5);

    private final int occupancy;

    RoomType(int occupancy) {
        this.occupancy = occupancy;
    }

    public int getOccupancy() {
        return occupancy;
    }

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

