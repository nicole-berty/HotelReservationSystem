package hotel;

import java.util.ArrayList;
import java.util.Date;

public class Reservation {
    private int id;
    private String name;
    private ReservationType reservationType;
    private boolean refundable;
    private Date checkInDate;
    private int numNights;
    private double totalCost;
    private double deposit;
    private boolean cancelled;
    private Date creationDate;
    private Date cancellationDate = null;
    private ArrayList<Room> rooms;
    private boolean paid = false;
    private boolean completed = false;

    Reservation(int id, String name, boolean refundable, Date checkInDate, int numNights, double totalCost, double deposit) {

    }

}

enum ReservationType { STANDARD, ADVANCED_PURCHASE}