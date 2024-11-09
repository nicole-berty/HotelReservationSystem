package hotel;

public class Room {
    private int roomNumber;
    private RoomType roomType;
    private int occupancy;
    private int floor;
    private boolean occupied;
    private String guestName;
    private double cost;

    Room(int roomNumber, RoomType roomType, int occupancy, int floor, double cost) {
        this(roomNumber, roomType, occupancy, floor, cost, false, "");
    }

    Room(int roomNumber, RoomType roomType, int occupancy, int floor, double cost, boolean occupied, String guestName) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.occupancy = occupancy;
        this.floor = floor;
        this.cost = cost;
        this.occupied = occupied;
        this.guestName = guestName;
    }

    public boolean isOccupied() {
        return occupied;
    }
}

