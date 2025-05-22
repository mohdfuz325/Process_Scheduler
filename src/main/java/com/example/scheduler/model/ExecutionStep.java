package com.example.scheduler.model;

public class ExecutionStep {
    private String processId;
    private int startTime;
    private int endTime;

    public ExecutionStep(String processId, int startTime, int endTime) {
        this.processId = processId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getProcessId() { return processId; }
    public int getStartTime() { return startTime; }
    public int getEndTime() { return endTime; }
}