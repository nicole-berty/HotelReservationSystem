package people;

public final class Customer extends Person {
    private int numCompletedReservations;

    public Customer(String name, String email, int id) {
        super(name, email, id);
    }

    public Customer(String name, String email, int id, int numCompletedReservations) {
        this(name, email, id);
        setNumCompletedReservations(numCompletedReservations);
    }

    public void setNumCompletedReservations(int numCompletedReservations) {
        this.numCompletedReservations = numCompletedReservations;
    }

    public int getNumCompletedReservations() {
        return numCompletedReservations;
    }

    @Override
    public String toString() {
        return super.toString() + STR."numCompletedReservations=\{numCompletedReservations}}";
    }
}
