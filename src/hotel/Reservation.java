package hotel;

import java.util.Date;

public class Reservation {
    private int id;
    private String name;
    private ReservationType reservationType;
    private Date checkInDate;
    private int numNights;
    // ROOMS
    private double totalCost;
    private double deposit;
    private boolean cancelled;
    private Date creationDate;
    private Date cancellationDate;

    Reservation(int id, String name, ReservationType reservationType, Date checkInDate, int numNights, double totalCost, double deposit) {

    }

}

enum ReservationType { REFUNDABLE, NON_REFUNDABLE}