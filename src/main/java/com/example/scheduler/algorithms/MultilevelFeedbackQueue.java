package com.example.scheduler.algorithms;

import com.example.scheduler.model.ExecutionStep;
import com.example.scheduler.model.Process;
import com.example.scheduler.model.QueueLevel;
import java.util.ArrayList;
import java.util.List;

public class MultilevelFeedbackQueue implements Scheduler {
    private List<QueueLevel> queues;

    public MultilevelFeedbackQueue() {
        queues = new ArrayList<>();
        // Example configuration
        queues.add(new QueueLevel(1, new RoundRobin(2), 2));
        queues.add(new QueueLevel(2, new RoundRobin(4), 4));
        queues.add(new QueueLevel(3, new FCFS(), 0));
    }

    @Override
    public List<ExecutionStep> schedule(List<Process> processes) {
        List<ExecutionStep> steps = new ArrayList<>();
        List<Process> copy = new ArrayList<>(processes);
        int currentTime = 0;

        for (QueueLevel queue : queues) {
            List<Process> currentQueue = new ArrayList<>();
            for (Process p : copy) {
                if (p.getArrivalTime() <= currentTime) {
                    currentQueue.add(p);
                }
            }
            copy.removeAll(currentQueue);
            if (!currentQueue.isEmpty()) {
                List<ExecutionStep> queueSteps = queue.getScheduler().schedule(currentQueue);
                steps.addAll(queueSteps);
                for (ExecutionStep step : queueSteps) {
                    currentTime = Math.max(currentTime, step.getEndTime());
                }
            }
        }
        return steps;
    }
}