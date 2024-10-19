package hotel;

public class Reservation {
    private int id;
    private String name;
    private ReservationType reservationType;
    private String checkInDate;
    private int numNights;
    // ROOMS
    private double totalCost;
    private double deposit;
    private boolean cancelled;

}

enum ReservationType { REFUNDABLE, NON_REFUNDABLE}