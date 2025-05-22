package com.example.scheduler.algorithms;

import com.example.scheduler.model.Process;
import com.example.scheduler.model.ExecutionStep;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FCFSScheduler implements Scheduler {
    @Override
    public List<ExecutionStep> schedule(List<Process> processes) {
        List<ExecutionStep> steps = new ArrayList<>();
        // Sort by arrival time
        List<Process> sorted = processes.stream()
            .sorted((p1, p2) -> Integer.compare(p1.getArrivalTime(), p2.getArrivalTime()))
            .collect(Collectors.toList());
        
        int currentTime = 0;
        for (Process p : sorted) {
            if (currentTime < p.getArrivalTime()) {
                currentTime = p.getArrivalTime();
            }
            steps.add(new ExecutionStep(p.getPid(), currentTime, currentTime + p.getBurstTime()));
            currentTime += p.getBurstTime();
        }
        return steps;
    }
}