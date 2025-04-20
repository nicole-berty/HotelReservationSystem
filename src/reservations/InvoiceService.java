package reservations;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.*;

public class InvoiceService {
    // 8 - Concurrency, using ExecutorService to process a list of Callable's
    private final ExecutorService executorService;

    public InvoiceService() {
        // Create an ExecutorService with a fixed thread pool
        this.executorService = Executors.newFixedThreadPool(4);
    }

    // Method to generate invoices concurrently
    public void generateInvoicesConcurrently(List<Reservation> reservations) {
        List<Callable<Void>> tasks = new ArrayList<>();

        // For each reservation, create a task to generate its invoice
        for (Reservation reservation : reservations) {
            tasks.add(() -> {
                System.out.println(STR."Task started for Reservation ID: \{reservation.getReservationId()} on thread: \{Thread.currentThread().getName()}");
                generateInvoiceForReservation(reservation);
                return null; // Callable returns null
            });
        }

        try {
            // Submit all tasks to the ExecutorService
            List<Future<Void>> futures = executorService.invokeAll(tasks);

            // Wait for all tasks to complete, blocking until that happens
            for (Future<Void> future : futures) {
                future.get();
            }
            System.out.println("All invoices have been generated.");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void generateInvoiceForReservation(Reservation reservation) {
        // Simulate a delay in processing (e.g., writing to a file, performing calculations in a real world use case)
        try {
            TimeUnit.SECONDS.sleep(2);
            System.out.println(STR."Generated invoice for Reservation ID: \{reservation.getReservationId()}, Guest: \{reservation.getEmail()}, Total Cost: €\{Math.round(reservation.getTotalCost() * 100.0) / 100.0} on thread: \{Thread.currentThread().getName()}");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Shutdown the ExecutorService after work is complete
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
