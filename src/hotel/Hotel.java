package hotel;

import people.Employee;
import pricing.PricingStrategy;
import system.SystemUtils;

import java.time.LocalDate;
import java.util.*;

public class Hotel {
    private String name;
    private int numRooms;
    private LocalDate openDate;
    private int currentOccupancy = 0;
    private EnumMap<RoomType, Double> roomTypeCostMap;
    private ArrayList<Room> rooms = new ArrayList<>();
    private ArrayList<Employee> employees = new ArrayList<>();
    private PricingStrategy pricingStrategy;

    // Copy constructor
    public Hotel(Hotel other) {
        this(other.name, other.numRooms, other.openDate, other.roomTypeCostMap, other.rooms, other.employees, other.pricingStrategy);
        this.currentOccupancy = other.currentOccupancy;
    }

    public Hotel(String name) {
        this.name = name;
        this.roomTypeCostMap = new EnumMap<>(RoomType.class);
    }

    public Hotel(String name, int numRooms, LocalDate openDate, EnumMap<RoomType, Double> roomTypeCostMap, ArrayList<Room> rooms,
                 ArrayList<Employee> employees, PricingStrategy pricingStrategy) {
        this.name = name;
        this.numRooms = numRooms;
        this.openDate = openDate;
        this.roomTypeCostMap = new EnumMap<>(roomTypeCostMap);
        this.rooms = new ArrayList<>(rooms);
        this.employees = new ArrayList<>(employees);
        this.pricingStrategy = pricingStrategy;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumRooms(int numRooms) {
        this.numRooms = numRooms;
    }

    public void setOpenDate(LocalDate openDate) {
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

    public void setRoomTypeCostMap(EnumMap<RoomType, Double> roomTypeCostMap) {
        this.roomTypeCostMap = roomTypeCostMap;
    }

    public void addRoomType(RoomType roomType, double cost) {
        this.roomTypeCostMap.put(roomType, cost);
    }

    public void setCurrentOccupancy(int currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
    }

    public EnumMap<RoomType, Double> getRoomTypeCostMap() {
        return roomTypeCostMap;
    }

    public String getName() {
        return name;
    }

    public int getCurrentOccupancy() {
        calcCurrentOccupancy();
        return currentOccupancy;
    }

    public double getRoomCost(RoomType roomType) {
        return roomTypeCostMap.getOrDefault(roomType, 0.0); // Default to 0.0 if not found
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public Room getRoomByNumber(int num) {
        return rooms.stream()
                .filter(room -> room.getRoomNumber() == num)
                .findFirst()
                .orElse(null);
    }

    public List<Room> getVacantRooms() {
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
                roomTypeCostMap.toString(), roomSb, employees.toString(), getPricingStrategy());
    }
}