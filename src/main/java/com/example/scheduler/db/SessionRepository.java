package com.example.scheduler.db;

import com.example.scheduler.model.Process;
import com.example.scheduler.model.Session;
import com.example.scheduler.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

public class SessionRepository {
    public static void saveSession(Session session) {
        try (Connection conn = DBConnection.getConnection()) {
            // Insert session and get generated ID
            String sessionSql = "INSERT INTO sessions () VALUES ()";
            PreparedStatement sessionStmt = conn.prepareStatement(sessionSql, Statement.RETURN_GENERATED_KEYS);
            sessionStmt.executeUpdate();
            ResultSet rs = sessionStmt.getGeneratedKeys();
            int sessionId = 0;
            if (rs.next()) {
                sessionId = rs.getInt(1);
            }

            // Insert processes with the session ID
            String processSql = "INSERT INTO processes (session_id, pid, arrival_time, burst_time, priority) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement processStmt = conn.prepareStatement(processSql);
            for (Process p : session.getProcesses()) {
                processStmt.setInt(1, sessionId);
                processStmt.setString(2, p.getPid());
                processStmt.setInt(3, p.getArrivalTime());
                processStmt.setInt(4, p.getBurstTime());
                processStmt.setInt(5, p.getPriority());
                processStmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}