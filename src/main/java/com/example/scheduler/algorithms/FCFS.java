package com.example.scheduler.algorithms;

import com.example.scheduler.model.ExecutionStep;
import com.example.scheduler.model.Process;
import java.util.ArrayList;
import java.util.List;

public class FCFS implements Scheduler {
    @Override
    public List<ExecutionStep> schedule(List<Process> processes) {
        List<ExecutionStep> steps = new ArrayList<>();
        processes.sort((p1, p2) -> Integer.compare(p1.getArrivalTime(), p2.getArrivalTime()));
        int currentTime = 0;

        for (Process p : processes) {
            if (currentTime < p.getArrivalTime()) {
                currentTime = p.getArrivalTime();
            }
            steps.add(new ExecutionStep(p.getPid(), currentTime, currentTime + p.getBurstTime()));
            currentTime += p.getBurstTime();
        }
        return steps;
    }
}