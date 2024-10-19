package hotel;

import java.util.ArrayList;

public class Hotel {
    private String name;
    private int numRooms;
    private ArrayList<RoomType> roomTypes;
    private ArrayList<Room> rooms;

    Hotel(String name, int numRooms, ArrayList<RoomType> roomTypes, ArrayList<Room> rooms) {
        this.name = name;
        this.numRooms = numRooms;
        this.roomTypes = roomTypes;
        this.rooms = rooms;
    }
}