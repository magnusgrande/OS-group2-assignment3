package diskscheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable container for the result of a disk scheduling algorithm simulation.
 *
 * <p>Stores:
 * <ul>
 *   <li>the service order (cylinder numbers in the order they were serviced),</li>
 *   <li>the full head path (every position the head moved to, including the initial
 *       head position and any intermediate waypoints such as the disk end in SCAN),</li>
 *   <li>per-step movement descriptions for step-by-step output, and</li>
 *   <li>the total number of cylinders the head travelled.</li>
 * </ul>
 */
public class SchedulingResult {

    private final String        algorithmName;
    private final List<Integer> serviceOrder;
    private final List<Integer> fullPath;
    private final List<String>  movementSteps;
    private final int           totalMovement;

    /**
     * Constructs a new {@code SchedulingResult}.
     *
     * @param algorithmName  human-readable name of the scheduling algorithm
     * @param serviceOrder   cylinder numbers in the order they were serviced
     * @param fullPath       every position the head occupied (initial + all moves)
     * @param movementSteps  human-readable description of each individual head movement
     * @param totalMovement  total number of cylinders the disk head travelled
     */
    public SchedulingResult(String algorithmName,
                            List<Integer> serviceOrder,
                            List<Integer> fullPath,
                            List<String>  movementSteps,
                            int           totalMovement) {
        this.algorithmName = algorithmName;
        this.serviceOrder  = Collections.unmodifiableList(new ArrayList<>(serviceOrder));
        this.fullPath      = Collections.unmodifiableList(new ArrayList<>(fullPath));
        this.movementSteps = Collections.unmodifiableList(new ArrayList<>(movementSteps));
        this.totalMovement = totalMovement;
    }

    /**
     * Returns the human-readable name of the scheduling algorithm.
     *
     * @return algorithm name
     */
    public String getAlgorithmName() {
        return algorithmName;
    }

    /**
     * Returns the cylinder numbers in the order they were serviced.
     *
     * @return unmodifiable service-order list
     */
    public List<Integer> getServiceOrder() {
        return serviceOrder;
    }

    /**
     * Returns every position the disk head occupied during the simulation,
     * starting with the initial head position. For SCAN this includes the
     * disk-end waypoint (cylinder 0 or 199).
     *
     * @return unmodifiable full-path list
     */
    public List<Integer> getFullPath() {
        return fullPath;
    }

    /**
     * Returns human-readable descriptions of every individual head movement,
     * one entry per step (e.g. {@code "53 -> 65 (dist: 12)"}).
     *
     * @return unmodifiable movement-steps list
     */
    public List<String> getMovementSteps() {
        return movementSteps;
    }

    /**
     * Returns the total number of cylinders traversed by the disk head.
     *
     * @return total head movement in cylinders
     */
    public int getTotalMovement() {
        return totalMovement;
    }

    /**
     * Computes the average seek distance as total head movement divided by
     * the number of serviced requests.
     *
     * @return average seek distance in cylinders, or {@code 0.0} if no requests
     *         were serviced
     */
    public double getAverageSeekDistance() {
        return serviceOrder.isEmpty() ? 0.0 : (double) totalMovement / serviceOrder.size();
    }
}
