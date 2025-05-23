package system;

import hotel.Hotel;
import hotel.Room;
import hotel.RoomType;
import people.*;
import pricing.PricingStrategy;
import reservations.Reservation;
import reservations.ReservationType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.*;

public class DataFileParser {

    public static Reservation parseReservationData(String fileLine) {

        String[] fields = fileLine.split("\\|");
        String reservationId = fields[0].trim();
        String name = fields[1].trim();
        String email = fields[2].trim();
        boolean advancedPurchase = ReservationType.fromString(fields[3]) == ReservationType.ADVANCE_PURCHASE;
        String hotelName = fields[4].trim();
        boolean refundable = Boolean.parseBoolean(fields[5]);

        LocalDate checkIn = SystemUtils.getFormattedDateOrNull(fields[6]);
        int numNights = Integer.parseInt(fields[7].trim());
        double totalCost = Double.parseDouble(fields[8].trim());
        double deposit = Double.parseDouble(fields[9].trim());
        // 5 - Date/Time API
        LocalDateTime creationDate = SystemUtils.getFormattedDateTimeOrNull(fields[10]);
        LocalDate cancellationDate = SystemUtils.getFormattedDateOrNull(fields[11]);

        List<String> roomsList = List.of(fields[12].split(","));
        Set<Room> roomsReserved = new HashSet<>();
        for(var roomReserved : roomsList) {
            // roomReserved looks like Room 301: Triple Room (3 Single Beds) so room split will be an array of ["Single Room (1 Single Bed)", "2"]
            var roomNum = roomReserved.split(":")[0].replace("Room ", "");
            Room room =HotelSystem.getInstance().getSelectedHotel().getRoomByNumber(Integer.parseInt(roomNum));
            if(room != null) {
                roomsReserved.add(room);
            }
        }
        boolean paid = Boolean.parseBoolean(fields[13]);
        boolean cancelled = Boolean.parseBoolean(fields[14]);
        boolean complete = Boolean.parseBoolean(fields[15]);
        PricingStrategy pricingStrategy = PricingStrategy.fromString(fields[16]);
        boolean checkedIn = Boolean.parseBoolean(fields[17]);
        String[] addCostsArr = fields[18].replace("[", "").replace("]", "").trim().split(",");
        int[] additionalCosts = new int[addCostsArr.length];

        for (int i = 0; i < addCostsArr.length; i++) {
            if(!addCostsArr[i].isBlank()) {
                additionalCosts[i] = Integer.parseInt(addCostsArr[i].trim());
            }
        }

        return new Reservation(reservationId, name, email, advancedPurchase, hotelName, refundable, checkIn, numNights,
                totalCost, deposit, creationDate, cancellationDate, roomsReserved, paid, cancelled, complete, pricingStrategy,
                checkedIn, additionalCosts);
    }

    public static Hotel parseHotelData(String fileLine) {

        String[] fields = fileLine.split("\\|");
        String hotelName = fields[0];
        LocalDate date = SystemUtils.getFormattedDateOrNull(fields[1]);
        int roomsAvailable = Integer.parseInt(fields[2].trim());
        String roomTypeCosts = fields[3];
        String roomsList = fields[4];
        String personData = fields[5];
        PricingStrategy pricingStrategy = PricingStrategy.fromString(fields[6].trim());

        // Parse Room Types and Costs, and Rooms and Employees lists
        EnumMap<RoomType, Double> roomTypeCostMap = parseRoomTypeCosts(roomTypeCosts);
        ArrayList<Room> rooms = parseRoomsList(roomsList);
        ArrayList<Employee> employees = getEmployeesFromPeopleList(personData);

        return new Hotel(hotelName, roomsAvailable, date, roomTypeCostMap, rooms, employees, pricingStrategy);
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
        Matcher personMatcher = Pattern.compile("Person\\{name='(.*?)', email='(.*?)', ([^}]*)").matcher(data);
        Matcher employeeMatcher = Pattern.compile("salary=(\\d+\\.\\d+), position='(.*?)', (?:managedDepartments=([^}]*), staffUnderManagement=([^}\\]]*)|assignedCheckInCounter=(\\d+))+").matcher(data);
        Matcher customerMatcher = Pattern.compile("numCompletedReservations=(\\d+)").matcher(data);
        while (personMatcher.find()) {
            String name = personMatcher.group(1).trim();
            String email = personMatcher.group(2).trim();

            while(employeeMatcher.find()) {
                double salary = Double.parseDouble(employeeMatcher.group(1).trim());
                String position = employeeMatcher.group(2).trim();
                if(position.equals("HotelReceptionist")) {
                    people.add(new HotelReceptionist(name, email, salary, Integer.parseInt(employeeMatcher.group(5).trim())));
                } else if(position.equals("HotelManager")) {
                    Employee[] employeesManaged = getEmployeesFromPeopleList(employeeMatcher.group(4)).toArray(Employee[]::new);
                    people.add(new HotelManager(name, email,salary, employeeMatcher.group(3).split(","), employeesManaged));
                }
            }

            while(customerMatcher.find()) {
                int numCompletedReservations = Integer.parseInt(customerMatcher.group(1));
                people.add(new Customer(name, email, numCompletedReservations));
            }
        }
        return people;
    }

    public static ArrayList<Employee> getEmployeesFromPeopleList(String employeeData) {
        ArrayList<Employee> employees = new ArrayList<>();
        if(employeeData != null && !employeeData.isBlank() && !employeeData.equals("null")) {
            ArrayList<Person> peopleList = parsePersonList(employeeData);
            // LVTI - type inferred while iterating for loop
            for(var person : peopleList) {
                // Pattern Matching for the instanceof Operator
                if(person instanceof Employee employee) {
                    employees.add(employee);
                }
            }
        }
        return employees;
    }
}
