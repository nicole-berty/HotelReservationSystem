package hotel;

import java.io.*;

public class HotelSystem {
    private static String fileName = "MegaCorpHotels.csv";
    public static void add(Hotel hotel) throws IOException {
        File hotelFile = new File(fileName);
        if(!hotelFile.exists()) {
            createFile(hotelFile);
        }
        addHotelToFile(hotel);
    }

    private static void addHotelToFile(Hotel hotel) throws IOException {
        FileWriter fw = new FileWriter(fileName, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(bw);
        out.println(hotel.toCsvString());
    }

    private static void createFile(File hotelFile) {
        try {
            if (hotelFile.createNewFile()) {
                System.out.println(STR."File created: \{hotelFile.getName()}");
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
