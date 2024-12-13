package people;

import hotel.RoomType;
import reservations.Reservation;
import system.HotelSystem;
import system.SystemUtils;

import java.util.Date;
import java.util.EnumMap;

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

    public void makeReservation(String name, String email, boolean advancedPurchase, boolean refundable, Date checkInDate,
                                int numNights, EnumMap<RoomType, Integer> roomsReserved, boolean paid) {
        Reservation reservation = new Reservation(name, email, advancedPurchase, refundable, checkInDate, numNights, roomsReserved, paid);
        if(!paid) {
            System.out.println("You must pay a 10% deposit today for the booking. This is refundable up to 5 days before your arrival.");
            reservation.setDepositPaid(reservation.calculateDeposit());
        }
        System.out.println(STR."The total reservation cost is \{reservation.getTotalCost()}.\nThe deposit is \{reservation.getDepositPaid()} and the remaining balance to be paid is \{reservation.calculateRemainingCost()}");
        System.out.println(STR."Your reservation number is \{reservation.getReservationId()}.");
        SystemUtils.writeToFile(HotelSystem.getInstance().dataFiles.get("reservations"), reservation.toString());
    }

    @Override
    public String toString() {
        return STR."Person{name='\{name}', email='\{email}, '";
    }
}
