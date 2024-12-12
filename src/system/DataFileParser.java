package system;

import hotel.Hotel;
import hotel.Room;
import hotel.RoomType;
import people.*;

import java.text.ParseException;
import java.util.*;
import java.util.regex.*;

public class DataFileParser {

    public static Hotel parseHotelData(String file) {

        String[] fields = file.split("\\|");
        String hotelName = fields[0];
        Date date = null;
        try {
            date = SystemUtils.getDateFormat().parse(fields[1]);
        } catch (ParseException _) {
            
        }
        int roomsAvailable = Integer.parseInt(fields[2].trim());
        String roomTypeCosts = fields[3];
        String roomsList = fields[4];
        String personData = fields[5];

        // Parse Room Types and Costs, and Rooms and Employees lists
        EnumMap<RoomType, Double> roomTypeCostMap = parseRoomTypeCosts(roomTypeCosts);
        ArrayList<Room> rooms = parseRoomsList(roomsList);
        ArrayList<Employee> employees = getEmployeesFromPeopleList(personData);

        return new Hotel(hotelName, roomsAvailable, date, roomTypeCostMap, rooms, employees);
    }

    private static EnumMap<RoomType, Double> parseRoomTypeCosts(String data) {
        EnumMap<RoomType, Double> map = new EnumMap<>(RoomType.class);
        Matcher matcher = Pattern.compile("([^{,]*?)=(\\d+\\.\\d+)").matcher(data);
        while (matcher.find()) {
            RoomType roomType = RoomType.fromString(matcher.group(1).trim());
            double cost = Double.parseDouble(matcher.group(2).trim());
            map.put(roomType, cost);
        }
        return map;
    }

    private static ArrayList<Room> parseRoomsList(String data) {
        ArrayList<Room> rooms = new ArrayList<>();
        Matcher matcher = Pattern.compile("Room\\{roomNumber=(\\d+), roomType=(.*?), occupancy=(\\d+), floorNum=(\\d+), occupied=(.*?), guestName='(.*?)', cost=(\\d+\\.\\d+)}")
                .matcher(data);
        while (matcher.find()) {
            int roomNumber = Integer.parseInt(matcher.group(1).trim());
            String roomType = matcher.group(2).trim();
            RoomType roomType1 = RoomType.fromString(roomType);
            int occupancy = Integer.parseInt(matcher.group(3).trim());
            int floorNum = Integer.parseInt(matcher.group(4).trim());
            boolean occupied = Boolean.parseBoolean(matcher.group(5).trim());
            String guestName = matcher.group(6).equals("null") ? null : matcher.group(6);
            double cost = Double.parseDouble(matcher.group(7).trim());

            rooms.add(new Room(roomNumber, roomType1, occupancy, floorNum, cost, occupied, guestName));
        }
        return rooms;
    }

    private static ArrayList<Person> parsePersonList(String data) {
        ArrayList<Person> people = new ArrayList<>();
        // match regex for people
        Matcher personMatcher = Pattern.compile("Person\\{name='(.*?)', email='(.*?)', id=(\\d+), ([^}]*)").matcher(data);
        Matcher employeeMatcher = Pattern.compile("salary=(\\d+\\.\\d+), position='(.*?)', (?:managedDepartments=([^}]*), staffUnderManagement=([^}\\]]*)|assignedCheckInCounter=(\\d+))+").matcher(data);
        Matcher customerMatcher = Pattern.compile("numCompletedReservations=(\\d+)").matcher(data);
        while (personMatcher.find()) {
            String name = personMatcher.group(1).trim();
            String email = personMatcher.group(2).trim();
            int id = Integer.parseInt(personMatcher.group(3).trim());

            while(employeeMatcher.find()) {
                double salary = Double.parseDouble(employeeMatcher.group(1).trim());
                String position = employeeMatcher.group(2).trim();
                if(position.equals("HotelReceptionist")) {
                    people.add(new HotelReceptionist(name, email, id, salary, Integer.parseInt(employeeMatcher.group(5).trim())));
                } else if(position.equals("HotelManager")) {
                    Employee[] employeesManaged = getEmployeesFromPeopleList(employeeMatcher.group(4)).toArray(Employee[]::new);
                    people.add(new HotelManager(name, email, id,salary, employeeMatcher.group(3).split(","), employeesManaged));
                }
            }

            while(customerMatcher.find()) {
                int numCompletedReservations = Integer.parseInt(customerMatcher.group(1));
                people.add(new Customer(name, email, id, numCompletedReservations));
            }
        }
        return people;
    }

    private static ArrayList<Employee> getEmployeesFromPeopleList(String employeeData) {
        ArrayList<Employee> employees = new ArrayList<>();
        if(employeeData != null && !employeeData.isBlank() && !employeeData.equals("null")) {
        ArrayList<Person> peopleList = parsePersonList(employeeData);
        for(var person : peopleList) {
            if(person instanceof Employee employee) {
                employees.add(employee);
            }
        }
        }
        return employees;
    }
}
