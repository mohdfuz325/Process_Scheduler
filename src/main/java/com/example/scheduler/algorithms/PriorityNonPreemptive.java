package com.example.scheduler.algorithms;

import com.example.scheduler.model.ExecutionStep;
import com.example.scheduler.model.Process;
import java.util.ArrayList;
import java.util.List;

public class PriorityNonPreemptive implements Scheduler {
    @Override
    public List<ExecutionStep> schedule(List<Process> processes) {
        List<ExecutionStep> steps = new ArrayList<>();
        List<Process> ready = new ArrayList<>();
        List<Process> copy = new ArrayList<>(processes);
        int currentTime = 0;

        while (!copy.isEmpty()) {
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
            ready.sort((p1, p2) -> Integer.compare(p1.getPriority(), p2.getPriority()));
            Process highest = ready.remove(0);
            steps.add(new ExecutionStep(highest.getPid(), currentTime, currentTime + highest.getBurstTime()));
            currentTime += highest.getBurstTime();
        }
        return steps;
    }
}