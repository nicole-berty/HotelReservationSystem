package hotel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Hotel {
    private String name;
    private int numRooms;
    private Date openDate;
    private int currentOccupancy = 0;
    private ArrayList<RoomType> roomTypes;
    private ArrayList<Room> rooms;

    Hotel(String name, int numRooms, Date openDate, ArrayList<RoomType> roomTypes, ArrayList<Room> rooms) {
        this.name = name;
        this.numRooms = numRooms;
        this.openDate = openDate;
        this.roomTypes = roomTypes;
        this.rooms = rooms;
    }

    public void setCurrentOccupancy(int currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
    }

    public int getCurrentOccupancy() {
        calcCurrentOccupancy();
        return currentOccupancy;
    }

    List<Room> getVacantRooms() {
        return rooms.stream().filter(room -> !room.isOccupied()).toList();
    }

    void calcCurrentOccupancy() {
        for(Room room : rooms) {
            if(room.isOccupied()) {
                currentOccupancy += 1;
            }
        }
    }

    String toCsvString() {
        return String.format("%s,%s,%s,%s,%s", name, numRooms, openDate, roomTypes.toString(), rooms.toString());
    }
}