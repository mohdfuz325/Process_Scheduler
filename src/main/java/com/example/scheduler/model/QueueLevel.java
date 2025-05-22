package com.example.scheduler.model;

import com.example.scheduler.algorithms.Scheduler;

public class QueueLevel {
    private int priority;
    private Scheduler scheduler;
    private int quantum;

    public QueueLevel(int priority, Scheduler scheduler, int quantum) {
        this.priority = priority;
        this.scheduler = scheduler;
        this.quantum = quantum;
    }

    public int getPriority() { return priority; }
    public Scheduler getScheduler() { return scheduler; }
    public int getQuantum() { return quantum; }
}