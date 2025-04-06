package reservations;

import hotel.Room;
import hotel.RoomType;
import pricing.PricingStrategy;
import system.HotelSystem;
import system.SystemUtils;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Reservation {
    private String reservationId;
    private String name;
    private String email;
    private final ReservationType reservationType;
    private String hotelName;
    private boolean refundable;
    private LocalDate checkInDate;
    private int numNights;
    private double totalCost;
    private double depositPaid;
    private boolean cancelled;
    private LocalDate creationDate;
    private LocalDate cancellationDate = null;
    private Set<Room> roomsReserved;
    private boolean paid;
    private boolean completed = false;
    private PricingStrategy pricingStrategy;
    private boolean checkedIn;
    private int[] additionalCosts;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    // Copy constructor to be able to initialise a reservation object from another reservation
    public Reservation(Reservation other) {
        this(other.reservationId, other.name, other.email, other.reservationType == ReservationType.ADVANCE_PURCHASE,
                other.hotelName, other.refundable, other.checkInDate, other.numNights, other.totalCost, other.depositPaid,
                other.creationDate, other.cancellationDate, other.roomsReserved, other.paid, other.cancelled, other.completed,
                other.pricingStrategy, other.checkedIn, other.additionalCosts);
    }

    // Reservation constructor with all 19 fields to be read from file
    public Reservation(String reservationId, String name, String email, boolean advancedPurchase, String hotelName,
                       boolean refundable, LocalDate checkInDate, int numNights, double totalCost, double depositPaid,
                       LocalDate creationDate, LocalDate cancellationDate,  Set<Room> roomsReserved,
                       boolean paid, boolean cancelled, boolean completed, PricingStrategy pricingStrategy,
                       boolean checkedIn, int[] additionalCosts) {
        this(name, email, advancedPurchase, refundable, checkInDate, numNights, roomsReserved, paid, pricingStrategy, additionalCosts);
        this.cancelled = cancelled;
        this.completed = completed;
        // hotelName is set in the other constructor called above but will use the default value, here we set to value
        // actually passed to this constructor
        this.hotelName = hotelName;
        // constructor called above will calculate total cost but will set it here to value actually passed to constructor
        // from stored file
        this.totalCost = totalCost;
        this.checkedIn = checkedIn;
        this.reservationId = reservationId;
        this.depositPaid = depositPaid;
        this.creationDate = creationDate;
        this.cancellationDate = cancellationDate;
    }

    public Reservation(String name, String email, boolean advancedPurchase, boolean refundable, LocalDate checkInDate,
                       int numNights,  Set<Room> roomsReserved, boolean paid, PricingStrategy pricingStrategy,
                       int[] additionalCosts) {
        this.additionalCosts = additionalCosts;
        this.paid = paid;
        this.pricingStrategy = pricingStrategy;
        this.numNights = numNights;
        this.roomsReserved = new HashSet<>(roomsReserved); // defensive copy
        this.reservationType = advancedPurchase ? ReservationType.ADVANCE_PURCHASE : ReservationType.STANDARD;
        this.checkedIn = false;
        Supplier<String> bookingReferenceSupplier = this::generateReservationId; // 1 - Lambdas: Supplier
        this.reservationId = bookingReferenceSupplier.get();
        this.name = name;
        this.email = email;
        this.refundable = refundable;
        this.checkInDate = checkInDate;
        this.hotelName = HotelSystem.getInstance().getSelectedHotel().getName();
        this.creationDate = LocalDate.now();
        calculateTotalCost();
    }

    public void calculateTotalCost() {
        double cost = 0;
        for(var room : roomsReserved) {
            // total cost is the cost of each room in the reservation x number of nights all added together
            cost += (HotelSystem.getInstance().getSelectedHotel().getRoomCost(room.getRoomType()) * numNights);
        }
        // you get 5% off total cost for advance purchase
        if(reservationType == ReservationType.ADVANCE_PURCHASE) {
            cost *= 0.95;
        }
        var totalCost = pricingStrategy.calculatePrice(cost) + pricingStrategy.calculateAdditionalCosts(additionalCosts);
        this.totalCost = Math.round(totalCost * 100.0) / 100.0;
        setDepositPaid(totalCost * 0.1);
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        if(totalCost >= 0) {
            this.totalCost = totalCost;
        }
    }

    public void setDepositPaid(double depositPaid) {
        this.depositPaid = depositPaid;
    }

    public double getDepositPaid() {
        return depositPaid;
    }

    public double calculateDeposit() {
        return Math.ceil(this.totalCost * 0.05);
    }

    public double calculateRemainingCost() {
        return paid ? 0 : this.totalCost - this.depositPaid;
    }

    public void setPricingStrategy(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    public PricingStrategy getPricingStrategy() {
        return pricingStrategy;
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

    public Map<RoomType, Long> getReservedRoomCounts() {
        return roomsReserved.stream()
                .collect(Collectors.groupingBy(Room::getRoomType, // 2 - Streams - Terminal Operations: Collectors.groupingBy,
                        Collectors.counting())); // Collectors.counting() to count the number of rooms by type
    }

    public LocalDate getCheckInDate() {
        return checkInDate == null ? null : checkInDate;
    }

    public Set<Room> getRoomsReserved() {
        return roomsReserved;
    }

    public String getEmail() {
        return email;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isCompleted() {
        return completed;
    }

    public int getNumNights() {
        return numNights;
    }

    public void setCancellationDate(LocalDate cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean conflictsWith(LocalDate checkIn, LocalDate checkOut) {
        return this.checkInDate.isBefore(checkOut) && (this.checkInDate.plusDays(numNights)).isAfter(checkIn);
    }

    @Override
    public String toString() {
        StringBuilder roomsReservedSb = new StringBuilder();
        for(var room : roomsReserved) {
            roomsReservedSb.append(STR."Room \{room.getRoomNumber()}: \{room.getRoomType()},");
        }
        return String.format("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s", reservationId, name, email, reservationType,
                hotelName, refundable, SystemUtils.getDateStringOrNull(checkInDate), numNights, totalCost, depositPaid,
                SystemUtils.getDateStringOrNull(creationDate), SystemUtils.getDateStringOrNull(cancellationDate),
                roomsReservedSb, paid, cancelled, completed, getPricingStrategy(), checkedIn, Arrays.toString(additionalCosts));
    }

}

