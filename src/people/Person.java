package people;

import hotel.Room;
import pricing.PricingStrategy;
import reservations.Reservation;
import system.DataFileParser;
import system.HotelSystem;
import system.SystemUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// 4 - Sealed Classes and Interfaces
sealed public abstract class Person permits Customer, Employee {
    private final String name;
    private final String email;

    Person(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    // Runtime Polymorphism (Dynamic Method Dispatch) - this method is overridden in sublasses and which version of
    // it is used depends on object type that calls it at runtime
    public void cancelReservation(Reservation reservation) {
        reservation.setCancelled(true);
        reservation.setCancellationDate(LocalDate.from(HotelSystem.currentTime.get())); // 1 - Lambdas: Supplier
        String reservations = SystemUtils.getModifiedReservationList(reservation);
        boolean success = SystemUtils.writeToFile(HotelSystem.getInstance().dataFiles.get("reservations"), reservations, false);
        if(success) {
            System.out.println("The reservation was successfully cancelled.");
        }
    }

    public void makeReservation(String name, String email, boolean advancedPurchase, boolean refundable, LocalDate checkInDate,
                                int numNights, Set<Room> roomsReserved, boolean paid, int[] additionalCosts) {
        PricingStrategy pricingStrategy = HotelSystem.getInstance().getSelectedHotel().getPricingStrategy();
        Reservation reservation = new Reservation(name, email, advancedPurchase, refundable, checkInDate, numNights, roomsReserved, paid, pricingStrategy, additionalCosts);
        System.out.println("Note that all prices in MegaCorp(C) Hotels may be liable for additional charges or discounts seasonally or based on certain promotions.");
        System.out.println(STR."The pricing strategy currently in place is \{pricingStrategy}");

        if(!paid) {
            System.out.println("You must pay an approx 10% deposit today for the booking.");
            reservation.setDepositPaid(reservation.calculateDeposit());
        }
        System.out.println(STR."The total reservation cost is \{reservation.getTotalCost()}.\nThe non refundable deposit is \{reservation.getDepositPaid()} and the remaining balance to be paid is \{reservation.calculateRemainingCost()}");
        System.out.println(STR."Your reservation number is \{reservation.getReservationId()}.");
        SystemUtils.writeToFile(HotelSystem.getInstance().dataFiles.get("reservations"), reservation.toString());
    }

    public ArrayList<Reservation> retrieveAllReservations() {
        List<String> fileContents = SystemUtils.readFileAsString(HotelSystem.getInstance().dataFiles.get("reservations").path());
        ArrayList<Reservation> reservations = new ArrayList<>();
        // start from first line of file to skip headers
        for(int i = 1; i < fileContents.size(); i++) {
            reservations.add(DataFileParser.parseReservationData(fileContents.get(i)));
        }
        return reservations;
    }

    @Override
    public String toString() {
        return STR."Person{name='\{name}', email='\{email}', ";
    }
}
