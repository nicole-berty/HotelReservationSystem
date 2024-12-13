package system;

import people.Employee;
import people.HotelManager;
import people.HotelReceptionist;
import reservations.Reservation;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class SystemUtils {
    /**
     *
     * @param fileName the name of the file to be created or retrieved
     * @return the file if file is created or already exists, null otherwise
     */
    public static File getOrCreateFile(String fileName) {
        File file = new File(fileName);
        try {
            if (file.createNewFile()) {
                // Java22 Feature: String templates for easier string interpolation
                System.out.println(STR."File created: \{file.getName()}");
            }
            return file;
            // Checked exception IOException may occur when creating, opening or reading files, must catch it or declare
            // it in the method signature
        } catch (IOException e) {
            System.out.println(STR."An error occurred when creating the file \{file.getName()}.");
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> readFileAsString(String fileName) {
        List<String> fileContents = new ArrayList<>();
        try {
            fileContents = List.of(Files.readString(Paths.get(fileName)).split("\n"));
        } catch (IOException e) {
            System.out.println(STR."Failed to read file \{fileName}, please try again.");
        }
        return fileContents;
    }

    public static int getNumLinesInFile(String fileName) {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
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
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            // LVT1 => local variable type inferred
            var result = stream.filter(line -> Arrays.asList(line.split("\\|")).contains(searchValue)).findFirst();
            return Arrays.asList(result.orElse("").split("\\|"));
        } catch (IOException e) {
            return null;
        }
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
        if(getOrCreateFile(fileDetails.path()) != null) {
            String dataWithHeaders = "";
            // if the file is missing headers or if we're going to overwrite instead of append, add headers to data
            if(isMissingHeaders(fileDetails.path()) || !append) {
                dataWithHeaders = fileDetails.headers() + data;
            } else {
                dataWithHeaders = data;
            }
            // try-with-resources statement means we don't need to close the resources
            try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileDetails.path(), append)))) {
                out.println(dataWithHeaders);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Employee validateAndGetEmployee(String email, String password) {
        File credentialsFile = SystemUtils.getOrCreateFile(HotelSystem.getInstance().dataFiles.get("credentials").path());
        if(credentialsFile != null) {
            // data is a list of (email, password, hotel, Person) from the credentials csv file or null if credentials not found
            // LVT1 => local variable type inferred here
            var data = readAndSearchFile(credentialsFile.getPath(), email);
            // Unchecked exceptions possible here - avoid NullPointerException and ArrayIndexOutOfBoundsException by
            // checking nullity and list length before attempting access
            if(data != null && data.size() > 2 && email.equals(data.get(0)) && password.equals(data.get(1))
                    && HotelSystem.getInstance().getSelectedHotel().getName().equals(data.get(2))) {
                ArrayList<Employee> employees = DataFileParser.getEmployeesFromPeopleList(STR."[\{data.get(3)}]");
                for(Employee employee : employees) {
                    if(employee instanceof HotelManager) {
                        return new HotelManager(employee.getName(), email, employee.getSalary(),
                                ((HotelManager) employee).getManagedDepartments(),
                                ((HotelManager) employee).getStaffUnderManagement());
                    } else if(employee instanceof HotelReceptionist) {
                        return new HotelReceptionist(employee.getName(), email, employee.getSalary(),
                                ((HotelReceptionist) employee).getAssignedCheckInCounter());
                    }
                }
            }
        }
        return null;
    }

    public static String getDateStringOrNull(Date date) {
        return date == null ? null : getDateFormat().format(date);
    }

    public static Date getFormattedDateOrNull(String date) {
        try {
            return getDateFormat().parse(date);
        } catch (ParseException _) {

        }
        return null;
    }

    public static DateFormat getDateFormat() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // set DateFormat field lenient to false so invalid months will not be allowed
        dateFormat.setLenient(false);
        return dateFormat;
    }
}
