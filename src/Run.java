import system.HotelSystem;

public class Run {
// unnamed class - preview feature currently, first introduced in Java 21
public static void main(String[] args) {
    System.out.println("Enter q or Q at any time to exit the system.");
    HotelSystem.getInstance().initialise();
}
}
