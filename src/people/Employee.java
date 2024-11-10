package people;

public abstract class Employee extends Person {
    private double salary;
    Employee(String name, String email, int id, double salary) {
        super(name,email, id);
        this.salary = salary;
    }

    @Override
    public String toString() {
        return super.toString() + STR." Employee{salary=\{salary}}";
    }
}
