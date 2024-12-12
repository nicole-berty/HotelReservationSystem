package system;

import hotel.Hotel;
import hotel.Room;
import hotel.RoomType;
import people.HotelManager;

import java.text.ParseException;
import java.util.*;
import java.util.stream.IntStream;

import static java.lang.System.exit;

public class SystemMenu {
    static Scanner sc = new Scanner(System.in);

    public static void displayStartMenu() {
        List<String> validValues = getOptions(2);
        System.out.println("Enter the option you would like to access or press q to exit.");
        System.out.println("(1) Employee Login");
        System.out.println("(2) Guest Login");
        System.out.println("(q) Exit");

        String userInput = getInput();
        while (!validValues.contains(userInput.toLowerCase())) {
            System.out.println("Please enter a valid option!");
            userInput = getInput();
        }
        switch (userInput.toLowerCase()) {
            case "1":
                login();
                break;
            case "2":
                System.out.println("Enter guest surname:");
                userInput = getInput();
                // TODO: validate credentials
                System.out.println("Enter reservation number:");
                userInput = getInput();
                // TODO: Add guest name below
                System.out.println("Welcome back, guest!");
                break;
        }
    }

    public static void displayFirstTimeMenu() {
        System.out.println("Welcome to the new MegaCorp(C) Hotel Management System, specially designed for all Megacorp(C) Hotels!\n" +
                "No hotels have been created yet - login as the hotel manager to get started! These credentials were sent to you in the system welcome email.");
        login();
        System.out.println("As this is the first time using the system, you need to register the hotel.");
        Hotel hotel = createHotel();
        System.out.println("Let's initialise you on the system.");
        HotelManager hotelManager = createManager(1, "megacorpmanager1@gmail.com");
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

        private static HotelManager createManager(int id, String email) {
        System.out.println("Enter your name");
        String name = getInput();
        System.out.println("Enter your salary");
        double salary = getNumberInput("Enter a number value greater than 0 only, either whole or as a decimal.", false);
        return new HotelManager(name, email, id, salary);
    }

    private static void login() {
        boolean loggedIn = false;
        while(!loggedIn) {
            loggedIn = inputAndCheckCredentials();
            if (loggedIn) {
                System.out.println("You have successfully logged in!");
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

    private static Hotel createHotel() {
        System.out.println("What is the name of the hotel?");
        String userInput = getInput();
        Hotel hotel = new Hotel(userInput);
        System.out.println("When did this hotel open? Please enter the date in YYYY-MM-DD format");
        while (true) {
            try {
                userInput = getInput();
                Date date = SystemUtils.getDateFormat().parse(userInput);
                hotel.setOpenDate(date);
                break;
                // Checked exception ParseException may occur when parsing a date in incorrect format, e.g. 2024-20-12,
                // must catch it or declare it in the method signature
            } catch (ParseException e) {
                System.err.println("Please enter a valid date!");
            }
        }
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

    private static double getNumberInput(String errorText, boolean getInt) {
        double value;
        while (true) {
            try {
                value = sc.nextDouble();
                sc.nextLine();
                if(value <= 0 || (getInt && (int) value != value)) {
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

    private static boolean inputAndCheckCredentials() {
        System.out.println("Enter username:");
        String email = getInput();
        System.out.println("Enter password:");
        String password = getInput();
        return SystemUtils.validateCredentials(email, password);
    }

    private static List<String> getOptions(int numValues) {
        List<Integer> intValues = IntStream.range(0, numValues).boxed().toList();
        return new ArrayList<>(intValues.stream().map(Object::toString).toList());
    }
}
