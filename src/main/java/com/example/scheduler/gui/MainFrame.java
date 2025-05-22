package com.example.scheduler.gui;

import com.example.scheduler.model.Session;
import com.example.scheduler.model.Process;
import com.example.scheduler.model.ExecutionStep;
import com.example.scheduler.util.SchedulerFactory;
import com.example.scheduler.algorithms.Scheduler;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class MainFrame extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(MainFrame.class.getName());
    private GanttChartPanel ganttPanel;
    private JTextField quantumField;
    private JComboBox<String> algorithmComboBox;
    private List<JTextField> pidFields;
    private List<JTextField> arrivalFields;
    private List<JTextField> burstFields;
    private List<JTextField> priorityFields;
    private List<JProgressBar> processProgressBars;
    private JLabel statusLabel;
    private JPanel processInputPanel;
    private JProgressBar mainProgressBar;
    private JLabel statusBar;
    private int processCount = 1;
    private ExecutorService executorService;

    public MainFrame() {
        setTitle("Process Scheduler Simulator");
        setSize(1100, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new AbsoluteLayout());
        getContentPane().setBackground(new Color(15, 15, 15));

        // Initialize thread pool
        executorService = Executors.newFixedThreadPool(3);

        // Title Label
        JLabel titleLabel = new JLabel("CPU Scheduling Simulator", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(230, 230, 230));
        getContentPane().add(titleLabel, new AbsoluteConstraints(20, 20, 1060, 40));

        // Algorithm selection panel
        JPanel algorithmPanel = new JPanel(new AbsoluteLayout());
        algorithmPanel.setBackground(new Color(25, 25, 25));
        algorithmPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)), 
            "Algorithm Settings", 
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
            javax.swing.border.TitledBorder.DEFAULT_POSITION, 
            new Font("Arial", Font.PLAIN, 14), new Color(230, 230, 230)
        ));
        getContentPane().add(algorithmPanel, new AbsoluteConstraints(20, 80, 700, 100));

        JLabel algorithmLabel = new JLabel("Algorithm:");
        algorithmLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        algorithmLabel.setForeground(new Color(230, 230, 230));
        algorithmPanel.add(algorithmLabel, new AbsoluteConstraints(20, 30, 100, 25));

        String[] algorithms = {"FCFS", "SJF (Non-Preemptive)", "SJF (Preemptive)", "Round Robin", 
                              "Priority (Non-Preemptive)", "Priority (Preemptive)", 
                              "Multilevel Queue", "Multilevel Feedback Queue"};
        algorithmComboBox = new JComboBox<>(algorithms);
        algorithmComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        algorithmComboBox.setBackground(new Color(35, 35, 35));
        algorithmComboBox.setForeground(new Color(230, 230, 230));
        algorithmComboBox.setToolTipText("Select scheduling algorithm");
        algorithmPanel.add(algorithmComboBox, new AbsoluteConstraints(120, 30, 250, 30));

        JLabel quantumLabel = new JLabel("Quantum:");
        quantumLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        quantumLabel.setForeground(new Color(230, 230, 230));
        algorithmPanel.add(quantumLabel, new AbsoluteConstraints(400, 30, 80, 25));

        quantumField = new JTextField("4", 5);
        quantumField.setFont(new Font("Arial", Font.PLAIN, 14));
        quantumField.setBackground(new Color(35, 35, 35));
        quantumField.setForeground(new Color(230, 230, 230));
        quantumField.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        quantumField.setHorizontalAlignment(JTextField.CENTER);
        quantumField.setToolTipText("Time quantum for Round Robin");
        addPlaceholder(quantumField, "4");
        algorithmPanel.add(quantumField, new AbsoluteConstraints(480, 30, 60, 30));

        // Process input panel
        processInputPanel = new JPanel();
        processInputPanel.setLayout(new AbsoluteLayout());
        processInputPanel.setBackground(new Color(25, 25, 25));
        JScrollPane inputScrollPane = new JScrollPane(processInputPanel);
        inputScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)), 
            "Process Input", 
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
            javax.swing.border.TitledBorder.DEFAULT_POSITION, 
            new Font("Arial", Font.PLAIN, 14), new Color(230, 230, 230)
        ));
        getContentPane().add(inputScrollPane, new AbsoluteConstraints(20, 200, 700, 250));

        // Initialize input fields and progress bars
        pidFields = new ArrayList<>();
        arrivalFields = new ArrayList<>();
        burstFields = new ArrayList<>();
        priorityFields = new ArrayList<>();
        processProgressBars = new ArrayList<>();
        addProcessRow(0);

        // Input labels
        JLabel pidLabel = new JLabel("PID");
        pidLabel.setFont(new Font("Arial", Font.BOLD, 12));
        pidLabel.setForeground(new Color(230, 230, 230));
        processInputPanel.add(pidLabel, new AbsoluteConstraints(20, 20, 50, 20));

        JLabel arrivalLabel = new JLabel("Arrival Time");
        arrivalLabel.setFont(new Font("Arial", Font.BOLD, 12));
        arrivalLabel.setForeground(new Color(230, 230, 230));
        processInputPanel.add(arrivalLabel, new AbsoluteConstraints(90, 20, 80, 20));

        JLabel burstLabel = new JLabel("Burst Time");
        burstLabel.setFont(new Font("Arial", Font.BOLD, 12));
        burstLabel.setForeground(new Color(230, 230, 230));
        processInputPanel.add(burstLabel, new AbsoluteConstraints(190, 20, 80, 20));

        JLabel priorityLabel = new JLabel("Priority");
        priorityLabel.setFont(new Font("Arial", Font.BOLD, 12));
        priorityLabel.setForeground(new Color(230, 230, 230));
        processInputPanel.add(priorityLabel, new AbsoluteConstraints(290, 20, 60, 20));

        JLabel progressLabel = new JLabel("Progress");
        progressLabel.setFont(new Font("Arial", Font.BOLD, 12));
        progressLabel.setForeground(new Color(230, 230, 230));
        processInputPanel.add(progressLabel, new AbsoluteConstraints(370, 20, 80, 20));

        // Control panel
        JPanel controlPanel = new JPanel(new AbsoluteLayout());
        controlPanel.setBackground(new Color(25, 25, 25));
        controlPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)), 
            "Controls", 
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
            javax.swing.border.TitledBorder.DEFAULT_POSITION, 
            new Font("Arial", Font.PLAIN, 14), new Color(230, 230, 230)
        ));
        getContentPane().add(controlPanel, new AbsoluteConstraints(740, 80, 340, 370));

        // Add process button
        JButton addProcessButton = createStyledButton("Add Process", new Color(0, 120, 215));
        addProcessButton.setToolTipText("Add a new process (max 5)");
        addProcessButton.addActionListener(e -> {
            if (processCount < 5) {
                addProcessRow(processCount);
                processCount++;
                processInputPanel.revalidate();
                processInputPanel.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Maximum 5 processes allowed");
                statusLabel.setText("Max processes reached");
            }
        });
        controlPanel.add(addProcessButton, new AbsoluteConstraints(20, 30, 300, 40));

        // Load file button
        JButton loadFileButton = createStyledButton("Load File", new Color(0, 120, 215));
        loadFileButton.setToolTipText("Load process data from a text file");
        loadFileButton.addActionListener(e -> loadFile());
        controlPanel.add(loadFileButton, new AbsoluteConstraints(20, 80, 300, 40));

        // Run button
        JButton runButton = createStyledButton("Run Simulation", new Color(0, 150, 100));
        runButton.setToolTipText("Start the scheduling simulation");
        runButton.addActionListener(e -> runSimulation());
        controlPanel.add(runButton, new AbsoluteConstraints(20, 130, 300, 40));

        // Reset button
        JButton resetButton = createStyledButton("Reset", new Color(200, 50, 50));
        resetButton.setToolTipText("Clear all inputs and outputs");
        resetButton.addActionListener(e -> reset());
        controlPanel.add(resetButton, new AbsoluteConstraints(20, 180, 300, 40));

        // Status label
        statusLabel = new JLabel("Ready to run simulation", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        statusLabel.setForeground(new Color(190, 190, 190));
        controlPanel.add(statusLabel, new AbsoluteConstraints(20, 230, 300, 30));

        // Main progress bar with pulsing animation
        mainProgressBar = new JProgressBar(0, 100);
        mainProgressBar.setFont(new Font("Arial", Font.BOLD, 14));
        mainProgressBar.setForeground(new Color(0, 180, 120));
        mainProgressBar.setBackground(new Color(35, 35, 35));
        mainProgressBar.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        mainProgressBar.setStringPainted(true);
        mainProgressBar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            @Override
            protected void paintDeterminate(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int width = c.getWidth();
                int height = c.getHeight();
                int barWidth = (int) (width * ((double) mainProgressBar.getValue() / mainProgressBar.getMaximum()));
                g2d.setColor(new Color(35, 35, 35));
                g2d.fillRect(0, 0, width, height);
                g2d.setPaint(new GradientPaint(0, 0, new Color(0, 180, 120), width, 0, new Color(0, 220, 150)));
                g2d.fillRect(0, 0, barWidth, height);
                g2d.setColor(new Color(70, 70, 70));
                g2d.drawRect(0, 0, width - 1, height - 1);
                String text = mainProgressBar.getString();
                g2d.setColor(new Color(230, 230, 230));
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();
                g2d.drawString(text, (width - textWidth) / 2, (height + textHeight) / 2 - 2);
            }
        });
        Timer pulseTimer = new Timer(500, e -> {
            mainProgressBar.setForeground(mainProgressBar.getForeground().equals(new Color(0, 180, 120)) 
                ? new Color(0, 200, 140) : new Color(0, 180, 120));
        });
        pulseTimer.start();
        controlPanel.add(mainProgressBar, new AbsoluteConstraints(20, 270, 300, 30));

        // Status bar
        statusBar = new JLabel("System ready", SwingConstants.LEFT);
        statusBar.setFont(new Font("Arial", Font.PLAIN, 12));
        statusBar.setForeground(new Color(190, 190, 190));
        statusBar.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        getContentPane().add(statusBar, new AbsoluteConstraints(20, 810, 1060, 20));

        // Gantt chart and graphs
        ganttPanel = new GanttChartPanel();
        JScrollPane ganttScrollPane = new JScrollPane(ganttPanel);
        ganttScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)), 
            "Gantt Chart & Metrics", 
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
            javax.swing.border.TitledBorder.DEFAULT_POSITION, 
            new Font("Arial", Font.PLAIN, 14), new Color(230, 230, 230)
        ));
        getContentPane().add(ganttScrollPane, new AbsoluteConstraints(20, 470, 1060, 330));

        // Update status bar on mouse hover
        JComponent[] components = {algorithmComboBox, quantumField, addProcessButton, loadFileButton, runButton, resetButton};
        for (JComponent c : components) {
            c.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (statusBar != null) {
                        String tooltip = c.getToolTipText();
                        String text = tooltip != null ? tooltip : "Hovering over " + c.getClass().getSimpleName();
                        statusBar.setText(text);
                        LOGGER.info("Status bar updated: " + text);
                    }
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    if (statusBar != null) {
                        statusBar.setText("System ready");
                        LOGGER.info("Status bar reset to: System ready");
                    }
                }
            });
        }
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setPaint(new GradientPaint(0, 0, baseColor.brighter(), 0, getHeight(), baseColor));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(new Color(230, 230, 230));
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(new Color(255, 255, 255));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(new Color(230, 230, 230));
            }
            @Override
            public void mousePressed(MouseEvent e) {
                Timer timer = new Timer(100, null);
                timer.addActionListener(evt -> {
                    int newWidth = (int) (button.getWidth() * 0.95);
                    int newHeight = (int) (button.getHeight() * 0.95);
                    int xOffset = (button.getWidth() - newWidth) / 2;
                    int yOffset = (button.getHeight() - newHeight) / 2;
                    button.setBounds(button.getX() + xOffset, button.getY() + yOffset, newWidth, newHeight);
                    button.repaint();
                    Timer revertTimer = new Timer(100, revertEvt -> {
                        button.setBounds(button.getX() - xOffset, button.getY() - yOffset, 
                                         button.getWidth() / 95 * 100, button.getHeight() / 95 * 100);
                        button.repaint();
                        timer.stop();
                    });
                    revertTimer.setRepeats(false);
                    revertTimer.start();
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
        return button;
    }

    private void addPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(new Color(230, 230, 230));
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void addProcessRow(int index) {
        int y = 50 + index * 40;
        JTextField pidField = new JTextField(5);
        pidField.setFont(new Font("Arial", Font.PLAIN, 12));
        pidField.setBackground(new Color(35, 35, 35));
        pidField.setForeground(new Color(230, 230, 230));
        pidField.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        pidField.setHorizontalAlignment(JTextField.CENTER);
        addPlaceholder(pidField, "P" + (index + 1));
        processInputPanel.add(pidField, new AbsoluteConstraints(20, y, 60, 30));
        pidFields.add(pidField);

        JTextField arrivalField = new JTextField(5);
        arrivalField.setFont(new Font("Arial", Font.PLAIN, 12));
        arrivalField.setBackground(new Color(35, 35, 35));
        arrivalField.setForeground(new Color(230, 230, 230));
        arrivalField.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        arrivalField.setHorizontalAlignment(JTextField.CENTER);
        addPlaceholder(arrivalField, "0");
        processInputPanel.add(arrivalField, new AbsoluteConstraints(90, y, 80, 30));
        arrivalFields.add(arrivalField);

        JTextField burstField = new JTextField(5);
        burstField.setFont(new Font("Arial", Font.PLAIN, 12));
        burstField.setBackground(new Color(35, 35, 35));
        burstField.setForeground(new Color(230, 230, 230));
        burstField.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        burstField.setHorizontalAlignment(JTextField.CENTER);
        addPlaceholder(burstField, "5");
        processInputPanel.add(burstField, new AbsoluteConstraints(190, y, 80, 30));
        burstFields.add(burstField);

        JTextField priorityField = new JTextField(5);
        priorityField.setFont(new Font("Arial", Font.PLAIN, 12));
        priorityField.setBackground(new Color(35, 35, 35));
        priorityField.setForeground(new Color(230, 230, 230));
        priorityField.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        priorityField.setHorizontalAlignment(JTextField.CENTER);
        addPlaceholder(priorityField, "1");
        processInputPanel.add(priorityField, new AbsoluteConstraints(290, y, 60, 30));
        priorityFields.add(priorityField);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setFont(new Font("Arial", Font.PLAIN, 10));
        progressBar.setForeground(new Color(0, 180, 120));
        progressBar.setBackground(new Color(35, 35, 35));
        progressBar.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        progressBar.setStringPainted(true);
        progressBar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            @Override
            protected void paintDeterminate(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int width = c.getWidth();
                int height = c.getHeight();
                int barWidth = (int) (width * ((double) progressBar.getValue() / progressBar.getMaximum()));
                g2d.setColor(new Color(35, 35, 35));
                g2d.fillRect(0, 0, width, height);
                g2d.setPaint(new GradientPaint(0, 0, new Color(0, 180, 120), width, 0, new Color(0, 220, 150)));
                g2d.fillRect(0, 0, barWidth, height);
                g2d.setColor(new Color(70, 70, 70));
                g2d.drawRect(0, 0, width - 1, height - 1);
                String text = progressBar.getString();
                g2d.setColor(new Color(230, 230, 230));
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();
                g2d.drawString(text, (width - textWidth) / 2, (height + textHeight) / 2 - 2);
            }
        });
        processInputPanel.add(progressBar, new AbsoluteConstraints(370, y, 100, 30));
        processProgressBars.add(progressBar);

        processInputPanel.setPreferredSize(new Dimension(480, y + 40));
    }

    private void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                reset();
                processCount = 0;
                List<String[]> processData = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null && processCount < 5) {
                    String[] parts = line.trim().split(",");
                    if (parts.length == 4) {
                        processData.add(parts);
                        processCount++;
                    }
                }
                for (int i = 0; i < processCount; i++) {
                    addProcessRow(i);
                    String[] parts = processData.get(i);
                    pidFields.get(i).setText(parts[0].trim());
                    arrivalFields.get(i).setText(parts[1].trim());
                    burstFields.get(i).setText(parts[2].trim());
                    priorityFields.get(i).setText(parts[3].trim());
                }
                processInputPanel.revalidate();
                processInputPanel.repaint();
                statusLabel.setText("File loaded successfully");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
                statusLabel.setText("Failed to load file");
            }
        }
    }

    private void runSimulation() {
        String algorithm = (String) algorithmComboBox.getSelectedItem();
        int quantum;
        try {
            quantum = Integer.parseInt(quantumField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantum value. Using default: 4");
            quantum = 4;
            quantumField.setText("4");
        }
        // Create final copies for lambda
        final String finalAlgorithm = algorithm;
        final int finalQuantum = quantum;

        Session session = new Session();
        List<Process> processes = new ArrayList<>();
        Map<String, Integer> burstTimes = new HashMap<>();
        for (int i = 0; i < processCount; i++) {
            try {
                String pid = pidFields.get(i).getText().trim();
                String arrival = arrivalFields.get(i).getText().trim();
                String burst = burstFields.get(i).getText().trim();
                String priority = priorityFields.get(i).getText().trim();
                if (pid.isEmpty() || arrival.isEmpty() || burst.isEmpty() || priority.isEmpty()) {
                    continue;
                }
                processes.add(new Process(pid, Integer.parseInt(arrival), 
                                         Integer.parseInt(burst), Integer.parseInt(priority)));
                burstTimes.put(pid, Integer.parseInt(burst));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid input in row " + (i + 1));
                statusLabel.setText("Error in input");
                return;
            }
        }
        if (processes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No valid processes entered");
            statusLabel.setText("No processes");
            return;
        }
        try {
            statusLabel.setText("Running simulation...");
            mainProgressBar.setValue(0);
            for (JProgressBar bar : processProgressBars) {
                bar.setValue(0);
                bar.setString("");
            }
            SwingWorker<Void, Integer> worker = new SwingWorker<>() {
                List<ExecutionStep> steps;
                Map<String, Integer> waitingTimes;
                Map<String, Integer> turnaroundTimes;
                Map<String, Integer> completionTimes;

                @Override
                protected Void doInBackground() throws Exception {
                    // Schedule in a separate thread
                    steps = executorService.submit(() -> {
                        LOGGER.info("Selected algorithm: " + finalAlgorithm);
                        Scheduler scheduler = SchedulerFactory.getScheduler(finalAlgorithm, finalQuantum);
                        return scheduler.schedule(processes);
                    }).get();

                    int maxTime = steps.stream().mapToInt(ExecutionStep::getEndTime).max().orElse(0);
                    mainProgressBar.setMaximum(maxTime);

                    // Calculate metrics in parallel
                    waitingTimes = executorService.submit(() -> calculateWaitingTimes(processes, steps)).get();
                    turnaroundTimes = executorService.submit(() -> calculateTurnaroundTimes(processes, steps)).get();
                    completionTimes = executorService.submit(() -> calculateCompletionTimes(processes, steps)).get();

                    // Update progress bars
                    Map<String, Integer> executedTime = new HashMap<>();
                    for (Process p : processes) {
                        executedTime.put(p.getPid(), 0);
                    }
                    for (int time = 0; time <= maxTime; time++) {
                        for (ExecutionStep step : steps) {
                            if (time >= step.getStartTime() && time < step.getEndTime()) {
                                String pid = step.getProcessId();
                                executedTime.put(pid, executedTime.getOrDefault(pid, 0) + 1);
                                int index = processes.indexOf(processes.stream()
                                    .filter(p -> p.getPid().equals(pid)).findFirst().orElse(null));
                                if (index >= 0) {
                                    int progress = (int) ((double) executedTime.get(pid) / burstTimes.get(pid) * 100);
                                    SwingUtilities.invokeLater(() -> {
                                        processProgressBars.get(index).setValue(progress);
                                        processProgressBars.get(index).setString(progress + "%");
                                    });
                                }
                            }
                        }
                        publish(time);
                        Thread.sleep(500);
                    }
                    return null;
                }

                @Override
                protected void process(List<Integer> chunks) {
                    int time = chunks.get(chunks.size() - 1);
                    SwingUtilities.invokeLater(() -> {
                        mainProgressBar.setValue(time);
                        mainProgressBar.setString(String.format("%d/%d", time, mainProgressBar.getMaximum()));
                        ganttPanel.setCurrentTime(time);
                    });
                }

                @Override
                protected void done() {
                    SwingUtilities.invokeLater(() -> {
                        ganttPanel.setExecutionSteps(steps, waitingTimes, turnaroundTimes, completionTimes, processes);
                        mainProgressBar.setString("Done");
                        statusLabel.setText("Simulation completed");
                    });
                    // Save to database in a separate thread
                    executorService.submit(() -> {
                        session.addProcesses(processes);
                        session.save();
                    });
                }
            };
            worker.execute();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error running simulation: " + e.getMessage());
            statusLabel.setText("Simulation failed");
            mainProgressBar.setValue(0);
            mainProgressBar.setString("");
            LOGGER.severe("Simulation error: " + e.getMessage());
        }
    }

    private void reset() {
        quantumField.setText("4");
        processCount = 1;
        processInputPanel.removeAll();
        pidFields.clear();
        arrivalFields.clear();
        burstFields.clear();
        priorityFields.clear();
        processProgressBars.clear();
        // Add input labels
        JLabel pidLabel = new JLabel("PID");
        pidLabel.setFont(new Font("Arial", Font.BOLD, 12));
        pidLabel.setForeground(new Color(230, 230, 230));
        processInputPanel.add(pidLabel, new AbsoluteConstraints(20, 20, 50, 20));

        JLabel arrivalLabel = new JLabel("Arrival Time");
        arrivalLabel.setFont(new Font("Arial", Font.BOLD, 12));
        arrivalLabel.setForeground(new Color(230, 230, 230));
        processInputPanel.add(arrivalLabel, new AbsoluteConstraints(90, 20, 80, 20));

        JLabel burstLabel = new JLabel("Burst Time");
        burstLabel.setFont(new Font("Arial", Font.BOLD, 12));
        burstLabel.setForeground(new Color(230, 230, 230));
        processInputPanel.add(burstLabel, new AbsoluteConstraints(190, 20, 80, 20));

        JLabel priorityLabel = new JLabel("Priority");
        priorityLabel.setFont(new Font("Arial", Font.BOLD, 12));
        pidLabel.setForeground(new Color(230, 230, 230));
        processInputPanel.add(priorityLabel, new AbsoluteConstraints(290, 20, 60, 20));

        JLabel progressLabel = new JLabel("Progress");
        progressLabel.setFont(new Font("Arial", Font.BOLD, 12));
        progressLabel.setForeground(new Color(230, 230, 230));
        processInputPanel.add(progressLabel, new AbsoluteConstraints(370, 20, 80, 20));

        addProcessRow(0);
        ganttPanel.clear();
        mainProgressBar.setValue(0);
        mainProgressBar.setString("");
        statusLabel.setText("Ready to run simulation");
        processInputPanel.revalidate();
        processInputPanel.repaint();
    }

    private Map<String, Integer> calculateWaitingTimes(List<Process> processes, List<ExecutionStep> steps) {
        Map<String, Integer> waitingTimes = new HashMap<>();
        for (Process p : processes) {
            String pid = p.getPid();
            int totalExecution = 0;
            int lastEndTime = 0;
            for (ExecutionStep step : steps) {
                if (step.getProcessId().equals(pid)) {
                    totalExecution += step.getEndTime() - step.getStartTime();
                    lastEndTime = Math.max(lastEndTime, step.getEndTime());
                }
            }
            int completionTime = lastEndTime;
            int turnaroundTime = completionTime - p.getArrivalTime();
            int waitingTime = turnaroundTime - p.getBurstTime();
            waitingTimes.put(pid, Math.max(0, waitingTime));
            LOGGER.info(String.format("PID: %s, Completion: %d, Turnaround: %d, Waiting: %d", 
                                     pid, completionTime, turnaroundTime, waitingTime));
        }
        return waitingTimes;
    }

    private Map<String, Integer> calculateTurnaroundTimes(List<Process> processes, List<ExecutionStep> steps) {
        Map<String, Integer> turnaroundTimes = new HashMap<>();
        for (Process p : processes) {
            String pid = p.getPid();
            int lastEndTime = 0;
            for (ExecutionStep step : steps) {
                if (step.getProcessId().equals(pid)) {
                    lastEndTime = Math.max(lastEndTime, step.getEndTime());
                }
            }
            int turnaroundTime = lastEndTime - p.getArrivalTime();
            turnaroundTimes.put(pid, Math.max(0, turnaroundTime));
        }
        return turnaroundTimes;
    }

    private Map<String, Integer> calculateCompletionTimes(List<Process> processes, List<ExecutionStep> steps) {
        Map<String, Integer> completionTimes = new HashMap<>();
        for (Process p : processes) {
            String pid = p.getPid();
            int lastEndTime = 0;
            for (ExecutionStep step : steps) {
                if (step.getProcessId().equals(pid)) {
                    lastEndTime = Math.max(lastEndTime, step.getEndTime());
                }
            }
            completionTimes.put(pid, lastEndTime);
        }
        return completionTimes;
    }

    @Override
    public void dispose() {
        executorService.shutdown();
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}