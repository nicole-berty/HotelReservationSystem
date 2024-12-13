package people;

public final class Customer extends Person {
    private int numCompletedReservations;

    public Customer(String name, String email) {
        super(name, email);
    }

    public Customer(String name, String email, int numCompletedReservations) {
        this(name, email);
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
