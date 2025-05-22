package com.example.scheduler.gui;

import com.example.scheduler.model.ExecutionStep;
import com.example.scheduler.model.Process;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

public class GanttChartPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(GanttChartPanel.class.getName());
    private List<ExecutionStep> steps = new ArrayList<>();
    private List<Process> processes = new ArrayList<>();
    private Map<String, Integer> waitingTimes = new HashMap<>();
    private Map<String, Integer> turnaroundTimes = new HashMap<>();
    private Map<String, Integer> completionTimes = new HashMap<>();
    private final Map<String, Color> processColors = new HashMap<>();
    private int maxTime = 0;
    private int currentTime = -1;
    private JTable metricsTable;
    private JScrollPane tableScrollPane;
    private final DefaultTableCellRenderer centerRenderer;

    public GanttChartPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(25, 25, 25));
        setPreferredSize(new Dimension(1000, 400));

        // Initialize center renderer
        centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Assign colors to processes
        processColors.put("P1", new Color(0, 120, 215));
        processColors.put("P2", new Color(0, 150, 100));
        processColors.put("P3", new Color(200, 50, 50));
        processColors.put("P4", new Color(150, 100, 200));
        processColors.put("P5", new Color(255, 150, 0));

        // Initialize metrics table
        String[] columns = {"PID", "Waiting Time", "Turnaround Time", "Completion Time"};
        Object[][] data = {};
        metricsTable = new JTable(data, columns);
        metricsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        metricsTable.setBackground(new Color(35, 35, 35));
        metricsTable.setForeground(new Color(230, 230, 230));
        metricsTable.setGridColor(new Color(70, 70, 70));
        metricsTable.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        metricsTable.setRowHeight(25);
        for (int i = 0; i < metricsTable.getColumnCount(); i++) {
            metricsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            metricsTable.getColumnModel().getColumn(i).setPreferredWidth(i == 0 ? 80 : 120);
        }
        tableScrollPane = new JScrollPane(metricsTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            "Process Metrics",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.PLAIN, 14), new Color(230, 230, 230)
        ));
        add(tableScrollPane, BorderLayout.SOUTH);

        // Add tooltip support
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                String tooltip = getTooltipAt(e.getX(), e.getY());
                setToolTipText(tooltip);
            }
        });
    }

    public void setExecutionSteps(List<ExecutionStep> steps, Map<String, Integer> waitingTimes, 
                                 Map<String, Integer> turnaroundTimes, Map<String, Integer> completionTimes, 
                                 List<Process> processes) {
        this.steps = steps;
        this.waitingTimes = waitingTimes;
        this.turnaroundTimes = turnaroundTimes;
        this.completionTimes = completionTimes;
        this.processes = processes;
        this.currentTime = -1;
        maxTime = steps.isEmpty() ? 0 : steps.stream().mapToInt(ExecutionStep::getEndTime).max().orElse(0);
        setPreferredSize(new Dimension(Math.max(1000, maxTime * 35 + 100), 400));

        // Update metrics table
        Object[][] data = new Object[processes.size()][4];
        for (int i = 0; i < processes.size(); i++) {
            String pid = processes.get(i).getPid();
            data[i][0] = pid;
            data[i][1] = waitingTimes.getOrDefault(pid, 0);
            data[i][2] = turnaroundTimes.getOrDefault(pid, 0);
            data[i][3] = completionTimes.getOrDefault(pid, 0);
            LOGGER.info(String.format("Table PID: %s, Waiting: %d, Turnaround: %d, Completion: %d", 
                                     pid, data[i][1], data[i][2], data[i][3]));
        }
        String[] columns = {"PID", "Waiting Time", "Turnaround Time", "Completion Time"};
        metricsTable.setModel(new javax.swing.table.DefaultTableModel(data, columns));
        for (int i = 0; i < metricsTable.getColumnCount(); i++) {
            metricsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            metricsTable.getColumnModel().getColumn(i).setPreferredWidth(i == 0 ? 80 : 120);
        }
        metricsTable.setBackground(new Color(35, 35, 35));
        metricsTable.setForeground(new Color(230, 230, 230));

        revalidate();
        repaint();
    }

    public void setCurrentTime(int time) {
        this.currentTime = time;
        repaint();
    }

    public void clear() {
        this.steps = new ArrayList<>();
        this.processes = new ArrayList<>();
        this.waitingTimes = new HashMap<>();
        this.turnaroundTimes = new HashMap<>();
        this.completionTimes = new HashMap<>();
        this.currentTime = -1;
        maxTime = 0;
        setPreferredSize(new Dimension(1000, 400));
        metricsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][] {}, 
            new String[] {"PID", "Waiting Time", "Turnaround Time", "Completion Time"}
        ));
        for (int i = 0; i < metricsTable.getColumnCount(); i++) {
            metricsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            metricsTable.getColumnModel().getColumn(i).setPreferredWidth(i == 0 ? 80 : 120);
        }
        revalidate();
        repaint();
    }

    private String getTooltipAt(int x, int y) {
        int ganttY = 30;
        int barHeight = 40;
        if (y >= ganttY && y <= ganttY + barHeight) {
            for (ExecutionStep step : steps) {
                int startX = 60 + step.getStartTime() * 35;
                int endX = 60 + step.getEndTime() * 35;
                if (x >= startX && x <= endX) {
                    String pid = step.getProcessId();
                    int waiting = waitingTimes.getOrDefault(pid, 0);
                    int turnaround = turnaroundTimes.getOrDefault(pid, 0);
                    int completion = completionTimes.getOrDefault(pid, 0);
                    int burst = processes.stream()
                        .filter(p -> p.getPid().equals(pid))
                        .mapToInt(Process::getBurstTime)
                        .findFirst()
                        .orElse(0);
                    int arrival = processes.stream()
                        .filter(p -> p.getPid().equals(pid))
                        .mapToInt(Process::getArrivalTime)
                        .findFirst()
                        .orElse(0);
                    return String.format(
                        "<html><b>%s</b><br>Arrival: %d<br>Burst: %d<br>Waiting: %d<br>Turnaround: %d<br>Completion: %d</html>",
                        pid, arrival, burst, waiting, turnaround, completion
                    );
                }
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw Gantt chart title
        int y = 20;
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.setColor(new Color(230, 230, 230));
        g2d.drawString("Gantt Chart", 10, y);

        // Draw Gantt chart
        y = 30;
        int barHeight = 40;
        int barSpacing = 5;

        // Draw timeline
        for (int t = 0; t <= maxTime; t++) {
            int x = 60 + t * 35;
            g2d.setColor(new Color(70, 70, 70));
            g2d.drawLine(x, y, x, y + barHeight);
            g2d.setColor(new Color(230, 230, 230));
            String timeLabel = String.valueOf(t);
            FontMetrics fm = g2d.getFontMetrics();
            int labelWidth = fm.stringWidth(timeLabel);
            g2d.drawString(timeLabel, x - labelWidth / 2, y - 5);
        }

        // Draw bars
        for (ExecutionStep step : steps) {
            int startX = 60 + step.getStartTime() * 35;
            int endX = 60 + step.getEndTime() * 35;
            int width = endX - startX;
            Color baseColor = processColors.getOrDefault(step.getProcessId(), Color.GRAY);
            boolean isActive = currentTime >= step.getStartTime() && currentTime < step.getEndTime();
            g2d.setColor(isActive ? baseColor.brighter() : baseColor);
            g2d.fillRect(startX, y + barSpacing, width, barHeight - 2 * barSpacing);
            g2d.setColor(isActive ? Color.YELLOW : Color.BLACK);
            g2d.drawRect(startX, y + barSpacing, width, barHeight - 2 * barSpacing);
            g2d.setColor(new Color(230, 230, 230));
            String label = step.getProcessId() + " (" + step.getStartTime() + "-" + step.getEndTime() + ")";
            FontMetrics fm = g2d.getFontMetrics();
            int labelWidth = fm.stringWidth(label);
            int labelHeight = fm.getAscent();
            int labelX = startX + (width - labelWidth) / 2;
            int labelY = y + barSpacing + (barHeight - 2 * barSpacing + labelHeight) / 2;
            g2d.drawString(label, labelX, labelY);
        }

        // Draw waiting time graph
        y = 110;
        g2d.setColor(new Color(230, 230, 230));
        g2d.drawString("Waiting Time per Process", 10, y - 5);
        int barWidth = 60;
        int maxWait = waitingTimes.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        int graphHeight = 100;
        int x = 60;
        for (Map.Entry<String, Integer> entry : waitingTimes.entrySet()) {
            String pid = entry.getKey();
            int wait = entry.getValue();
            int barHeightScaled = (int) ((double) wait / maxWait * graphHeight);
            Color color = processColors.getOrDefault(pid, Color.GRAY);
            g2d.setColor(color);
            g2d.fillRect(x, y + graphHeight - barHeightScaled, barWidth - 10, barHeightScaled);
            g2d.setColor(new Color(230, 230, 230));
            g2d.drawString(pid + ": " + wait, x, y + graphHeight + 15);
            x += barWidth;
        }

        // Draw turnaround time graph
        y = 230;
        g2d.setColor(new Color(230, 230, 230));
        g2d.drawString("Turnaround Time per Process", 10, y - 5);
        int maxTurnaround = turnaroundTimes.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        x = 60;
        for (Map.Entry<String, Integer> entry : turnaroundTimes.entrySet()) {
            String pid = entry.getKey();
            int turnaround = entry.getValue();
            int barHeightScaled = (int) ((double) turnaround / maxTurnaround * graphHeight);
            Color color = processColors.getOrDefault(pid, Color.GRAY);
            g2d.setColor(color);
            g2d.fillRect(x, y + graphHeight - barHeightScaled, barWidth - 10, barHeightScaled);
            g2d.setColor(new Color(230, 230, 230));
            g2d.drawString(pid + ": " + turnaround, x, y + graphHeight + 15);
            x += barWidth;
        }
    }
}