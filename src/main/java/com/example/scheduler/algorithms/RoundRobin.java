package com.example.scheduler.algorithms;

import com.example.scheduler.model.ExecutionStep;
import com.example.scheduler.model.Process;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RoundRobin implements Scheduler {
    private int quantum;

    public RoundRobin(int quantum) {
        this.quantum = quantum;
    }

    @Override
    public List<ExecutionStep> schedule(List<Process> processes) {
        List<ExecutionStep> steps = new ArrayList<>();
        Queue<Process> queue = new LinkedList<>();
        List<Process> copy = new ArrayList<>(processes);
        int currentTime = 0;

        while (!copy.isEmpty() || !queue.isEmpty()) {
            for (Process p : copy) {
                if (p.getArrivalTime() <= currentTime) {
                    queue.add(p);
                }
            }
            copy.removeAll(queue);
            if (queue.isEmpty()) {
                currentTime++;
                continue;
            }
            Process current = queue.poll();
            int timeSlice = Math.min(quantum, current.getRemainingTime());
            steps.add(new ExecutionStep(current.getPid(), currentTime, currentTime + timeSlice));
            current.setRemainingTime(current.getRemainingTime() - timeSlice);
            currentTime += timeSlice;
            if (current.getRemainingTime() > 0) {
                queue.add(current);
            }
        }
        return steps;
    }
}