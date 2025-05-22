package com.example.scheduler.algorithms;

import com.example.scheduler.model.ExecutionStep;
import com.example.scheduler.model.Process;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class FCFSTest {
    @Test
    public void testFCFS() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process("P1", 0, 5, 1));
        processes.add(new Process("P2", 1, 3, 2));
        processes.add(new Process("P3", 2, 2, 3));

        FCFS scheduler = new FCFS();
        List<ExecutionStep> steps = scheduler.schedule(processes);

        assertEquals(3, steps.size());
        assertEquals("P1", steps.get(0).getProcessId());
        assertEquals(0, steps.get(0).getStartTime());
        assertEquals(5, steps.get(0).getEndTime());
        assertEquals("P2", steps.get(1).getProcessId());
        assertEquals(5, steps.get(1).getStartTime());
        assertEquals(8, steps.get(1).getEndTime());
        assertEquals("P3", steps.get(2).getProcessId());
        assertEquals(8, steps.get(2).getStartTime());
        assertEquals(10, steps.get(2).getEndTime());
    }
}