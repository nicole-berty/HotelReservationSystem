package people;

public abstract sealed class Employee extends Person permits HotelManager, HotelReceptionist {
    private double salary;
    Employee(String name, String email, double salary) {
        // contrasting super(), used to invoke a constructor in the superclass Person, with super, which is used to access class
        // methods (if accessible) in the super class such as super.toString() called in the toString method below
        super(name,email);
        this.salary = salary;
    }

    public double getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        return super.toString() + STR."salary=\{salary}, ";
    }
}
