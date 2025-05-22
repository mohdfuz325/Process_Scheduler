package com.example.scheduler.algorithms;

import com.example.scheduler.model.Process;
import com.example.scheduler.model.ExecutionStep;
import java.util.ArrayList;
import java.util.List;

public class StubScheduler implements Scheduler {
    @Override
    public List<ExecutionStep> schedule(List<Process> processes) {
        return new ArrayList<>(); // Placeholder
    }
}