package system;

import hotel.HotelSystem;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    public static File getOrCreateFile(String fileName) {
        File file = new File(fileName);
        try {
            if (file.createNewFile()) {
                System.out.println(STR."File created: \{file.getName()}");
            }
            return file;
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

    public static List<String> readAndSearchFile(String fileName, String searchValue) {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            // LVT1 => local variable type inferred
            var result = stream.filter(line -> Arrays.asList(line.split(",")).contains(searchValue)).findFirst();
            return Arrays.asList(result.orElse("").split(","));
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean writeToFile(String fileName, String data) {
        if(getOrCreateFile(fileName) != null) {
        // try-with-resources statement means we don't need to close the resources
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
            out.println(data);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
        return false;
    }

    public static boolean validateCredentials(String email, String password) {
        File credentialsFile = SystemUtils.getOrCreateFile(HotelSystem.hotelCredentials);
        if(credentialsFile != null) {
            // data is a list of (id, email, password) from the credentials csv file or null if credentials not found
            var data = readAndSearchFile(HotelSystem.hotelCredentials, email);
            // avoid NullPointerException and ArrayIndexOutOfBoundsException by checking nullity and list length
            return data != null && data.size() > 2 && email.equals(data.get(1)) && password.equals(data.get(2));
        }
        return false;
    }

    public static DateFormat getDateFormat() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // set DateFormat field lenient to false so invalid months will not be allowed
        dateFormat.setLenient(false);
        return dateFormat;
    }
}
