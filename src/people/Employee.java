package people;

abstract class Employee extends Person {
    private String position;

    Employee(String name, int id, String position) {
        super(name,id);
        this.position = position;
    }
}
