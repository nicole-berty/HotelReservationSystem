package system;

import hotel.Hotel;
import people.Person;

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
    private Hotel selectedHotel;
    private Person currentUser;
    ArrayList<Hotel> hotels = new ArrayList<>();

    public void initialise() {
        SystemUtils.getOrCreateFile(hotelsFileName);
        boolean addedHeaders = SystemUtils.addHeaders(hotelsFileName, "Hotel Name|Open Date|Number of Rooms|Room Types|Rooms|Employees");

        if(addedHeaders) {
            SystemMenu.displayFirstTimeMenu();
            // if we didn't add headers, we've already initialised the system previously
        } else {
            List<String> fileContents = SystemUtils.readFileAsString(hotelsFileName);
            // start from first line of file to skip headers
            for(int i = 1; i < fileContents.size(); i++) {
                addToHotelList(DataFileParser.parseHotelData(fileContents.get(i)));
            }
            SystemMenu.displayHotelSelectionMenu();
        }
    }

    public Hotel getSelectedHotel() {
        return selectedHotel;
    }

    public void setSelectedHotel(Hotel selectedHotel) {
        this.selectedHotel = selectedHotel;
    }

    public void setCurrentUser(Person currentUser) {
        this.currentUser = currentUser;
    }

    public Person getCurrentUser() {
        return currentUser;
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
