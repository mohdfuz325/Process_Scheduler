package com.example.scheduler.algorithms;

import com.example.scheduler.model.ExecutionStep;
import com.example.scheduler.model.Process;
import java.util.ArrayList;
import java.util.List;

public class SJFNonPreemptive implements Scheduler {
    @Override
    public List<ExecutionStep> schedule(List<Process> processes) {
        List<ExecutionStep> steps = new ArrayList<>();
        List<Process> ready = new ArrayList<>();
        // Create a deep copy to avoid modifying the input list
        List<Process> copy = new ArrayList<>();
        for (Process p : processes) {
            copy.add(new Process(p.getPid(), p.getArrivalTime(), p.getBurstTime(), p.getPriority()));
        }
        int currentTime = 0;

        // Continue until both copy and ready are empty
        while (!copy.isEmpty() || !ready.isEmpty()) {
            // Add arrived processes to ready queue
            for (Process p : copy) {
                if (p.getArrivalTime() <= currentTime) {
                    ready.add(p);
                }
            }
            copy.removeAll(ready);
            if (ready.isEmpty()) {
                currentTime++;
                continue;
            }
            // Sort by burst time and select shortest
            ready.sort((p1, p2) -> Integer.compare(p1.getBurstTime(), p2.getBurstTime()));
            Process shortest = ready.remove(0);
            steps.add(new ExecutionStep(shortest.getPid(), currentTime, currentTime + shortest.getBurstTime()));
            currentTime += shortest.getBurstTime();
        }
        return steps;
    }
}