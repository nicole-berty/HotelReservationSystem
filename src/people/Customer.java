package people;

public class Customer extends Person {
    private int numCompletedReservations;

    Customer(String name, String email, int id) {
        super(name, email, id);
    }

    public void setNumCompletedReservations(int numCompletedReservations) {
        this.numCompletedReservations = numCompletedReservations;
    }

    public int getNumCompletedReservations() {
        return numCompletedReservations;
    }
}
