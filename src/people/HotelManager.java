package people;

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
