package com.example.scheduler.algorithms;

import com.example.scheduler.model.Process;
import com.example.scheduler.model.ExecutionStep;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RoundRobinScheduler implements Scheduler {
    private int quantum;

    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }

    @Override
    public List<ExecutionStep> schedule(List<Process> processes) {
        List<ExecutionStep> steps = new ArrayList<>();
        List<Process> procList = new ArrayList<>(processes);
        Queue<Process> readyQueue = new LinkedList<>();
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
            int timeSlice = Math.min(quantum, remainingTime[index]);
            currentTime += timeSlice;
            remainingTime[index] -= timeSlice;

            steps.add(new ExecutionStep(current.getPid(), startTime, currentTime));
            if (remainingTime[index] > 0) {
                readyQueue.add(current);
            }
        }
        return steps;
    }
}