package com.example.scheduler.algorithms;

import com.example.scheduler.model.ExecutionStep;
import com.example.scheduler.model.Process;
import com.example.scheduler.model.QueueLevel;
import java.util.ArrayList;
import java.util.List;

public class MultilevelQueue implements Scheduler {
    private List<QueueLevel> queues;

    public MultilevelQueue() {
        queues = new ArrayList<>();
        // Example configuration
        queues.add(new QueueLevel(1, new FCFS(), 0));
        queues.add(new QueueLevel(2, new RoundRobin(4), 4));
    }

    @Override
    public List<ExecutionStep> schedule(List<Process> processes) {
        List<ExecutionStep> steps = new ArrayList<>();
        List<List<Process>> queueProcesses = new ArrayList<>();
        for (int i = 0; i < queues.size(); i++) {
            queueProcesses.add(new ArrayList<>());
        }

        for (Process p : processes) {
            int priority = p.getPriority();
            if (priority <= queues.size()) {
                queueProcesses.get(priority - 1).add(p);
            }
        }

        for (QueueLevel queue : queues) {
            steps.addAll(queue.getScheduler().schedule(queueProcesses.get(queue.getPriority() - 1)));
        }
        return steps;
    }
}