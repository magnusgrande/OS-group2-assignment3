package diskscheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SCAN (Elevator) disk scheduling algorithm.
 *
 * <p>The disk head sweeps in the given initial direction, servicing all
 * requests it encounters along the way. When the head reaches the end of the
 * disk (cylinder {@value MIN_CYLINDER} when travelling left, or
 * {@value MAX_CYLINDER} when travelling right), it reverses direction and
 * sweeps back, servicing any remaining requests. This provides more uniform
 * wait times than SSTF and avoids indefinite starvation.
 *
 * <p><b>Disk end behaviour:</b> the head always travels to the physical end of
 * the disk before reversing, even when no requests lie beyond the last one in
 * the current direction — provided there are still requests to service in the
 * opposite direction.
 *
 * <p><b>Assumptions:</b>
 * <ul>
 *   <li>Disk cylinders are numbered {@value MIN_CYLINDER} through
 *       {@value MAX_CYLINDER} inclusive.</li>
 *   <li>A request at exactly the current head position is treated as being in
 *       the current sweep direction (distance = 0, serviced first).</li>
 * </ul>
 */
public class SCANScheduler {

    /** Maximum cylinder number on the simulated disk (inclusive). */
    public static final int MAX_CYLINDER = 199;

    /** Minimum cylinder number on the simulated disk (inclusive). */
    public static final int MIN_CYLINDER = 0;

    /**
     * Simulates SCAN disk scheduling.
     *
     * @param initialHead      starting cylinder position of the disk head
     * @param initialDirection initial sweep direction: {@code "left"} or
     *                         {@code "right"} (case-insensitive)
     * @param requests         cylinder numbers to service
     * @return a {@link SchedulingResult} containing the service order, full head
     *         path (including disk-end waypoint), per-step movements, and total
     *         head movement
     * @throws IllegalArgumentException if {@code initialDirection} is not
     *                                  {@code "left"} or {@code "right"}
     */
    public SchedulingResult simulate(int initialHead,
                                     String initialDirection,
                                     int[] requests) {
        if (!initialDirection.equalsIgnoreCase("left")
                && !initialDirection.equalsIgnoreCase("right")) {
            throw new IllegalArgumentException(
                    "Direction must be 'left' or 'right', got: " + initialDirection);
        }

        List<Integer> serviceOrder = new ArrayList<>();
        List<Integer> fullPath     = new ArrayList<>();
        List<String>  steps        = new ArrayList<>();
        int totalMovement = 0;
        int current       = initialHead;
        boolean movingRight = initialDirection.equalsIgnoreCase("right");

        fullPath.add(current);

        // Sort all requests to enable easy directional traversal.
        List<Integer> sorted = new ArrayList<>();
        for (int r : requests) {
            sorted.add(r);
        }
        Collections.sort(sorted);

        // Partition into the two halves relative to the initial head position.
        // Requests at exactly the initial position belong to the current direction.
        List<Integer> leftSide  = new ArrayList<>(); // cylinders < current (or <= when going left)
        List<Integer> rightSide = new ArrayList<>(); // cylinders >= current (or > when going right)

        for (int r : sorted) {
            if (movingRight) {
                if (r >= current) {
                    rightSide.add(r);
                } else {
                    leftSide.add(r);
                }
            } else {
                if (r <= current) {
                    leftSide.add(r);
                } else {
                    rightSide.add(r);
                }
            }
        }

        // Build the two ordered passes.
        List<Integer> firstPass;
        List<Integer> secondPass;

        if (movingRight) {
            // First: ascending (right), then descending (left) after reversal.
            firstPass  = rightSide;
            secondPass = new ArrayList<>(leftSide);
            Collections.reverse(secondPass);
        } else {
            // First: descending (left), then ascending (right) after reversal.
            firstPass = new ArrayList<>(leftSide);
            Collections.reverse(firstPass);
            secondPass = rightSide;
        }

        // --- First pass ---
        for (int r : firstPass) {
            int distance = Math.abs(r - current);
            totalMovement += distance;
            steps.add(current + " -> " + r + " (dist: " + distance + ")");
            serviceOrder.add(r);
            fullPath.add(r);
            current = r;
        }

        // --- Travel to the physical disk end before reversing ---
        // Only needed when there are requests remaining in the second pass.
        if (!secondPass.isEmpty()) {
            int endOfDisk = movingRight ? MAX_CYLINDER : MIN_CYLINDER;
            if (current != endOfDisk) {
                int distance = Math.abs(endOfDisk - current);
                totalMovement += distance;
                steps.add(current + " -> " + endOfDisk
                        + " [end of disk, dist: " + distance + "]");
                fullPath.add(endOfDisk);
                current = endOfDisk;
            }
        }

        // --- Second pass ---
        for (int r : secondPass) {
            int distance = Math.abs(r - current);
            totalMovement += distance;
            steps.add(current + " -> " + r + " (dist: " + distance + ")");
            serviceOrder.add(r);
            fullPath.add(r);
            current = r;
        }

        return new SchedulingResult("SCAN", serviceOrder, fullPath, steps, totalMovement);
    }
}
