package com.example.scheduler.util;

import com.example.scheduler.algorithms.*;
import java.util.HashMap;
import java.util.Map;

public class SchedulerFactory {
    private static final Map<String, Scheduler> SCHEDULERS = new HashMap<>();

    static {
        SCHEDULERS.put("FCFS", new FCFSScheduler());
        SCHEDULERS.put("SJF (Non-Preemptive)", new SJFScheduler(false));
        SCHEDULERS.put("SJF (Preemptive)", new SJFScheduler(true));
        SCHEDULERS.put("Round Robin", new RoundRobinScheduler());
        // Stub implementations for other algorithms
        SCHEDULERS.put("Priority (Non-Preemptive)", new StubScheduler());
        SCHEDULERS.put("Priority (Preemptive)", new StubScheduler());
        SCHEDULERS.put("Multilevel Queue", new StubScheduler());
        SCHEDULERS.put("Multilevel Feedback Queue", new StubScheduler());
    }

    public static Scheduler getScheduler(String algorithm, int quantum) {
        Scheduler scheduler = SCHEDULERS.get(algorithm);
        if (scheduler == null) {
            throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
        if (scheduler instanceof RoundRobinScheduler) {
            ((RoundRobinScheduler) scheduler).setQuantum(quantum);
        }
        return scheduler;
    }
}