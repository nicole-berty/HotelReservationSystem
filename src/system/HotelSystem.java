package system;

import hotel.Hotel;

import java.util.ArrayList;
import java.util.List;

public class HotelSystem {
    private static HotelSystem instance = null;

    // private constructor so it's only accessible in this class and default constructor won't be created or used,
    // ensuring singleton pattern is adhered to
    private HotelSystem() {}

    // Static method to ensure singleton instance of HotelSystem class
    public static synchronized HotelSystem getInstance()
    {
        if (instance == null)
            instance = new HotelSystem();

        return instance;
    }

    public static final String hotelCredentials = "./src/data/ValidCredentials.csv";
    public static final String hotelsFileName = "./src/data/MegaCorpHotels.csv";
    public static final String reservationsFileName = "./src/data/MegaCorpHotelReservations.csv";
    private boolean guestLogin = true;
    ArrayList<Hotel> hotels = new ArrayList<>();

    public void initialise() {
        SystemUtils.getOrCreateFile(hotelsFileName);
        int lineCount = SystemUtils.getNumLinesInFile(hotelsFileName);
        // if more than 1 line in the file, we've already initialised the system as the headers and another line are present
        if(lineCount > 1) {
            List<String> fileContents = SystemUtils.readFileAsString(hotelsFileName);
            // start from first line of file to skip headers
            for(int i = 1; i < fileContents.size(); i++) {
                addToHotelList(DataFileParser.parseHotelData(fileContents.get(i)));
            }
            SystemMenu.displayStartMenu();
        } else {
            if(lineCount == 0) {
                // if the file is empty, add the headers
                SystemUtils.writeToFile(hotelsFileName, "Hotel Name,Open Date,Number of Rooms,Room Types,Rooms,Employees");
            }
            SystemMenu.displayFirstTimeMenu();
        }
    }

    public boolean isGuestLogin() {
        return guestLogin;
    }

    public void setGuestLogin(boolean guestLogin) {
        this.guestLogin = guestLogin;
    }

    public void addToHotelList(Hotel hotel) {
        hotels.add(hotel);
    }

    public boolean addHotel(Hotel hotel) {
        boolean success = SystemUtils.writeToFile(hotelsFileName, hotel.toCsvString());
        if(success) {
            addToHotelList(hotel);
        }
        return success;
    }
}
