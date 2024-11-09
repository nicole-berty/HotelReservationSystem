package people;

abstract class Person {
    private String name;
    private String email;
    private int id;

    Person(String name, String email, int id) {
        this.name = name;
        this.email = email;
        this.id = id;
    }
}
