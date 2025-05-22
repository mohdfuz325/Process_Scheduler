package com.example.scheduler.algorithms;

import com.example.scheduler.model.ExecutionStep;
import com.example.scheduler.model.Process;
import java.util.List;

public interface Scheduler {
    List<ExecutionStep> schedule(List<Process> processes);
}