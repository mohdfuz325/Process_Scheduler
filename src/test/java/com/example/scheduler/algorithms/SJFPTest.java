package com.example.scheduler.algorithms;

import com.example.scheduler.model.ExecutionStep;
import com.example.scheduler.model.Process;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class SJFPTest {
    @Test
    public void testSJFNonPreemptive() {
        try {
            List<Process> processes = new ArrayList<>();
            processes.add(new Process("P1", 0, 5, 1));
            processes.add(new Process("P2", 1, 3, 2));
            processes.add(new Process("P3", 2, 2, 3));

            SJFNonPreemptive scheduler = new SJFNonPreemptive();
            System.out.println("Input processes: " + processes);
            List<ExecutionStep> steps = scheduler.schedule(processes);
            System.out.println("Execution steps: " + steps);

            assertEquals("Expected 3 steps, got: " + steps.size(), 3, steps.size());
            if (steps.size() >= 1) {
                assertEquals("Step 1 PID", "P1", steps.get(0).getProcessId());
                assertEquals("Step 1 start time", 0, steps.get(0).getStartTime());
                assertEquals("Step 1 end time", 5, steps.get(0).getEndTime());
            }
            if (steps.size() >= 2) {
                assertEquals("Step 2 PID", "P3", steps.get(1).getProcessId());
                assertEquals("Step 2 start time", 5, steps.get(1).getStartTime());
                assertEquals("Step 2 end time", 7, steps.get(1).getEndTime());
            }
            if (steps.size() >= 3) {
                assertEquals("Step 3 PID", "P2", steps.get(2).getProcessId());
                assertEquals("Step 3 start time", 7, steps.get(2).getStartTime());
                assertEquals("Step 3 end time", 10, steps.get(2).getEndTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}