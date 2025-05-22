package com.example.scheduler.algorithms;

import com.example.scheduler.model.Process;
import com.example.scheduler.model.ExecutionStep;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class SJFScheduler implements Scheduler {
    private final boolean preemptive;

    public SJFScheduler(boolean preemptive) {
        this.preemptive = preemptive;
    }

    @Override
    public List<ExecutionStep> schedule(List<Process> processes) {
        List<ExecutionStep> steps = new ArrayList<>();
        List<Process> procList = new ArrayList<>(processes);
        PriorityQueue<Process> readyQueue = new PriorityQueue<>((p1, p2) -> {
            int burst1 = p1.getBurstTime();
            int burst2 = p2.getBurstTime();
            return burst1 == burst2 ? Integer.compare(p1.getArrivalTime(), p2.getArrivalTime()) 
                                   : Integer.compare(burst1, burst2);
        });

        int currentTime = 0;
        int[] remainingTime = new int[procList.size()];
        for (int i = 0; i < procList.size(); i++) {
            remainingTime[i] = procList.get(i).getBurstTime();
        }

        while (!procList.isEmpty() || !readyQueue.isEmpty()) {
            // Add arrived processes to ready queue
            for (int i = 0; i < procList.size(); i++) {
                if (procList.get(i).getArrivalTime() <= currentTime) {
                    readyQueue.add(procList.get(i));
                    procList.remove(i);
                    i--;
                }
            }

            if (readyQueue.isEmpty()) {
                currentTime++;
                continue;
            }

            Process current = readyQueue.poll();
            int index = processes.indexOf(current);
            int startTime = currentTime;

            if (preemptive) {
                int nextArrival = procList.isEmpty() ? Integer.MAX_VALUE 
                    : procList.stream().mapToInt(Process::getArrivalTime).min().orElse(Integer.MAX_VALUE);
                int timeSlice = Math.min(remainingTime[index], nextArrival - currentTime);
                if (timeSlice <= 0) timeSlice = 1;
                currentTime += timeSlice;
                remainingTime[index] -= timeSlice;
                if (remainingTime[index] > 0) {
                    readyQueue.add(current);
                }
                steps.add(new ExecutionStep(current.getPid(), startTime, currentTime));
            } else {
                currentTime += remainingTime[index];
                remainingTime[index] = 0;
                steps.add(new ExecutionStep(current.getPid(), startTime, currentTime));
            }
        }
        return steps;
    }
}