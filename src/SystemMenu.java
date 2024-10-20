import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
