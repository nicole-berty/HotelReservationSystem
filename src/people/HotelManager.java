package people;

import reservations.Reservation;
import system.DataFileParser;
import system.HotelSystem;
import system.SystemUtils;

import java.util.List;

public final class HotelManager extends Employee {
    private String[] managedDepartments;
    private Employee[] staffUnderManagement;

    public HotelManager(String name, String email, double salary) {
        super(name, email, salary);
        managedDepartments = new String[0];
        staffUnderManagement = new Employee[0];
    }

    public HotelManager(String name, String email, double salary, String[] managedDepartments, Employee[] staffUnderManagement) {
        // contrasting this(), used to invoke another constructor in the class, with this, which is used to access class
        // fields like this.managedDepartments
        this(name, email, salary);
        this.managedDepartments = managedDepartments;
        this.staffUnderManagement = staffUnderManagement;
    }

    public void giveDiscount(Reservation reservation, double discount) {
        double cost = reservation.getTotalCost();
        double newPrice = Math.round(cost * (1 - (discount / 100.0)));
        reservation.setTotalCost(newPrice);
        String reservations = SystemUtils.getModifiedReservationList(reservation);
        boolean success = SystemUtils.writeToFile(HotelSystem.getInstance().dataFiles.get("reservations"), reservations, false);
        if(success) {
            System.out.println(STR."The discount was applied and the new cost of the reservation is \{newPrice}");
        }
    }

    public Employee[] getStaffUnderManagement() {
        return staffUnderManagement;
    }

    public String[] getManagedDepartments() {
        return managedDepartments;
    }

    @Override
    public String toString() {
        String departments = String.join(",", managedDepartments);
        StringBuilder managedEmployeesSb = new StringBuilder();
        for(Employee employee : staffUnderManagement) {
            managedEmployeesSb.append(employee.toString()).append(",");
        }
        return super.toString() + STR."position='HotelManager', managedDepartments=\{departments}, staffUnderManagement=[\{managedEmployeesSb}]}";
    }
}
