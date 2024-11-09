package people;

public abstract class Employee extends Person {
    private double salary;
    Employee(String name, String email, int id, double salary) {
        super(name,email, id);
        this.salary = salary;
    }
}
