package hotel;

import people.Employee;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;

public class Hotel {
    private String name;
    private int numRooms;
    private Date openDate;
    private int currentOccupancy = 0;
    private EnumMap<RoomType, Double> roomTypes;
    private ArrayList<Room> rooms;
    private ArrayList<Employee> employees;

    public Hotel(String name) {
        this.name = name;
    }

    Hotel(String name, int numRooms, Date openDate, EnumMap<RoomType, Double> roomTypes, ArrayList<Room> rooms, ArrayList<Employee> employees) {
        this.name = name;
        this.numRooms = numRooms;
        this.openDate = openDate;
        this.roomTypes = roomTypes;
        this.rooms = rooms;
        this.employees = employees;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumRooms(int numRooms) {
        this.numRooms = numRooms;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    public void setRoomTypes(EnumMap<RoomType, Double> roomTypes) {
        this.roomTypes = roomTypes;
    }

    public void addRoomType(RoomType roomType, double cost) {
        this.roomTypes.put(roomType, cost);
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