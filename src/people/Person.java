package people;

sealed public abstract class Person permits Customer, Employee {
    private String name;
    private String email;
    private int id;

    Person(String name, String email, int id) {
        this.name = name;
        this.email = email;
        this.id = id;
    }

    @Override
    public String toString() {
        return STR."Person{name='\{name}', email='\{email}', id=\{id}, ";
    }
}
