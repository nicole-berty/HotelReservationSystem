package people;

public abstract sealed class Employee extends Person permits HotelManager, HotelReceptionist {
    private double salary;
    Employee(String name, String email, int id, double salary) {
        super(name,email, id);
        this.salary = salary;
    }

    @Override
    public String toString() {
        return super.toString() + STR."salary=\{salary}, ";
    }
}
