package people;

import reservations.Reservation;
import system.HotelSystem;
import system.SystemUtils;

public abstract sealed class Employee extends Person permits HotelManager, HotelReceptionist {
    private double salary;
    Employee(String name, String email, double salary) {
        // contrasting super(), used to invoke a constructor in the superclass Person, with super, which is used to access class
        // methods (if accessible) in the super class such as super.toString() called in the toString method below
        super(name,email);
        this.salary = salary;
    }

    public double getSalary() {
        return salary;
    }

    public void checkIn(Reservation reservation) {
        reservation.setCheckedIn(true);
        String reservations = SystemUtils.getModifiedReservationList(reservation);
        boolean success = SystemUtils.writeToFile(HotelSystem.getInstance().dataFiles.get("reservations"), reservations, false);
        if(success) {
            System.out.println("Check in complete!");
        }
    }

    public void checkOut(Reservation reservation) {
        reservation.setCheckedIn(false);
        reservation.setCompleted(true);
        reservation.setCancelled(false);
        reservation.setCancellationDate(null);
        String reservations = SystemUtils.getModifiedReservationList(reservation);
        boolean success = SystemUtils.writeToFile(HotelSystem.getInstance().dataFiles.get("reservations"), reservations, false);
        if(success) {
            System.out.println("Check out complete!");
        }
    }

    @Override
    public String toString() {
        return super.toString() + STR."salary=\{salary}, ";
    }
}
