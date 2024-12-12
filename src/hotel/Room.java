package hotel;

public class Room {
    private int roomNumber;
    private RoomType roomType;
    private int occupancy;
    private int floorNum;
    private boolean occupied;
    private String guestName;
    private double cost;

    public Room(RoomType roomType, int occupancy, double cost) {
        this.roomType = roomType;
        this.occupancy = occupancy;
        this.cost = cost;
    }

    Room(int roomNumber, RoomType roomType, int occupancy, int floorNum, double cost) {
        this(roomNumber, roomType, occupancy, floorNum, cost, false, "");
    }

    public Room(int roomNumber, RoomType roomType, int occupancy, int floorNum, double cost, boolean occupied, String guestName) {
        this(roomType, occupancy, cost);
        this.roomNumber = roomNumber;
        this.floorNum = floorNum;
        this.occupied = occupied;
        this.guestName = guestName;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getFloorNum() {
        return floorNum;
    }

    public void setFloorNum(int floorNum) {
        this.floorNum = floorNum;
    }

    @Override
    public String toString() {
        return STR."Room{roomNumber=\{roomNumber}, roomType=\{roomType}, occupancy=\{occupancy}, floorNum=\{floorNum}, occupied=\{occupied}, guestName='\{guestName}', cost=\{cost}}";
    }
}

