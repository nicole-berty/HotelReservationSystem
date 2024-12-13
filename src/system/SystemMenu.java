package system;

import hotel.Hotel;
import hotel.Room;
import hotel.RoomType;
import people.Customer;
import people.Employee;
import people.HotelManager;
import people.Person;
import reservations.Reservation;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.System.exit;

public class SystemMenu {
    static Scanner sc = new Scanner(System.in);

    public static void displayHotelSelectionMenu() {
        List<String> validValues = getOptions(HotelSystem.getInstance().hotels.size());
        System.out.println("Welcome to the MegaCorp(C) Hotels system!\nPlease select the hotel you want to access or press q to exit.");

        for(int i = 0; i < HotelSystem.getInstance().hotels.size(); i++) {
            System.out.println(STR."(\{validValues.get(i)}) \{HotelSystem.getInstance().hotels.get(i).getName()}");
        }

        String userInput = getInput();
        while (!validValues.contains(userInput.toLowerCase())) {
            System.out.println("Please enter a valid option!");
            userInput = getInput();
        }
        // Subtract 1 from userInput as ArrayList is 0 based
        HotelSystem.getInstance().setSelectedHotel(HotelSystem.getInstance().hotels.get(Integer.parseInt(userInput) - 1));
        displayStartMenu();
    }

    public static void displayStartMenu() {
        List<String> validValues = getOptions(3);
        System.out.println("Enter the option you would like to access or press q to exit.");
        System.out.println("(1) Employee Login");
        System.out.println("(2) Guest Login");
        System.out.println("(3) Back");
        System.out.println("(q) Exit");

        String userInput = getInput();
        while (!validValues.contains(userInput.toLowerCase())) {
            System.out.println("Please enter a valid option!");
            userInput = getInput();
        }
        switch (userInput.toLowerCase()) {
            case "1":
                Employee employee = login();
                // Control will only reach this point if login was successful, as login contains a loop that is only broken by
                // successfully logging in or entering Q to exit the program
                if(employee instanceof HotelManager) {
                    displayMainManagerMenu((HotelManager) employee);
                } else {
                    displayMainMenu(employee);
                }
                break;
            case "2":
                System.out.println("Welcome! Please enter your name: ");
                userInput = getInput();
                System.out.println("Enter your email: ");
                String email = getInput();
                Customer customer = new Customer(userInput, email);
                HotelSystem.getInstance().setCurrentUser(customer);
                displayMainMenu(customer);
                break;
            case "3":
                HotelSystem.getInstance().setCurrentUser(null);
                displayHotelSelectionMenu();
                break;
        }
    }

    public static void displayMainManagerMenu(HotelManager manager) {
        //noinspection InfiniteLoopStatement - false positive warning, we only want to exit if user input is q
        while(true) {
            List<String> validValues = getOptions(3);
            System.out.println("Enter the option you would like to access or press q to exit.");
            System.out.println("(1) Reservations & Cancellations");
            System.out.println("(2) Apply Discount");
            System.out.println("(3) View Reservations");
            System.out.println("(q) Exit");
            String userInput = getInput();
            while (!validValues.contains(userInput.toLowerCase())) {
                System.out.println("Please enter a valid option!");
                userInput = getInput();
            }
            outerSwitch: // label for the switch so can break out of it in loop later
            switch (userInput.toLowerCase()) {
                case "1":
                    displayMainMenu(manager);
                    break;
                case "2":
                    System.out.println("Enter the reservation number you'd like to apply the discount to.");
                    userInput = getInput();
                    String reservation = Optional.ofNullable(SystemUtils.readAndSearchFile(HotelSystem.getInstance().dataFiles.get("reservations").path(), userInput))
                            .orElse(List.of()) // Provide a default empty list in case method returned null
                            .stream()
                            .filter(Objects::nonNull)
                            .collect(Collectors.joining("|"));
                    while (reservation.isEmpty()) {
                        System.out.println("Reservation not found! Please try again, or press b to go back or q to quit.");
                        userInput = getInput();
                        if(userInput.equalsIgnoreCase("b")) {
                            break outerSwitch;
                        }
                        reservation = Optional.ofNullable(SystemUtils.readAndSearchFile(HotelSystem.getInstance().dataFiles.get("reservations").path(), userInput))
                                .orElse(List.of()) // Provide a default empty list in case method returned null
                                .stream()
                                .filter(Objects::nonNull)
                                .collect(Collectors.joining("|"));
                    }
                    Reservation reservation1 = DataFileParser.parseReservationData(reservation);
                    System.out.println(STR."The total cost of this reservation is \{reservation1.getTotalCost()}.");
                    System.out.println("How much of a % discount would you like to apply?");
                    double discount = getNumberInput("Please enter a number greater than 0.", false);
                    manager.giveDiscount(reservation1, discount);
                    break;
            }
        }
    }

    public static void displayMainMenu(Person person) {
        //noinspection InfiniteLoopStatement - false positive warning, we only want to exit if user input is q
        while(true) {
            List<String> validValues = getOptions(3);
            System.out.println("Enter the option you would like to access or press q to exit.");
            System.out.println("(1) Make a reservation");
            System.out.println("(2) Make a cancellation");
            System.out.println("(3) View Reservations");
            System.out.println("(q) Exit");
            String userInput = getInput();
            while (!validValues.contains(userInput.toLowerCase())) {
                System.out.println("Please enter a valid option!");
                userInput = getInput();
            }
            switch (userInput.toLowerCase()) {
                case "1":
                    createReservation(person);
                    break;
                case "2":

                    break;
            }
        }
    }

    public static void createReservation(Person person) {
        Date checkInDate = new Date();
        long daysBetween = 0L;
        // checkInDate must be in the future
        while(daysBetween <= 0) {
            System.out.println("When would you like to stay at the hotel? Please enter the date in YYYY-MM-DD format. The date must be at least 1 day in advance of today.");
            checkInDate = getValidDate();
            daysBetween = (checkInDate.getTime() - (new Date()).getTime())  / (24 * 60 * 60 * 1000);
        }

        System.out.println("How many nights will you stay? Enter a whole number.");
        int numNights = (int) getNumberInput("Enter a whole number of nights to stay.", true);

        System.out.println("Please enter the name for the booking: ");
        String name = getInput();
        System.out.println("Please enter the email for the booking: ");
        String email = getInput();

        System.out.println("How many of each room would you like to book? Enter 0 or more for each room.");
        EnumMap<RoomType, Integer> roomsReserved = new EnumMap<>(RoomType.class);

        for(var roomType : HotelSystem.getInstance().getSelectedHotel().getRoomTypes().entrySet()) {
            System.out.println("How many **" + roomType.getKey() + STR." - Occupancy: \{roomType.getKey().getOccupancy()} - Cost: \{roomType.getValue()}** do you want to reserve?");
            int numRooms = (int) getNumberInput("Enter a whole number of 0 or more", true, true);
            roomsReserved.put(roomType.getKey(), numRooms);
            String userInput = displayYesNoQuestion("Would you like to see more room types? Y/N");
            if(userInput.equalsIgnoreCase("n")) {
                break;
            }
        }
        if(roomsReserved.values().stream().allMatch(num -> num == 0)) {
            System.out.println("You didn't reserve any rooms! Going back to main menu...");
            return;
        }

        boolean advancedPurchase = false;
        boolean refundable = false;
        boolean paid = false;
        if(daysBetween > 5) {
            String userInput = displayYesNoQuestion("Would you like to avail of an advance purchase discount? (Y/N)\nYou will receive 5% off the cost of your reservation by paying in full today and your booking is refundable up to 5 days before arrival.");
            if(userInput.equalsIgnoreCase("y")) {
                advancedPurchase = true;
                paid = true;
            }
            refundable = true;
        } else {
            System.out.println("You must pay in full today as your reservation is imminent. Your booking cannot be refunded.");
            paid = true;
        }
        person.makeReservation(name, email, advancedPurchase, refundable, checkInDate, numNights, roomsReserved, paid);
    }

    public static void displayFirstTimeMenu() {
        System.out.println("Welcome to the new MegaCorp(C) Hotel Management System, specially designed for all Megacorp(C) Hotels!\n" +
                "No hotels have been created yet - login as the hotel manager to get started! These credentials were sent to you in the system welcome email.");
        HotelManager hotelManager = (HotelManager) login();
        System.out.println("As this is the first time using the system, you need to register the hotel.");
        Hotel hotel = createHotel();
        hotel.addEmployee(hotelManager);
        addHotelToSystem(hotel);
    }

    private static void addHotelToSystem(Hotel hotel) {
        if(HotelSystem.getInstance().addHotel(hotel)) {
            System.out.println("Successfully created the hotel!");
        } else {
            System.out.println("Sorry, an error occurred and the hotel was not added.");
        }
    }

    private static HotelManager createManager(String email) {
        System.out.println("Enter manager name");
        String name = getInput();
        System.out.println("Enter manager salary");
        double salary = getNumberInput("Enter a number value greater than 0 only, either whole or as a decimal.", false);
        return new HotelManager(name, email, salary);
    }

    private static Employee login() {
        while(true) {
            Employee employee = inputAndCheckCredentials();
            if (employee != null) {
                HotelSystem.getInstance().setCurrentUser(employee);
                System.out.println("You have successfully logged in!");
                return employee;
            } else {
                System.out.println("Those credentials do not match our records, please try again!");
            }
        }
    }

    static String getInput() {
        String userInput = sc.nextLine();
        if (userInput.equalsIgnoreCase("q")) {
            exit(0);
        }
        return userInput;
    }

    private static Date getValidDate() {
        Date date;
        while (true) {
            try {
                String userInput = getInput();
                date = SystemUtils.getDateFormat().parse(userInput);
                break;
                // Checked exception ParseException may occur when parsing a date in incorrect format, e.g. 2024-20-12,
                // must catch it or declare it in the method signature
            } catch (ParseException e) {
                System.err.println("Please enter a valid date!");
            }
        }
        return date;
    }

    private static Hotel createHotel() {
        System.out.println("What is the name of the hotel?");
        String userInput = getInput();
        Hotel hotel = new Hotel(userInput);
        System.out.println("When did this hotel open? Please enter the date in YYYY-MM-DD format");
        hotel.setOpenDate(getValidDate());
        System.out.println("How many rooms does this hotel have overall? Enter a whole number greater than 0.");
        int numRooms = (int) getNumberInput("Enter a whole number greater than 0.", true);
        hotel.setNumRooms(numRooms);
        ArrayList<Room> rooms = new ArrayList<>(numRooms);
        for(RoomType roomType : RoomType.values()) {
            userInput = displayYesNoQuestion(STR."Does this hotel have \{roomType.toString().toLowerCase()} rooms? Y/N\t\t");
            if(userInput.equalsIgnoreCase("y")) {
                System.out.println("How much does this type of room cost per night? Enter a number value greater than 0 only, either whole or as a decimal.");
                double cost = getNumberInput("Enter a number value greater than 0 only, either whole or as a decimal.", false);
                hotel.addRoomType(roomType, cost);
                System.out.println("How many rooms of this type are in the hotel?");
                numRooms = (int) getNumberInput("Enter a whole number greater than 0.", true);
                String autoNumberRooms = displayYesNoQuestion("Do you want the rooms to be automatically numbered based on their floor? Y/N\nE.g Rooms on floor 1 will be 101, 102, rooms on floor 2 will be 201, 202, etc.");
                while (!userInput.equalsIgnoreCase("n") && !userInput.matches("^[1-9]\\d*$")) {
                    System.out.println("Enter the floor number if all the rooms of this type are on the same floor or N if they're not, and then you will need to manually enter the floor for each room.");
                    userInput = getInput();
                }
                int num;
                for(int i = 0; i < numRooms; i++) {
                    Room room = new Room(roomType, roomType.getOccupancy(), cost);
                    if(userInput.equalsIgnoreCase("n")) {
                        System.out.println(STR."Enter the floor number for room \{i} of this type.");
                        num = (int) getNumberInput("Enter a whole number greater than 0.", true);
                    } else {
                        try {
                            num = Integer.parseInt(userInput);
                        } catch(NumberFormatException e) {
                            num = 1;
                        }
                    }
                    room.setFloorNum(num);
                    if(autoNumberRooms.equalsIgnoreCase("n")) {
                        System.out.println("Enter the room number for the room.");
                        num = (int) getNumberInput("Enter a whole number greater than 0.", true);
                    } else {
                        // num is currently set to the floor number from above code, multiply by 100 so first floor = 100,
                        // second floor = 200, etc. which each room on the floor incrementing by 1
                        num = (num * 100) + i;
                    }
                    room.setRoomNumber(num);
                    rooms.add(room);
                }
            }
        }
        hotel.setRooms(rooms);
        return hotel;
    }

    // overloaded getNumberInput method which defaults to not allowing 0 as input
    private static double getNumberInput(String errorText, boolean getInt) {
        return getNumberInput(errorText, getInt, false);
    }

    private static double getNumberInput(String errorText, boolean getInt, boolean allowZero) {
        double value;
        while (true) {
            try {
                value = sc.nextDouble();
                sc.nextLine();
                if((!allowZero && value == 0) || value < 0 || (getInt && (int) value != value)) {
                    System.out.println(errorText);
                    continue;
                }
                break;  // get out of the loop
                // Unchecked exceptions possible here - catch error if user enters input of incorrect type/range
            } catch (InputMismatchException mme) {
                System.out.println(errorText);
                sc.nextLine();
            }
        }
        return value;
    }

    private static String displayYesNoQuestion(String question) {
        String userInput = "";
        while(!userInput.equalsIgnoreCase("y") && !userInput.equalsIgnoreCase("n")) {
            System.out.println(question);
            userInput = getInput();
            if(userInput.equalsIgnoreCase("q")) {
                System.exit(0);
            }
        }
        return userInput;
    }

    private static Employee inputAndCheckCredentials() {
        System.out.println("Enter email: ");
        String email = getInput();
        System.out.println("Enter password: ");
        String password = getInput();
        return SystemUtils.validateAndGetEmployee(email, password);
    }

    private static List<String> getOptions(int numValues) {
        List<Integer> intValues = IntStream.range(1, numValues + 1).boxed().toList();
        return new ArrayList<>(intValues.stream().map(Object::toString).toList());
    }
}
