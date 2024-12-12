package people;

public final class HotelManager extends Employee {
    private String[] managedDepartments;
    private Employee[] staffUnderManagement;

    public HotelManager(String name, String email, int id, double salary) {
        super(name, email, id, salary);
        managedDepartments = new String[0];
        staffUnderManagement = new Employee[0];
    }

    public HotelManager(String name, String email, int id, double salary, String[] managedDepartments, Employee[] staffUnderManagement) {
        this(name, email, id, salary);
        this.managedDepartments = managedDepartments;
        this.staffUnderManagement = staffUnderManagement;
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
