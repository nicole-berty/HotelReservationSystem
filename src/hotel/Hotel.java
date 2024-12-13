package hotel;

import people.Employee;
import pricing.PricingStrategy;
import system.SystemUtils;

import java.util.*;

public class Hotel {
    private String name;
    private int numRooms;
    private Date openDate;
    private int currentOccupancy = 0;
    private EnumMap<RoomType, Double> roomTypes;
    private ArrayList<Room> rooms = new ArrayList<>();
    private ArrayList<Employee> employees = new ArrayList<>();
    private PricingStrategy pricingStrategy;

    // Copy constructor
    public Hotel(Hotel other) {
        this(other.name, other.numRooms, other.openDate, other.roomTypes, other.rooms, other.employees, other.pricingStrategy);
        this.currentOccupancy = other.currentOccupancy;
    }

    public Hotel(String name) {
        this.name = name;
        this.roomTypes = new EnumMap<>(RoomType.class);
    }

    public Hotel(String name, int numRooms, Date openDate, EnumMap<RoomType, Double> roomTypes, ArrayList<Room> rooms,
                 ArrayList<Employee> employees, PricingStrategy pricingStrategy) {
        this.name = name;
        this.numRooms = numRooms;
        this.openDate = openDate;
        this.roomTypes = roomTypes;
        this.rooms = rooms;
        this.employees = employees;
        this.pricingStrategy = pricingStrategy;
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

    public void setPricingStrategy(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    public PricingStrategy getPricingStrategy() {
        return pricingStrategy;
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

    public EnumMap<RoomType, Double> getRoomTypes() {
        return roomTypes;
    }

    public String getName() {
        return name;
    }

    public int getCurrentOccupancy() {
        calcCurrentOccupancy();
        return currentOccupancy;
    }

    List<Room> getVacantRooms() {
        return rooms.stream().filter(room -> !room.isOccupied()).toList();
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    void calcCurrentOccupancy() {
        for(Room room : rooms) {
            if(room.isOccupied()) {
                currentOccupancy += 1;
            }
        }
    }

    public String toCsvString() {
        StringBuilder roomSb = new StringBuilder();
        for(Room room : rooms) {
            roomSb.append(room.toString()).append(",");
        }
        return String.format("%s|%s|%s|%s|%s|%s|%s", name, SystemUtils.getDateStringOrNull(openDate), numRooms,
                roomTypes.toString(), roomSb, employees.toString(), getPricingStrategy());
    }
}