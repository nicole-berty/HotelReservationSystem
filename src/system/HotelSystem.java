package system;

import hotel.Hotel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private Hotel selectedHotel;
    ArrayList<Hotel> hotels = new ArrayList<>();

    public final Map<String, FileDetails> dataFiles = Map.of(
            "credentials", new FileDetails("./src/data/ValidCredentials.csv", "email|password|hotel|person\n"),
            "hotels", new FileDetails("./src/data/MegaCorpHotels.csv", "Hotel Name|Open Date|Number of Rooms|Room Types|Rooms|Employees|PricingStrategy\n"),
            "reservations", new FileDetails("./src/data/MegaCorpHotelReservations.csv", "Reservation Id|Name|Email|Type|Hotel|Refundable|Check In|Num Nights|Total Cost|Deposit|Creation Date|Cancellation Date|Rooms Reserved|Paid|Cancelled|Complete|PricingStrategy|Checked In|AdditionalCosts\n")
    );


    public void initialise() {
        SystemUtils.getOrCreateFile(HotelSystem.getInstance().dataFiles.get("hotels").path());
        boolean missingHeaders = SystemUtils.isMissingHeaders(HotelSystem.getInstance().dataFiles.get("hotels").path());

        if(missingHeaders) {
            SystemUtils.writeToFile(HotelSystem.getInstance().dataFiles.get("hotels"), "");
            SystemMenu.displayFirstTimeMenu();
            // if we didn't add headers, we've already initialised the system previously
        } else {
            List<String> fileContents = SystemUtils.readFileAsString(HotelSystem.getInstance().dataFiles.get("hotels").path());
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

    public void addToHotelList(Hotel hotel) {
        hotels.add(hotel);
    }

    public boolean addHotel(Hotel hotel) {
        boolean success = SystemUtils.writeToFile(HotelSystem.getInstance().dataFiles.get("hotels"), hotel.toCsvString());
        if(success) {
            addToHotelList(hotel);
        }
        return success;
    }
}
