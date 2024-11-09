import hotel.Hotel;
import hotel.RoomType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.IntStream;

import static java.lang.System.exit;

public class SystemMenu {
    static Scanner sc = new Scanner(System.in);

    static void displayStartMenu() {
        List<String> validValues = getOptions(2);
        System.out.println("Enter the option you would like to access or press q to exit.");
        System.out.println("(1) Employee Login");
        System.out.println("(2) Guest Login");
        System.out.println("(q) Exit");

        String userInput = sc.nextLine();
        while (!validValues.contains(userInput.toLowerCase())) {
            System.out.println("Please enter a valid option!");
            userInput = sc.nextLine();
        }
        switch (userInput.toLowerCase()) {
            case "1":
                userInput = displayEmployeeLogin();
                break;
            case "2":
                System.out.println("Enter guest surname:");
                userInput = sc.nextLine();
                // TODO: validate credentials
                System.out.println("Enter reservation number:");
                userInput = sc.nextLine();
                // TODO: Add guest name below
                System.out.println("Welcome back, guest!");
                break;
            case "q":
                exit(0);
        }
    }

    static void displayFirstTimeMenu() {
        System.out.println("Welcome to the new MegaCorp© Hotel Management System, specially designed for all Megacorp© Hotels!\n" +
                "No hotels have been created yet - login as the hotel administrator to get started! These credentials were sent to you in the system welcome email.");
        displayEmployeeLogin();
        System.out.println("What is the name of this hotel?");
        String userInput = sc.nextLine();
        Hotel hotel = new Hotel(userInput);
        System.out.println("When did this hotel open? Please enter the date in YYYY-MM-DD format");
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        while (true) {
            try {
                userInput = sc.nextLine();
                Date date = format.parse(userInput);
                hotel.setOpenDate(date);
                break;
            } catch (ParseException e) {
                System.err.println("Please enter a valid date!");
            }
        }
        System.out.println("Which types of rooms does this hotel have? Please enter a comma separated list of options, e.g. 1,2,3");
        for(RoomType roomType : RoomType.values()) {
            userInput = "";
            while(!userInput.equalsIgnoreCase("y") && !userInput.equalsIgnoreCase("n")) {
                System.out.printf("Does this hotel have %s rooms? Y/N\t\t", roomType.toString().toLowerCase());
                userInput = sc.nextLine();
            }
        }
    }

    private static String displayEmployeeLogin() {
        System.out.println("Enter username:");
        String userInput = sc.nextLine();
        // TODO: validate credentials
        System.out.println("Enter password:");
        userInput = sc.nextLine();
        System.out.println("You have successfully logged in!");
        return userInput;
    }

    private static List<String> getOptions(int numValues) {
        List<Integer> intValues = IntStream.range(0, numValues).boxed().toList();
        List<String> options = new ArrayList<>(intValues.stream().map(Object::toString).toList());
        options.add("q");
        return options;
    }
}
