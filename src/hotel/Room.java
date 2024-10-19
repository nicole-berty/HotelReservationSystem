package hotel;

public class Room {
    private int roomNumber;
    private RoomType roomType;
    private int occupancy;
    private boolean occupied;
    private String guestName;

    Room(int roomNumber, RoomType roomType, int occupancy) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.occupancy = occupancy;
        this.occupied = false;
        this.guestName = "";
    }

    Room(int roomNumber, RoomType roomType, int occupancy, boolean occupied, String guestName) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.occupancy = occupancy;
        this.occupied = occupied;
        this.guestName = guestName;
    }
}

enum RoomType { SINGLE, STANDARD_DOUBLE, STANDARD_TWIN, DELUXE_DOUBLE, TRIPLE, STUDIO_APARTMENT, EXECUTIVE_SUITE}
