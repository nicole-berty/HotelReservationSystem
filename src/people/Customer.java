package people;

public class Customer extends Person {
    private int numCompletedReservations;

    Customer(String name, int id) {
        super(name, id);
    }

    public void setNumCompletedReservations(int numCompletedReservations) {
        this.numCompletedReservations = numCompletedReservations;
    }

    public int getNumCompletedReservations() {
        return numCompletedReservations;
    }
}
