package com.example.scheduler.model;

public class Process {
    private String pid;
    private int arrivalTime;
    private int burstTime;
    private int priority;
    private int remainingTime;

    public Process(String pid, int arrivalTime, int burstTime, int priority) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.priority = priority;
    }

    // Getters and setters
    public String getPid() { return pid; }
    public int getArrivalTime() { return arrivalTime; }
    public int getBurstTime() { return burstTime; }
    public int getPriority() { return priority; }
    public int getRemainingTime() { return remainingTime; }
    public void setRemainingTime(int remainingTime) { this.remainingTime = remainingTime; }
}