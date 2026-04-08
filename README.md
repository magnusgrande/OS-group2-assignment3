# OS-group2-assignment3
IDATA2305 Operating Systems – Group 2 – Assignment 3 – Spring 2026 – NTNU Ålesund

## Overview

Java simulation of three disk scheduling algorithms:

| Algorithm | Description |
|-----------|-------------|
| **FCFS** | First Come First Serve – services requests in arrival order |
| **SSTF** | Shortest Seek Time First – always picks the nearest pending request |
| **SCAN** | Elevator – sweeps in one direction to the disk end, then reverses |

**Disk assumptions:** cylinders numbered **0 – 199**.

---

## Project structure

```
src/diskscheduler/
    Main.java               – entry point; test cases and output formatting
    SchedulingResult.java   – immutable result container
    FCFSScheduler.java      – FCFS algorithm
    SSTFScheduler.java      – SSTF algorithm
    SCANScheduler.java      – SCAN (elevator) algorithm
```

---

## How to run

### Requirements
- Java Development Kit (JDK) 11 or later
- A terminal in the repository root directory

### 1 – Compile

```bash
mkdir -p out
javac -d out src/diskscheduler/*.java
```

### 2 – Run

```bash
java -cp out diskscheduler.Main
```

The program prints results to the console **and** writes the same output to
`output.txt` in the working directory.

---

## Test cases

| Case | Purpose | Initial head | Direction | Requests |
|------|---------|-------------|-----------|----------|
| Case 1 | General performance comparison | 53 | right | 98, 183, 37, 122, 14, 124, 65, 67 |
| Case 2 | Advantage of nearby-request selection | 50 | right | 45, 48, 52, 90, 150, 160 |
| Case 3 | SSTF fairness / starvation risk | 15 | right | 10, 12, 14, 16, 100, 102 |

---

## Sample summary table

```
+----------+-----------+---------------------+---------------------+
| Case     | Algorithm | Total Head Movement |   Avg Seek Distance |
+----------+-----------+---------------------+---------------------+
| Case 1   | FCFS      |                 640 |               80.00 |
| Case 1   | SSTF      |                 236 |               29.50 |
| Case 1   | SCAN      |                 331 |               41.38 |
+----------+-----------+---------------------+---------------------+
| Case 2   | FCFS      |                 120 |               20.00 |
| Case 2   | SSTF      |                 120 |               20.00 |
| Case 2   | SCAN      |                 303 |               50.50 |
+----------+-----------+---------------------+---------------------+
| Case 3   | FCFS      |                  97 |               16.17 |
| Case 3   | SSTF      |                  97 |               16.17 |
| Case 3   | SCAN      |                 373 |               62.17 |
+----------+-----------+---------------------+---------------------+
```
