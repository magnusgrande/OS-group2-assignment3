package diskscheduler;

import java.util.ArrayList;
import java.util.List;

/**
 * First Come First Serve (FCFS) disk scheduling algorithm.
 *
 * <p>Requests are serviced strictly in the order they arrive in the queue,
 * with no reordering. FCFS is simple and completely fair in the sense that no
 * request is ever overtaken, but it does not optimise head movement, which can
 * lead to large total seek distances when consecutive requests are far apart.
 */
public class FCFSScheduler {

    /**
     * Simulates FCFS disk scheduling.
     *
     * <p>The disk head starts at {@code initialHead} and moves to each
     * cylinder in the order given by {@code requests}.
     *
     * @param initialHead the starting cylinder position of the disk head
     * @param requests    cylinder numbers to service, in arrival order
     * @return a {@link SchedulingResult} containing the service order, full head
     *         path, per-step movements, and total head movement
     */
    public SchedulingResult simulate(int initialHead, int[] requests) {
        List<Integer> serviceOrder = new ArrayList<>();
        List<Integer> fullPath     = new ArrayList<>();
        List<String>  steps        = new ArrayList<>();
        int totalMovement = 0;
        int current       = initialHead;

        fullPath.add(current);

        for (int request : requests) {
            int distance = Math.abs(request - current);
            totalMovement += distance;
            steps.add(current + " -> " + request + " (dist: " + distance + ")");
            serviceOrder.add(request);
            fullPath.add(request);
            current = request;
        }

        return new SchedulingResult("FCFS", serviceOrder, fullPath, steps, totalMovement);
    }
}
