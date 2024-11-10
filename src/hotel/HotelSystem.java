package hotel;

import system.SystemMenu;
import system.SystemUtils;

import java.io.*;

public class HotelSystem {
    public static final String hotelCredentials = "./src/data/ValidCredentials.csv";
    private final String hotelFileName = "./src/data/MegaCorpHotelReservations.csv";
    private boolean initialised = false;

    public void initialise() {
        SystemUtils.getOrCreateFile(hotelFileName);
        int lineCount = SystemUtils.getNumLinesInFile(hotelFileName);
        if(lineCount <= 1) {
            if(lineCount == 0) {
                SystemUtils.writeToFile(hotelFileName, "Hotel Name,Open Date,Number of Rooms,Room Types,");
            }
            SystemMenu.displayFirstTimeMenu();
        } else {
            initialised = true;
            SystemMenu.displayStartMenu();
        }
    }

    boolean isInitialised() {
        return initialised;
    }

    public void add(Hotel hotel) throws IOException {
        File hotelFile = SystemUtils.getOrCreateFile(hotelFileName);
        addHotelToFile(hotel);
    }

    private void addHotelToFile(Hotel hotel) throws IOException {
        FileWriter fw = new FileWriter(hotelFileName, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(bw);
        out.println(hotel.toCsvString());
    }
}
