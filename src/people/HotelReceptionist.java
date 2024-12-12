package people;

public final class HotelReceptionist extends Employee {
    private int assignedCheckInCounter;
    public HotelReceptionist(String name, String email, int id, double salary, int assignedCheckInCounter) {
        super(name, email, id, salary);
        this.assignedCheckInCounter = assignedCheckInCounter;
    }

    @Override
    public String toString() {
        return super.toString() + STR."position='HotelReceptionist', assignedCheckInCounter=\{assignedCheckInCounter}}";
    }
}
