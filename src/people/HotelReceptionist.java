package people;

public class HotelReceptionist extends Employee {
    private int assignedCheckInCounter;
    HotelReceptionist(String name, String email, int id, double salary, int assignedCheckInCounter) {
        super(name, email, id, salary);
        this.assignedCheckInCounter = assignedCheckInCounter;
    }
}
