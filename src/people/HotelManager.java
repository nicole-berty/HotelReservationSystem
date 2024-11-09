package people;

import java.util.ArrayList;

public class HotelManager extends Employee {
    private ArrayList<String> managedDepartments;
    private ArrayList<Employee> staffUnderManagement;

    HotelManager(String name, String email, int id, double salary, ArrayList<String> managedDepartments, ArrayList<Employee> staffUnderManagement) {
        super(name, email, id, salary);
        this.managedDepartments = managedDepartments;
        this.staffUnderManagement = staffUnderManagement;
    }
}
