package system;

import hotel.HotelSystem;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class SystemUtils {
    /**
     *
     * @param fileName
     * @return true if file created or already exists, false otherwise
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

    public static int getNumLinesInFile(String fileName) {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            return (int) stream.count();
        } catch (IOException e) {
            return 0;
        }
    }

    public static List<String> readAndSearchFile(String fileName, String searchValue) {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            var result = stream.filter(line -> Arrays.asList(line.split(",")).contains(searchValue)).findFirst();
            return result.stream().toList();
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean writeToFile(String fileName, String data) {
        // try-with-resources statement means we don't need to close the resources
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
            out.println(data);
            return true;
        }catch (IOException e) {
            System.err.println(e);
        }
        return false;
    }

    public static boolean validateCredentials(String email, String password) {
        File credentialsFile = SystemUtils.getOrCreateFile(HotelSystem.hotelCredentials);
        if(credentialsFile != null) {
            // data is a list of (id, email, password) from the credentials csv file
            List<String> data = readAndSearchFile(HotelSystem.hotelCredentials, email);
            return data != null && data.size() > 2 && email.equals(data.get(1)) && password.equals(data.get(2));
        }
        return false;
    }
}
