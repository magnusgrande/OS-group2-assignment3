package diskscheduler;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Entry point for the disk scheduling simulation.
 *
 * <p>Runs {@link FcfsScheduler FCFS}, {@link SstfScheduler SSTF}, and {@link ScanScheduler SCAN}
 * against three predefined test cases. Results are printed to the console and mirrored to {@code
 * output.txt} in the working directory.
 *
 * <p><b>Test cases:</b>
 *
 * <ol>
 *   <li><em>Standard request queue</em> – general performance comparison.
 *   <li><em>Clustered requests</em> – shows the advantage of choosing nearby requests.
 *   <li><em>Fairness challenge</em> – demonstrates how SSTF may delay requests far from the current
 *       head position.
 * </ol>
 *
 * <p><b>Disk assumptions:</b> cylinders numbered 0 through 199.
 */
public class Main {

  /** Name of the output file written alongside the console output. */
  private static final String OUTPUT_FILE = "output.txt";

  // -----------------------------------------------------------------------
  // Entry point
  // -----------------------------------------------------------------------

  /**
   * Runs all three scheduling algorithms against each test case, prints results to stdout, and
   * writes the same output to {@value OUTPUT_FILE}.
   *
   * @param args command-line arguments (not used)
   */
  public static void main(String[] args) {

    // ----- Define test cases -------------------------------------------
    TestCase[] cases = {
      new TestCase(
          "Case 1",
          "Standard request queue",
          53,
          "right",
          new int[] {98, 183, 37, 122, 14, 124, 65, 67}),
      new TestCase(
          "Case 2", "Clustered requests", 50, "right", new int[] {45, 48, 52, 90, 150, 160}),
      new TestCase(
          "Case 3", "Fairness challenge", 15, "right", new int[] {10, 12, 14, 16, 100, 102})
    };

    FcfsScheduler fcfs = new FcfsScheduler();
    SstfScheduler sstf = new SstfScheduler();
    ScanScheduler scan = new ScanScheduler();

    // ----- Run simulations and build output ----------------------------
    StringBuilder sb = new StringBuilder();
    appendBanner(sb);

    // results[i][j] where i = case index, j = algorithm (0=FCFS,1=SSTF,2=SCAN)
    SchedulingResult[][] results = new SchedulingResult[cases.length][3];

    for (int i = 0; i < cases.length; i++) {
      TestCase tc = cases[i];
      results[i][0] = fcfs.simulate(tc.initialHead, tc.requests);
      results[i][1] = sstf.simulate(tc.initialHead, tc.requests);
      results[i][2] = scan.simulate(tc.initialHead, tc.direction, tc.requests);
      appendCaseSection(sb, tc, results[i]);
    }

    appendSummaryTable(sb, cases, results);

    // ----- Output -------------------------------------------------------
    String output = sb.toString();
    System.out.print(output);
    writeToFile(output);
  }

  // -----------------------------------------------------------------------
  // Output helpers
  // -----------------------------------------------------------------------

  /** Appends the program banner. */
  private static void appendBanner(StringBuilder sb) {
    sb.append("=".repeat(70)).append("\n");
    sb.append("   DISK SCHEDULING SIMULATION\n");
    sb.append("   Cylinders: 0 – 199  |  Algorithms: FCFS, SSTF, SCAN\n");
    sb.append("=".repeat(70)).append("\n\n");
  }

  /**
   * Appends a complete section for one test case, covering all three algorithm results.
   *
   * @param sb the output builder
   * @param tc the test case configuration
   * @param results the three {@link SchedulingResult} objects for this case (index 0 = FCFS, 1 =
   *     SSTF, 2 = SCAN)
   */
  private static void appendCaseSection(StringBuilder sb, TestCase tc, SchedulingResult[] results) {
    sb.append("=".repeat(70)).append("\n");
    sb.append(tc.name).append(" – ").append(tc.description).append("\n");
    sb.append("  Initial head : ").append(tc.initialHead).append("\n");
    sb.append("  Direction    : ").append(tc.direction).append("\n");
    sb.append("  Requests     : ").append(Arrays.toString(tc.requests)).append("\n");
    sb.append("=".repeat(70)).append("\n\n");

    for (SchedulingResult r : results) {
      appendAlgorithmResult(sb, r);
    }
  }

  /**
   * Appends the formatted result block for a single algorithm run.
   *
   * <p>Shows:
   *
   * <ul>
   *   <li>Service order
   *   <li>Compact head-movement path (all positions including disk-end waypoint)
   *   <li>Step-by-step movement details
   *   <li>Total head movement and average seek distance
   * </ul>
   *
   * @param sb the output builder
   * @param r the scheduling result to format
   */
  private static void appendAlgorithmResult(StringBuilder sb, SchedulingResult r) {
    sb.append("--- ").append(r.getAlgorithmName()).append(" ---\n");

    // Service order (requested cylinders only, no disk-end waypoints)
    String serviceStr =
        r.getServiceOrder().stream().map(String::valueOf).collect(Collectors.joining(", "));
    sb.append(String.format("  Service order : %s%n", serviceStr));

    // Full head path (includes initial head and SCAN disk-end waypoint)
    String pathStr =
        r.getFullPath().stream().map(String::valueOf).collect(Collectors.joining(" -> "));
    sb.append(String.format("  Head path     : %s%n", pathStr));

    // Step-by-step movement
    sb.append("  Steps         :\n");
    for (String step : r.getMovementSteps()) {
      sb.append("    ").append(step).append("\n");
    }

    sb.append(String.format("  Total movement: %d cylinders%n", r.getTotalMovement()));
    sb.append(String.format("  Avg seek dist : %.2f cylinders%n%n", r.getAverageSeekDistance()));
  }

  /**
   * Appends the summary comparison table covering all test cases and algorithms.
   *
   * @param sb the output builder
   * @param cases the array of test cases
   * @param results the 2-D array of results (cases × algorithms)
   */
  private static void appendSummaryTable(
      StringBuilder sb, TestCase[] cases, SchedulingResult[][] results) {
    String[] algNames = {"FCFS", "SSTF", "SCAN"};

    sb.append("=".repeat(70)).append("\n");
    sb.append("   SUMMARY TABLE\n");
    sb.append("=".repeat(70)).append("\n");

    // Table borders and header
    String sep =
        "+"
            + "-".repeat(10)
            + "+"
            + "-".repeat(11)
            + "+"
            + "-".repeat(21)
            + "+"
            + "-".repeat(21)
            + "+\n";
    String hdr =
        String.format(
            "| %-8s | %-9s | %19s | %19s |%n",
            "Case", "Algorithm", "Total Head Movement", "Avg Seek Distance");
    String rowFmt = "| %-8s | %-9s | %19d | %19.2f |%n";

    sb.append(sep).append(hdr).append(sep);

    for (int i = 0; i < cases.length; i++) {
      for (int j = 0; j < algNames.length; j++) {
        sb.append(
            String.format(
                rowFmt,
                cases[i].name,
                algNames[j],
                results[i][j].getTotalMovement(),
                results[i][j].getAverageSeekDistance()));
      }
      sb.append(sep);
    }
    sb.append("\n");
  }

  // -----------------------------------------------------------------------
  // File I/O
  // -----------------------------------------------------------------------

  /**
   * Writes {@code content} to {@value OUTPUT_FILE} in the working directory. Prints a warning to
   * stderr if the write fails, but does not abort the program.
   *
   * @param content the text to write
   */
  private static void writeToFile(String content) {
    try (PrintWriter pw = new PrintWriter(new FileWriter(OUTPUT_FILE))) {
      pw.print(content);
      System.out.println("[Output also written to " + OUTPUT_FILE + "]");
    } catch (IOException e) {
      System.err.println("Warning: could not write " + OUTPUT_FILE + ": " + e.getMessage());
    }
  }

  // -----------------------------------------------------------------------
  // Inner class: TestCase
  // -----------------------------------------------------------------------

  /** Simple container for a single test case configuration. */
  private static class TestCase {

    /** Short case identifier, e.g. {@code "Case 1"}. */
    final String name;

    /** Brief description of the case's purpose. */
    final String description;

    /** Initial cylinder position of the disk head. */
    final int initialHead;

    /** Initial head direction: {@code "left"} or {@code "right"}. */
    final String direction;

    /** Disk request queue (cylinder numbers). */
    final int[] requests;

    /**
     * Constructs a {@code TestCase}.
     *
     * @param name short case identifier
     * @param description brief description of the case's purpose
     * @param initialHead initial disk head position
     * @param direction initial head direction: {@code "left"} or {@code "right"}
     * @param requests disk request queue
     */
    TestCase(String name, String description, int initialHead, String direction, int[] requests) {
      this.name = name;
      this.description = description;
      this.initialHead = initialHead;
      this.direction = direction;
      this.requests = requests;
    }
  }
}
