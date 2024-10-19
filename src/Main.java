public class Main {
    public static void main(String[] args) {
        // TODO: add condition for existing hotel in DB/file/etc. here
        System.out.println("Welcome to the Hotel Management System!\n" +
                "No hotels have been created yet - login as a hotel administrator to get started!");
        SystemMenu.displayStartMenu();
        System.out.println("Finished login!");
    }
}