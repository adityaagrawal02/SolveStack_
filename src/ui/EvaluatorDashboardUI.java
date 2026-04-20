package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class EvaluatorDashboardUI extends JFrame {

    public EvaluatorDashboardUI() {
        setTitle("SolveStack — Evaluator Panel");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(Theme.WINDOW);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_LIGHT);
        root.add(Components.navbar("Evaluator", e -> returnToLogin()), BorderLayout.NORTH);
        root.add(buildBody(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Theme.BG_LIGHT);
        body.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title row
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        titleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        JPanel titleLeft = new JPanel(new BorderLayout(0, 3));
        titleLeft.setOpaque(false);
        JLabel title = new JLabel("Evaluator panel");
        title.setFont(Theme.FONT_TITLE); title.setForeground(Theme.TEXT_PRIMARY);
        JLabel sub = new JLabel("Review and score submissions");
        sub.setFont(Theme.FONT_SMALL); sub.setForeground(Theme.TEXT_MUTED);
        titleLeft.add(title, BorderLayout.NORTH);
        titleLeft.add(sub, BorderLayout.CENTER);
        JButton lbBtn = Components.outlineBtn("View leaderboard");
        lbBtn.addActionListener(e -> new LeaderboardUI(this).setVisible(true));
        titleRow.add(titleLeft, BorderLayout.WEST);
        titleRow.add(lbBtn, BorderLayout.EAST);

        // Metrics
        JPanel metrics = new JPanel(new GridLayout(1, 4, 12, 0));
        metrics.setOpaque(false);
        metrics.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        metrics.add(Components.metric("Pending review",   "7"));
        metrics.add(Components.metric("Reviewed today",   "3"));
        metrics.add(Components.metric("Avg score given",  "74"));
        metrics.add(Components.metric("Total evaluated",  "29"));

        // Submissions card
        JPanel card = Components.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 999));

        JLabel header = Components.sectionHeader("Pending submissions");
        header.setBorder(new EmptyBorder(0, 0, 12, 0));
        card.add(header);

        String[][] subs = {
            {"RK", "Rahul K.",      "AI supply chain solution",      "AI-powered supply chain"},
            {"PM", "Priya M.",      "Carbon footprint tracker",      "Carbon footprint tracker"},
            {"SA", "Siddharth A.", "Inventory optimizer",           "Smart inventory"},
        };
        for (int i = 0; i < subs.length; i++) {
            final String[] s = subs[i];
            card.add(pendingRow(s[0], s[1], s[2], s[3]));
            if (i < subs.length - 1) card.add(Components.sep());
        }

        body.add(titleRow);
        body.add(Box.createVerticalStrut(16));
        body.add(metrics);
        body.add(Box.createVerticalStrut(16));
        body.add(card);
        body.add(Box.createVerticalGlue());
        return body;
    }

    private JPanel pendingRow(String initials, String name, String solution, String challenge) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 0, 10, 0));
        row.add(Components.avatar(initials, new Color(250, 199, 117), new Color(99, 56, 6)), BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel n = new JLabel(name + " — " + solution);
        n.setFont(Theme.FONT_BODY); n.setForeground(Theme.TEXT_PRIMARY);
        JLabel d = new JLabel("Challenge: " + challenge);
        d.setFont(Theme.FONT_SMALL); d.setForeground(Theme.TEXT_MUTED);
        info.add(n); info.add(d);

        JButton evalBtn = Components.primaryBtn("Evaluate");
        evalBtn.setPreferredSize(new Dimension(90, 28));
        evalBtn.addActionListener(e -> new EvaluationUI(this, name, solution).setVisible(true));

        row.add(info, BorderLayout.CENTER);
        row.add(evalBtn, BorderLayout.EAST);
        return row;
    }

    private void returnToLogin() {
        new LoginUI().setVisible(true);
        this.dispose();
    }
}
