package people;

import java.util.ArrayList;

public class HotelManager extends Employee {
    private ArrayList<String> managedDepartments;
    private ArrayList<Employee> staffUnderManagement;

    public HotelManager(String name, String email, int id, double salary) {
        super(name, email, id, salary);
    }

    HotelManager(String name, String email, int id, double salary, ArrayList<String> managedDepartments, ArrayList<Employee> staffUnderManagement) {
        this(name, email, id, salary);
        this.managedDepartments = managedDepartments;
        this.staffUnderManagement = staffUnderManagement;
    }

    @Override
    public String toString() {
        return super.toString() + STR." HotelManager{managedDepartments=\{managedDepartments}, staffUnderManagement=\{staffUnderManagement}}";
    }
}
