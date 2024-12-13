package reservations;

import hotel.Room;
import hotel.RoomType;
import system.HotelSystem;
import system.SystemUtils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;

public class Reservation {
    private String reservationId;
    private String name;
    private String email;
    private ReservationType reservationType;
    private String hotelName;
    private boolean refundable;
    private Date checkInDate;
    private int numNights;
    private double totalCost;
    private double depositPaid;
    private boolean cancelled;
    private Date creationDate;
    private Date cancellationDate = null;
    private EnumMap<RoomType, Integer> roomsReserved;
    private ArrayList<Room> rooms;
    private boolean paid = false;
    private boolean completed = false;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public Reservation(String name, String email, boolean advancedPurchase, boolean refundable, Date checkInDate,
                       int numNights, EnumMap<RoomType, Integer> roomsReserved, boolean paid) {
        this(name, email, advancedPurchase, refundable, checkInDate, numNights, roomsReserved);
        this.paid = paid;
    }

    public Reservation(String name, String email, boolean advancedPurchase, boolean refundable, Date checkInDate,
                       int numNights, EnumMap<RoomType, Integer> roomsReserved) {
        this.reservationId = generateReservationId();
        this.name = name;
        this.email = email;
        this.reservationType = advancedPurchase ? ReservationType.ADVANCE_PURCHASE : ReservationType.STANDARD;
        this.refundable = refundable;
        this.checkInDate = checkInDate;
        this.numNights = numNights;
        this.roomsReserved = roomsReserved;
        this.hotelName = HotelSystem.getInstance().getSelectedHotel().getName();
        this.creationDate = new Date();
        calculateTotalCost();
    }

    public void calculateTotalCost() {
        for(var roomType : roomsReserved.entrySet()) {
            // total cost is the cost of each room in the reservation x number of nights all added together
            this.totalCost += (roomType.getValue() * HotelSystem.getInstance().getSelectedHotel().getRoomTypes()
                    .get(roomType.getKey()) * numNights);
        }
        // you get 5% off total cost for advance purchase
        if(reservationType == ReservationType.ADVANCE_PURCHASE) {
            totalCost *= 0.95;
        }
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setDepositPaid(double depositPaid) {
        this.depositPaid = depositPaid;
    }

    public double getDepositPaid() {
        return depositPaid;
    }

    public double calculateDeposit() {
        return this.totalCost * 0.05;
    }

    public double calculateRemainingCost() {
        return paid ? 0 : this.totalCost - this.depositPaid;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    private String generateReservationId() {
        SecureRandom random = new SecureRandom();
        StringBuilder id = new StringBuilder(8);

        for (int i = 0; i < 9; i++) {
            int index = random.nextInt(CHARACTERS.length());
            id.append(CHARACTERS.charAt(index));
        }

        return id.toString();
    }

    @Override
    public String toString() {
        StringBuilder roomsReservedSb = new StringBuilder("{");
        for(var roomReserved : roomsReserved.entrySet()) {
            roomsReservedSb.append(STR."\{roomReserved.getKey()}:\{roomReserved.getValue()},");
        }
        return String.format("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s", reservationId, name, email, reservationType,
                hotelName, refundable, SystemUtils.getDateStringOrNull(checkInDate), numNights, totalCost, depositPaid,
                SystemUtils.getDateStringOrNull(creationDate), SystemUtils.getDateStringOrNull(cancellationDate),
                roomsReservedSb, paid);
    }

}

enum ReservationType {
    STANDARD, ADVANCE_PURCHASE;

    @Override
    public String toString()
    {
        return switch (this.ordinal()) {
            case 0 -> "Standard";
            case 1 -> "Advance Purchase";
            default -> "";
        };
    }

    public static ReservationType fromString(String text) {
        for (ReservationType b : ReservationType.values()) {
            if (b.toString().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}