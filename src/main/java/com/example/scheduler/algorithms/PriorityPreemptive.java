package com.example.scheduler.algorithms;

import com.example.scheduler.model.ExecutionStep;
import com.example.scheduler.model.Process;
import java.util.ArrayList;
import java.util.List;

public class PriorityPreemptive implements Scheduler {
    @Override
    public List<ExecutionStep> schedule(List<Process> processes) {
        List<ExecutionStep> steps = new ArrayList<>();
        List<Process> ready = new ArrayList<>();
        List<Process> copy = new ArrayList<>(processes);
        int currentTime = 0;

        while (!copy.isEmpty() || !ready.isEmpty()) {
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
            Process highest = ready.get(0);
            steps.add(new ExecutionStep(highest.getPid(), currentTime, currentTime + 1));
            highest.setRemainingTime(highest.getRemainingTime() - 1);
            if (highest.getRemainingTime() == 0) {
                ready.remove(highest);
            }
            currentTime++;
        }
        return combineSteps(steps);
    }

    private List<ExecutionStep> combineSteps(List<ExecutionStep> steps) {
        List<ExecutionStep> combined = new ArrayList<>();
        if (steps.isEmpty()) return combined;
        ExecutionStep current = steps.get(0);
        for (int i = 1; i < steps.size(); i++) {
            ExecutionStep next = steps.get(i);
            if (current.getProcessId().equals(next.getProcessId()) && current.getEndTime() == next.getStartTime()) {
                current = new ExecutionStep(current.getProcessId(), current.getStartTime(), next.getEndTime());
            } else {
                combined.add(current);
                current = next;
            }
        }
        combined.add(current);
        return combined;
    }
}