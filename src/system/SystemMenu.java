package system;

import hotel.Hotel;
import hotel.Room;
import hotel.RoomType;
import people.Customer;
import people.Employee;
import people.HotelManager;
import people.Person;
import pricing.PricingStrategy;
import reservations.InvoiceService;
import reservations.Reservation;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.System.exit;

public class SystemMenu {
    static Scanner sc = new Scanner(System.in);

    public static void displayHotelSelectionMenu() {
        List<String> validValues = getOptions(HotelSystem.getInstance().hotels.size());
        System.out.println(I18n.get("welcome")); // 10 - Localisation using resource bundle
        System.out.println(STR."""
            Please select the hotel you want to access or press q to exit.
            Current time: \{SystemUtils.getDateStringOrNull(HotelSystem.currentTime.get())}""");

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
        System.out.println("(3) Go Back");
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
                    displayEmployeeMainMenu(employee);
                }
                break;
            case "2":
                System.out.println("Welcome! Please enter your email: ");
                String email = getInput();
                Customer customer = new Customer(userInput, email);
                displayMainMenu(customer);
                break;
            case "3":
                displayHotelSelectionMenu();
                break;
        }
    }

    public static void displayEmployeeMainMenu(Employee employee) {
        //noinspection InfiniteLoopStatement - false positive warning, we only want to exit if user input is q
        while(true) {
            List<String> validValues = getOptions(3);
            System.out.println("Enter the option you would like to access or press q to exit.");
            System.out.println("(1) Reservations & Cancellations");
            System.out.println("(2) Check in / Check out");
            System.out.printf("(3) %s%n", (employee instanceof HotelManager ? "Go Back" : "Logout"));
            System.out.println("(q) Exit");
            String userInput = getInput();
            while (!validValues.contains(userInput.toLowerCase())) {
                System.out.println("Please enter a valid option!");
                userInput = getInput();
            }
            switch (userInput.toLowerCase()) {
                case "1":
                    displayMainMenu(employee);
                    break;
                case "2":
                    String reservation = findReservation();
                    if (!reservation.isBlank()) {
                        Reservation reservation1 = DataFileParser.parseReservationData(reservation);
                        System.out.println("(1) Check In (2) Check out");
                        userInput = getInput();
                        while (!validValues.contains(userInput.toLowerCase())) {
                            System.out.println("Please enter a valid option!");
                            userInput = getInput();
                        }
                        // 3 - Switch expressions and pattern matching
                        switch (userInput) {
                            case "1" -> employee.checkIn(reservation1);
                            case "2" -> employee.checkOut(reservation1);
                        }
                    } else {
                        System.out.println("Reservation not found!");
                    }
                    break;
                case "3":
                    // 3 - Switch expressions and pattern matching
                    if(employee instanceof HotelManager hotelManager) {
                        displayMainManagerMenu(hotelManager);
                    } else {
                        displayStartMenu();
                    }
                    break;
            }
        }
    }


    public static void displayMainManagerMenu(HotelManager manager) {
        //noinspection InfiniteLoopStatement - false positive warning, we only want to exit if user input is q
        while(true) {
            List<String> validValues = getOptions(5);
            System.out.println("Enter the option you would like to access or press q to exit.");
            System.out.println("(1) Reservations, Cancellations & Check In/out");
            System.out.println("(2) Apply Discounts");
            System.out.println("(3) Adjust Pricing Strategy");
            System.out.println("(4) Invoices");
            System.out.println("(5) Logout");
            System.out.println("(q) Exit");
            String userInput = getInput();
            while (!validValues.contains(userInput.toLowerCase())) {
                System.out.println("Please enter a valid option!");
                userInput = getInput();
            }
            switch (userInput.toLowerCase()) {
                case "1":
                    displayEmployeeMainMenu(manager);
                    break;
                case "2":
                    String reservation = findReservation();
                    if (!reservation.isBlank()) {
                        Reservation reservation1 = DataFileParser.parseReservationData(reservation);
                        System.out.println(STR."The total cost of this reservation is \{reservation1.getPricingStrategy()
                                .displayFormattedPrice(reservation1.getTotalCost())}.");
                        System.out.println("How much of a % discount would you like to apply?");
                        double discount = getNumberInput("Please enter a number greater than 0.", false);
                        manager.giveDiscount(reservation1, discount);
                    }
                    break;
                case "3":
                    String hotel = findHotel();
                    if(hotel.isBlank()) {
                        System.out.println("There was an issue obtaining the hotel details. Please try again later");
                    } else {
                        Hotel hotel1 = DataFileParser.parseHotelData(hotel);
                        System.out.println(STR."Which pricing strategy would you like the hotel to use from now on? \nIt currently uses the \{hotel1.getPricingStrategy()} strategy.");
                        System.out.println("(1) Regular Pricing (2) Promotional Pricing (3) Corporate Pricing (4) Seasonal Pricing (B) Go Back");
                        List<String> newValidOptions = getOptions(4);
                        newValidOptions.add("b");
                        userInput = getInput();
                        while (!newValidOptions.contains(userInput.toLowerCase())) {
                            System.out.println("Please enter a valid option!");
                            userInput = getInput();
                        }
                        if(!userInput.equalsIgnoreCase("b")) {
                            // 3 - Switch expressions and pattern matching
                            String strategy = switch (userInput) {
                                case "2" -> "promotional";
                                case "3" -> "corporate";
                                case "4" -> "seasonal";
                                default -> "regular";
                            };
                            PricingStrategy pricingStrategy = PricingStrategy.fromString(strategy);
                            hotel1.setPricingStrategy(pricingStrategy);
                            manager.changePricingStrategy(hotel1);
                        }
                    }
                    break;
                case "4":
                    System.out.println("Generating invoices for all reservations...");
                    // 8 - Concurrency, using ExecutorService to process a list of Callable's
                    InvoiceService invoiceService = new InvoiceService();
                    invoiceService.generateInvoicesConcurrently(manager.retrieveAllReservations());

                    // Shutdown the ExecutorService after processing
                    invoiceService.shutdown();
                    break;
                case "5":
                    displayStartMenu();
                    break;
            }
        }
    }

    private static String findHotel() {
        // Provide a default empty list in case method returned null
        return Optional.ofNullable(SystemUtils.readAndSearchFile(HotelSystem.getInstance().dataFiles.get("hotels").path(), HotelSystem.getInstance().getSelectedHotel().getName()))
                .orElse(List.of()) // Provide a default empty list in case method returned null
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.joining("|"));
    }

    private static String findReservation() {
        System.out.println("Enter the reservation number you're looking for.");
        String userInput = getInput();
        boolean foundReservation;
        var data = SystemUtils.readAndSearchFile(HotelSystem.getInstance().dataFiles.get("reservations").path(), userInput);
        foundReservation = data != null && !data.isEmpty() && data.getFirst().equals(userInput);
        while (!foundReservation) {
            System.out.println("Reservation not found! Please try again, or press b to go back or q to quit.");
            userInput = getInput();
            if(userInput.equalsIgnoreCase("b")) {
                return "";
            }
            data = SystemUtils.readAndSearchFile(HotelSystem.getInstance().dataFiles.get("reservations").path(), userInput);
            foundReservation = data != null && !data.isEmpty() && data.getFirst().equals(userInput);
        }
        return Optional.of(data)
                .orElse(List.of()) // Provide a default empty list in case method returned null
                .stream()
                .filter(Objects::nonNull) // 2 - Streams - Intermediate Operations: filter()
                .collect(Collectors.joining("|")); // 2 - Streams - Terminal Operations: collect()
    }

    public static void displayMainMenu(Person person) {
        //noinspection InfiniteLoopStatement - false positive warning, we only want to exit if user input is q
        while(true) {
            List<String> validValues = getOptions(4);
            System.out.println("Enter the option you would like to access or press q to exit.");
            System.out.println("(1) Make a reservation");
            System.out.println("(2) Make a cancellation");
            System.out.println("(3) View Reservations");
            System.out.println("(4) Go Back");
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
                    String reservation = findReservation();
                    if(!reservation.isBlank()) {
                        cancelReservation(person, reservation);
                    }
                    break;
                case "3":
                    // 1 - Lambdas: Function
                    Function<Reservation, String> reservationSummary = res ->
                            STR."Reservation ID: \{res.getReservationId()} | Total Cost: €\{String.format("%.2f", res.getTotalCost())} | Check In Date: \{res.getCheckInDate().toString()} Email: \{res.getEmail()} | Rooms\{res.getReservedRoomCounts()}";
                    Function<Room, String> roomSummary = room ->
                            STR."Room Number: \{room.getRoomNumber()} | Cost: €\{String.format("%.2f", room.getCost())} | Type: \{room.getRoomType()} Occupied: \{room.isOccupied()}";
                    if(person instanceof Employee) {
                        System.out.println("View Reservations (1) All (2) By Check In Date (3) By Email (4) Cancelled (5) Completed Stays (6) Reservation Statistics");
                        userInput = getInput();
                        List<String> innerValidValues = getOptions(6);
                        while (!innerValidValues.contains(userInput.toLowerCase())) {
                            System.out.println("Please enter a valid option!");
                            userInput = getInput();
                        }
                        switch (userInput) {
                            case "1":
                                System.out.print(HotelSystem.getInstance().dataFiles.get("reservations").headers());
                                Consumer<Reservation> printReservation = res -> System.out.println(res); // 1 - Lambdas: Consumer
                                person.retrieveAllReservations().stream().limit(10) // 2 - Streams - Intermediate Operations: limit()
                                        .forEach(printReservation); // 2 - Streams - Terminal Operations: for each
                                System.out.println("**Only 10 reservations are shown for readability**");
                                break;
                            case "2":
                                System.out.println("Enter a date in the format YYYY-MM-DD.");
                                LocalDate selectedDate = getValidDate();
                                System.out.print(HotelSystem.getInstance().dataFiles.get("reservations").headers());
                                person.retrieveAllReservations().stream().filter(r -> r.getCheckInDate() == selectedDate).forEach(System.out::println);
                                break;
                            case "3":
                                System.out.println("Enter the email you want to get reservations for.");
                                // email is effectively final here, variable used in lambda needs to be final or
                                // effectively final, it's accessed but never modified.
                                String email = getInput();
                                System.out.print(HotelSystem.getInstance().dataFiles.get("reservations").headers());
                                person.retrieveAllReservations().stream().filter(
                                        r -> email.equals(r.getEmail())).forEach(System.out::println);
                                break;
                            case "4":
                                System.out.println("Cancelled reservations:");
                                System.out.print(HotelSystem.getInstance().dataFiles.get("reservations").headers());
                                // predicate used to filter reservations for those which were cancelled
                                Predicate<Reservation> cancellationPredicate = Reservation::isCancelled; // 1 - Lambdas: Predicate
                                person.retrieveAllReservations().stream().filter(cancellationPredicate).forEach(System.out::println);
                                break;
                            case "5":
                                System.out.println("Completed stays:");
                                System.out.print(HotelSystem.getInstance().dataFiles.get("reservations").headers());
                                Predicate<Reservation> completedPredicate = Reservation::isCompleted; // 1 - Lambdas: Predicate
                                // method reference used to filter reservations for those which are completed
                                person.retrieveAllReservations().stream()
                                        .filter(completedPredicate::test).forEach(System.out::println); // 1 - Lambdas: Consumer, forEach expects a consumer, in this case the method reference for println
                                break;
                            case "6":
                                var reservations = person.retrieveAllReservations();
                                Predicate<Reservation> isFromLastQuarter = r ->
                                        r.getCheckInDate().isBefore(ChronoLocalDate.from(HotelSystem.currentTime.get())) // 1 - Lambdas: Supplier to get current time
                                                && r.getCheckInDate().isAfter(ChronoLocalDate.from(HotelSystem.currentTime.get().minusMonths(3))); // 5 - Date/Time API

                                var reservationsLastQuarter = FilterUtil.filter(reservations, isFromLastQuarter);  // 7 - Generics uses in FilterUtil
                                var rooms = HotelSystem.getInstance().getSelectedHotel().getRooms();

                                long resCount = reservations.stream().count(); // 2 - Streams - Terminal Operations: count()
                                Optional<Reservation> cheapestRes = reservations.stream()
                                        .min(Comparator.comparing(Reservation::getTotalCost)); // 2 - Streams - Terminal Operations: min(), 7 - Comparator.comparing
                                Optional<Reservation> mostExpensiveRes = reservations.stream()
                                        .max(Comparator.comparing(Reservation::getTotalCost)); // 2 - Streams - Terminal Operations: max(), 7 - Comparator.comparing
                                boolean allCompleted = reservations.stream().allMatch(res -> res.isCompleted()); // 2 - Streams - Terminal Operations: allMatch()
                                boolean anyCancellations = reservations.stream().anyMatch(res -> res.isCancelled());  // 2 - Streams - Terminal Operations: anyMatch()
                                boolean allRoomsAreCheap = rooms.stream().noneMatch(room -> room.getCost() > 200);  // 2 - Streams - Terminal Operations: noneMatch()
                                Map<Boolean, List<Room>> partitioned = rooms.stream()
                                        .collect(Collectors.partitioningBy(room -> room.getCost() > 200));  // 2 - Streams - Terminal Operations: Collectors.partitioningBy()
                                Predicate<Room> isOccupiedPredicate = Room::isOccupied;
                                Optional<Room> availableRoom = rooms.stream() // optional, so may return null
                                        .filter(Predicate.not(isOccupiedPredicate))  // 1 - Lambdas: Predicate, negation, 2 - Streams - Intermediate Operations: filter()
                                        .findAny();  // 2 - Streams - Terminal Operations: findAny() returns any available room

                                Map<String, Integer> emailToReservationCount = reservations.stream()
                                        .collect(Collectors.toMap( // 2 - Streams - Terminal Operations: Collectors.toMap, map email type to number of reservations made
                                                Reservation::getEmail,
                                                _ -> 1, // Java 22 - Unnamed variables
                                                Integer::sum  // Merge function: sum the counts if same email is encountered
                                        ));

                                // Extract and filter distinct room types from last quarter's reservations
                                List<RoomType> uniqueRoomTypes = reservationsLastQuarter.stream()
                                        .flatMap(res -> res.getRoomsReserved().stream())
                                        .map(Room::getRoomType)
                                        .distinct() // 2 - Streams - Intermediate Operations: distinct()
                                        .toList();


                                System.out.println(STR."Reservation Statistics for \{HotelSystem.getInstance().getSelectedHotel().getName()}:");
                                System.out.println(STR."Reservations Made: \{resCount}");
                                cheapestRes.map(reservationSummary).ifPresent(res -> System.out.println(STR."The cheapest reservation is: \{res}"));
                                mostExpensiveRes.map(reservationSummary).ifPresent(res -> System.out.println(STR."The most expensive reservation is: \{res}"));
                                System.out.println("All reservations are complete? " + allCompleted);
                                System.out.println("Any cancellations? " + anyCancellations);

                                System.out.println("\nHotel Room Statistics");
                                availableRoom.map(roomSummary).ifPresentOrElse(room -> System.out.println("Found an available room: " + room),
                                        () -> System.out.println("There are no rooms available in the hotel right now."));
                                System.out.println("Any expensive rooms, > €200/night? " + !allRoomsAreCheap);
                                System.out.println("Expensive rooms: ");
                                partitioned.get(true).stream().map(roomSummary).forEach(System.out::println);
                                System.out.println("Cheap rooms: ");
                                partitioned.get(false).stream().map(roomSummary).forEach(System.out::println);

                                System.out.println("Unique room types reserved last quarter: " + uniqueRoomTypes);

                                emailToReservationCount.forEach((resEmail, count) ->
                                        System.out.println("Email: " + resEmail + ", Number of Reservations: " + count));
                        }
                    } else {
                        System.out.println("Reservations found for this email shown below.");
                        System.out.print(HotelSystem.getInstance().dataFiles.get("reservations").headers());

                        person.retrieveAllReservations().stream().filter(r -> Objects.equals(r.getEmail(),
                                        person.getEmail())).map(reservationSummary) // 2 - Streams - Intermediate Operations: map()
                                .forEach(System.out::println);
                    }
                    break;
                case "4":
                    // 3 - Switch expressions and pattern matching
                    switch (person) {
                        case Employee employee -> displayEmployeeMainMenu(employee);
                        case Customer _ -> displayStartMenu(); // Java 22 - Unnamed variables and patterns
                    }
                    break;
            }
        }
    }

    private static void cancelReservation(Person person, String reservation) {
        System.out.println("Reservation found.");
        System.out.println(reservation);
        String input = displayYesNoQuestion("Are you sure you want to cancel this reservation? Y/N");
        if(input.equalsIgnoreCase("y")) {
            Reservation reservation1 = DataFileParser.parseReservationData(reservation);
            // Runtime Polymorphism (Dynamic Method Dispatch) - this method is overridden from person class
            // and which version of it is used depends on object type that calls it at runtime
            person.cancelReservation(reservation1);
        } else {
            System.out.println("Aborting. Back to main menu....");
        }
    }

    private static void createReservation(Person person) {
        LocalDate checkInDate = LocalDate.now();
        long hoursBetween = 0L;
        int numNights = 0;
        boolean hasVacancy = false;
        Map<RoomType, List<Room>> availableRoomsByType = new HashMap<>();

        while(!hasVacancy) {
            // checkInDate must be in the future
            while (hoursBetween <= 24) {
                System.out.println("When would you like to stay at the hotel? Please enter the date in YYYY-MM-DD format. The date must be at least 1 day in advance of today.");
                checkInDate = getValidDate();
                hoursBetween = Duration.between(LocalDateTime.now(), checkInDate.atStartOfDay()).toHours();
            }

            System.out.println("How many nights will you stay? Enter a whole number.");
            numNights = (int) getNumberInput("Enter a whole number of nights to stay.", true);

            LocalDate finalCheckInDate = checkInDate;
            LocalDate checkOutDate = checkInDate.plusDays(numNights);
            Set<Integer> occupiedRooms = person.retrieveAllReservations().stream()
                    .filter(res -> res.conflictsWith(finalCheckInDate, checkOutDate))
                    .flatMap(res -> res.getRoomsReserved().stream()) // Flatten the lists of rooms
                    .map(Room::getRoomNumber) // 2 - Streams - Intermediate Operations: map(), to extract room numbers
                    .collect(Collectors.toSet()); // 2 - Streams - Terminal Operations: collect()
            Predicate<Room> isAvailable = room -> !occupiedRooms.contains(room.getRoomNumber());
            availableRoomsByType  = HotelSystem.getInstance().getSelectedHotel()
                    .getRooms().stream()
                    .filter(isAvailable)
                    .collect(Collectors.groupingBy(Room::getRoomType)); // 2 - Streams - Terminal Operations: Collectors.groupingBy, group available rooms by type
            int totalAvailableRooms = availableRoomsByType.values().stream()
                    .mapToInt(List::size)
                    .sum();

            System.out.println(STR."Available rooms for these dates: \{totalAvailableRooms}");
            hasVacancy = totalAvailableRooms > 0;
        }

        System.out.println("How many of each room would you like to book? Enter 0 or more for each room. Only available types will be shown.");
        Set<Room> roomsReserved = new HashSet<>();
        List<Map.Entry<RoomType, List<Room>>> entries = new ArrayList<>(availableRoomsByType.entrySet());

        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<RoomType, List<Room>> entry = entries.get(i);
            RoomType roomType = entry.getKey();
            List<Room> roomsOfType = entry.getValue();
            int availableCount = roomsOfType.size();

            System.out.println("How many **" + roomType + STR." - Occupancy: \{roomType.getOccupancy()} "
                    + STR."- Cost: \{HotelSystem.getInstance().getSelectedHotel().getRoomCost(roomType)}** do you want to reserve? Available: " + availableCount);

            int requestedCount = (int) getNumberInput("Enter a whole number of 0 or more", true, true);

            if (requestedCount > availableCount) {
                System.out.println(STR."Not enough rooms available! Requested: \{requestedCount}, Available: \{availableCount}");
            } else {
                List<Room> bookedRooms = roomsOfType.subList(0, requestedCount);
                roomsReserved.addAll(bookedRooms);
            }

            if(i != entries.size() - 1) {
                String userInput = displayYesNoQuestion("Would you like to see more room types? Y/N");
                if (userInput.equalsIgnoreCase("n")) {
                    break;
                }
            }
        }

        if (roomsReserved.isEmpty()) {
            System.out.println("You didn't reserve any rooms! Going back to main menu...");
        }

        System.out.println("Please enter the name for the booking: ");
        String name = getInput();

        System.out.println("Please enter the email for the booking: ");
        String email = getInput();

        boolean advancedPurchase = false;
        boolean refundable = false;
        boolean paid = false;
        if(hoursBetween > 120) {
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
        int[] additionalCosts = new int[0];
        person.makeReservation(name, email, advancedPurchase, refundable, checkInDate, numNights, roomsReserved, paid, additionalCosts);
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
                System.out.println("You have successfully logged in!");
                return employee;
            } else {
                System.out.println("Those credentials do not match our records, please try again!");
            }
        }
    }

    static String getInput() {
        String userInput = sc.nextLine();
        while(userInput.isEmpty()) {
            System.err.println("Please enter valid input!");
            userInput = sc.nextLine();
        }
        if (userInput.equalsIgnoreCase("q")) {
            exit(0);
        }
        return userInput;
    }

    private static LocalDate getValidDate() {
        LocalDate date;
        while (true) {
            try {
                String userInput = getInput();
                date = LocalDate.parse(userInput, SystemUtils.getDateFormatter());
                break;
                // Checked exception ParseException may occur when parsing a date in incorrect format, e.g. 2024-20-12,
                // must catch it or declare it in the method signature
            } catch (DateTimeParseException e) {
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
                exit(0);
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
