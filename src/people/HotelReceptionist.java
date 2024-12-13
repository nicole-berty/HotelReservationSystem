package people;

public final class HotelReceptionist extends Employee {
    private int assignedCheckInCounter;
    public HotelReceptionist(String name, String email, double salary, int assignedCheckInCounter) {
        super(name, email, salary);
        this.assignedCheckInCounter = assignedCheckInCounter;
    }

    public int getAssignedCheckInCounter() {
        return assignedCheckInCounter;
    }

    @Override
    public String toString() {
        return super.toString() + STR."position='HotelReceptionist', assignedCheckInCounter=\{assignedCheckInCounter}}";
    }
}
