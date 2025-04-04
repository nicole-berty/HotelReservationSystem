package people;

import hotel.Room;
import reservations.Reservation;

import java.time.LocalDate;
import java.util.Set;

public final class Customer extends Person {
    private int numCompletedReservations;

    public Customer(String name, String email) {
        super(name, email);
    }

    public Customer(String name, String email, int numCompletedReservations) {
        this(name, email);
        setNumCompletedReservations(numCompletedReservations);
    }

    public void setNumCompletedReservations(int numCompletedReservations) {
        this.numCompletedReservations = numCompletedReservations;
    }

    public int getNumCompletedReservations() {
        return numCompletedReservations;
    }

    // Runtime Polymorphism (Dynamic Method Dispatch) - this method is overridden from person class and which version of
    // it is used depends on object type that calls it at runtime
    @Override
    public void makeReservation(String name, String email, boolean advancedPurchase, boolean refundable, LocalDate checkInDate,
                                int numNights, Set<Room> roomsReserved, boolean paid, int[] additionalCosts) {
        System.out.println(STR."Customer \{getName()} making reservation...");
        // customers making their own reservation are subject to additional fees
        int[] newAdditionalCosts = {5, 10};
        super.makeReservation(name, email, advancedPurchase, refundable, checkInDate, numNights, roomsReserved, paid,
                newAdditionalCosts);
    }

    // Runtime Polymorphism (Dynamic Method Dispatch) - this method is overridden from person class and which version of
    // it is used depends on object type that calls it at runtime
    @Override
    public void cancelReservation(Reservation reservation) {
        System.out.println(STR."Customer \{getName()} cancelling reservation...");
        super.cancelReservation(reservation);
    }

    @Override
    public String toString() {
        return super.toString() + STR."numCompletedReservations=\{numCompletedReservations}}";
    }
}
