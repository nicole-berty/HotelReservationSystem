package hotel;

public class Room {
    private int roomNumber;
    private RoomType roomType;
    private int occupancy;
    private boolean occupied;
    private String guestName;
    private double cost;

    Room(int roomNumber, RoomType roomType, int occupancy, double cost) {
        this(roomNumber, roomType, occupancy, cost, false, "");
    }

    Room(int roomNumber, RoomType roomType, int occupancy, double cost, boolean occupied, String guestName) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.occupancy = occupancy;
        this.cost = cost;
        this.occupied = occupied;
        this.guestName = guestName;
    }

    public boolean isOccupied() {
        return occupied;
    }
}

enum RoomType { SINGLE, STANDARD_DOUBLE, STANDARD_TWIN, DELUXE_DOUBLE, TRIPLE, STUDIO_APARTMENT, EXECUTIVE_SUITE}
