package com.example.scheduler.model;

import com.example.scheduler.db.SessionRepository;
import java.util.ArrayList;
import java.util.List;

public class Session {
    private List<Process> processes;
    private int sessionId;

    public Session() {
        this.processes = new ArrayList<>();
    }

    public void addProcess(Process process) {
        processes.add(process);
    }
    public void addProcesses(List<Process> process) {
        this.processes.addAll(processes);
    }

    public List<Process> getProcesses() {
        return processes;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public void save() {
        SessionRepository.saveSession(this);
    }
}