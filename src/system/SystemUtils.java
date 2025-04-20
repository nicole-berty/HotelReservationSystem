package system;

import hotel.Hotel;
import people.Employee;
import people.HotelManager;
import people.HotelReceptionist;
import reservations.Reservation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class SystemUtils {
    /**
     *
     * @param fileName the name of the file to be created or retrieved
     * @return the file if file is created or already exists, null otherwise
     */
    public static Path getOrCreatePath(String fileName) { // 9 - NIO2: Create or retrieve file using Path and Files
        Path path = Paths.get(fileName); // 9 - NIO2
        try {
            if (Files.notExists(path)) {
                Files.createFile(path); // 9 - NIO2
                System.out.println(STR."File created: \{path.getFileName()}");
            }
            return path;
        } catch (IOException e) {
            System.out.println(STR."An error occurred when creating the file \{fileName}.");
            e.printStackTrace();
            return null;
        }
    }


    public static List<String> readFileAsString(String fileName) {
        List<String> fileContents = new ArrayList<>();
        try {
            fileContents = List.of(Files.readString(Paths.get(fileName)).split("\n")); // 9 - NIO2
        } catch (IOException e) {
            System.out.println(STR."Failed to read file \{fileName}, please try again.");
        }
        return fileContents;
    }

    public static int getNumLinesInFile(String fileName) {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) { // 9 - NIO2
            return (int) stream.count();
        } catch (IOException e) {
            return 0;
        }
    }

    public static boolean isMissingHeaders(String filePath) {
        int lineCount = SystemUtils.getNumLinesInFile(filePath);
        // if less than 1 line in the file, there's no headers
        return lineCount < 1;
    }

    public static List<String> readAndSearchFile(String fileName, String searchValue) {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) { // 9 - NIO2
            // LVTI => local variable type inferred
            var result = stream.filter(line -> Arrays.asList(line.split("\\|")).contains(searchValue)).findFirst();
            return Arrays.asList(result.orElse("").split("\\|"));
        } catch (IOException e) {
            return null;
        }
    }

    public static String getModifiedHotelsList(Hotel updatedHotel) {
        List<String> fileContents = SystemUtils.readFileAsString(HotelSystem.getInstance().dataFiles.get("hotels").path());
        StringBuilder hotels = new StringBuilder();
        // start from first line of file to skip headers
        for(int i = 1; i < fileContents.size(); i++) {
            Hotel hotel = DataFileParser.parseHotelData(fileContents.get(i));
            if(hotel.getName().equals(updatedHotel.getName())) {
                hotel = new Hotel(updatedHotel);
            }
            hotels.append(hotel.toCsvString());
            // append new line on each line except the last one to avoid unnecessary whitespace in file
            if(i != fileContents.size() - 1) {
                hotels.append("\n");
            }
        }
        return hotels.toString();
    }

    public static String getModifiedReservationList(Reservation updatedReservation) {
        List<String> fileContents = SystemUtils.readFileAsString(HotelSystem.getInstance().dataFiles.get("reservations").path());
        StringBuilder reservations = new StringBuilder();
        // start from first line of file to skip headers
        for(int i = 1; i < fileContents.size(); i++) {
            Reservation reservation1 = DataFileParser.parseReservationData(fileContents.get(i));
            if(reservation1.getReservationId().equals(updatedReservation.getReservationId())) {
                reservation1 = new Reservation(updatedReservation);
            }
            reservations.append(reservation1);
            // append new line on each line except the last one to avoid unnecessary whitespace in file
            if(i != fileContents.size() - 1) {
                reservations.append("\n");
            }
        }
        return reservations.toString();
    }

    // overloaded writeToFile method to default the append value to true
    public static boolean writeToFile(FileDetails fileDetails, String data) {
        return writeToFile(fileDetails, data, true);
    }

    public static boolean writeToFile(FileDetails fileDetails, String data, boolean append) {
        Path path = getOrCreatePath(fileDetails.path());
        if (path != null) {
            String dataWithHeaders = isMissingHeaders(fileDetails.path()) || !append
                    ? fileDetails.headers() + data
                    : data;
            try {
                Files.writeString(path, dataWithHeaders + System.lineSeparator(),
                        StandardOpenOption.CREATE,
                        append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public static Employee validateAndGetEmployee(String email, String password) {
        Path credentialsPath = getOrCreatePath(HotelSystem.getInstance().dataFiles.get("credentials").path());
        if(credentialsPath != null) {
            // data is a list of (email, password, hotel, Person) from the credentials csv file or null if credentials not found
            // LVTI => local variable type inferred here
            var data = readAndSearchFile(credentialsPath.toString(), email);
            // Unchecked exceptions possible here - avoid NullPointerException and ArrayIndexOutOfBoundsException by
            // checking nullity and list length before attempting access
            if(data != null && data.size() > 2 && email.equals(data.get(0)) && password.equals(data.get(1))
                    && HotelSystem.getInstance().getSelectedHotel().getName().equals(data.get(2))) {
                ArrayList<Employee> employees = DataFileParser.getEmployeesFromPeopleList(STR."[\{data.get(3)}]");
                for(Employee employee : employees) {
                    // 3 - Switch expressions and pattern matching
                    // 4 Sealed Classes and Interfaces - exhaustive checking in switch statement
                    switch (employee) {
                        case HotelManager hotelManager -> {
                            return hotelManager;
                        }
                        case HotelReceptionist hotelReceptionist -> {
                            return hotelReceptionist;
                        }
                    }
                }
            }
        }
        return null;
    }

    // 5 - Date/Time API
    public static String getDateStringOrNull(Temporal date) {
        return date == null ? null : (date instanceof LocalDate) ? getDateFormatter().format(date)
                : getDateTimeFormatter().format(date);
    }

    public static LocalDate getFormattedDateOrNull(String date) {
        try {
            return LocalDate.parse(date, getDateFormatter());
        } catch (DateTimeParseException _) { // Java 22 - Unnamed variables
            return null;
        }
    }

    public static LocalDateTime getFormattedDateTimeOrNull(String date) {
        try {
            // 5 - Date/Time API
            return LocalDateTime.parse(date, getDateTimeFormatter());
        } catch (DateTimeParseException _) { // Java 22 - Unnamed variables
            return null;
        }
    }

    public static DateTimeFormatter getDateTimeFormatter() {
        // 'DateTimeFormatter' is lenient by default, it does not allow invalid dates
        return DateTimeFormatter.ofPattern("uuuu-MM-dd hh:mm").withResolverStyle(ResolverStyle.STRICT);
    }

    public static DateTimeFormatter getDateFormatter() {
         // 'DateTimeFormatter' is lenient by default, it does not allow invalid dates
        return DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
    }
}
