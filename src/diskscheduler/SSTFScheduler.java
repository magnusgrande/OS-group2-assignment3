package diskscheduler;

import java.util.ArrayList;
import java.util.List;

/**
 * Shortest Seek Time First (SSTF) disk scheduling algorithm.
 *
 * <p>At each step, the pending request nearest to the current head position is selected next. SSTF
 * minimises individual seek distances and therefore typically achieves a lower total head movement
 * than FCFS. However, it can cause <em>starvation</em>: if nearby requests keep arriving, distant
 * requests may be delayed indefinitely.
 *
 * <p><b>Tie-breaking rule:</b> when two pending requests are equally close to the current head, the
 * one that appears earlier in the original request queue is selected first.
 */
public class SstfScheduler {

  /**
   * Simulates SSTF disk scheduling.
   *
   * <p>The disk head starts at {@code initialHead} and greedily moves to the closest pending
   * cylinder at each step until all requests have been serviced.
   *
   * @param initialHead the starting cylinder position of the disk head
   * @param requests cylinder numbers to service, in arrival order
   * @return a {@link SchedulingResult} containing the service order, full head path, per-step
   *     movements, and total head movement
   */
  public SchedulingResult simulate(int initialHead, int[] requests) {
    List<Integer> serviceOrder = new ArrayList<>();
    List<Integer> fullPath = new ArrayList<>();
    List<String> steps = new ArrayList<>();
    int totalMovement = 0;
    int current = initialHead;

    fullPath.add(current);

    // Work on a mutable copy so serviced requests can be removed.
    List<Integer> pending = new ArrayList<>();
    for (int r : requests) {
      pending.add(r);
    }

    while (!pending.isEmpty()) {
      // Find the index of the closest pending request.
      // On ties, the earlier index (original queue order) wins.
      int closestIndex = 0;
      int closestDistance = Math.abs(pending.get(0) - current);

      for (int i = 1; i < pending.size(); i++) {
        int distance = Math.abs(pending.get(i) - current);
        if (distance < closestDistance) {
          closestDistance = distance;
          closestIndex = i;
        }
      }

      int next = pending.remove(closestIndex);
      totalMovement += closestDistance;
      steps.add(current + " -> " + next + " (dist: " + closestDistance + ")");
      serviceOrder.add(next);
      fullPath.add(next);
      current = next;
    }

    return new SchedulingResult("SSTF", serviceOrder, fullPath, steps, totalMovement);
  }
}
