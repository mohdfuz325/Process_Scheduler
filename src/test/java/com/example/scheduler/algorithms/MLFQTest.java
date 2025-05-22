package com.example.scheduler.algorithms;

import com.example.scheduler.model.ExecutionStep;
import com.example.scheduler.model.Process;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class MLFQTest {
    @Test
    public void testMLFQ() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process("P1", 0, 5, 1));
        processes.add(new Process("P2", 1, 3, 2));
        processes.add(new Process("P3", 2, 2, 3));

        MultilevelFeedbackQueue scheduler = new MultilevelFeedbackQueue();
        List<ExecutionStep> steps = scheduler.schedule(processes);

        assertEquals(5, steps.size()); // Example assertion, adjust based on actual MLFQ behavior
    }
}