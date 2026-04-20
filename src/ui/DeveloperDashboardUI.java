package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class DeveloperDashboardUI extends JFrame {

    private JPanel browsePanel;
    private JPanel minePanel;
    private JButton browseTab;
    private JButton mineTab;

    public DeveloperDashboardUI() {
        setTitle("SolveStack — Developer Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(Theme.WINDOW);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_LIGHT);
        root.add(Components.navbar("Developer", e -> returnToLogin()), BorderLayout.NORTH);
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
        JLabel title = new JLabel("Developer dashboard");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        JLabel sub = new JLabel("Browse challenges and track your submissions");
        sub.setFont(Theme.FONT_SMALL);
        sub.setForeground(Theme.TEXT_MUTED);
        titleLeft.add(title, BorderLayout.NORTH);
        titleLeft.add(sub, BorderLayout.CENTER);
        JButton newSub = Components.primaryBtn("+ Submit solution");
        newSub.addActionListener(e -> new SubmitSolutionUI(this, "AI-powered supply chain optimizer").setVisible(true));
        titleRow.add(titleLeft, BorderLayout.WEST);
        titleRow.add(newSub, BorderLayout.EAST);

        // Metrics
        JPanel metrics = new JPanel(new GridLayout(1, 4, 12, 0));
        metrics.setOpaque(false);
        metrics.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        metrics.add(Components.metric("Open challenges", "8"));
        metrics.add(Components.metric("My submissions",  "5"));
        metrics.add(Components.metric("Under review",    "2"));
        metrics.add(Components.metric("Accepted",        "1"));

        // Tab bar
        JPanel tabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabBar.setOpaque(false);
        tabBar.setBorder(new MatteBorder(0, 0, 1, 0, Theme.BORDER));
        tabBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        browseTab = tabButton("Browse challenges", true);
        mineTab   = tabButton("My submissions",    false);
        browseTab.addActionListener(e -> switchTab(true));
        mineTab.addActionListener(e -> switchTab(false));
        tabBar.add(browseTab);
        tabBar.add(mineTab);

        // Browse panel
        browsePanel = Components.card();
        browsePanel.setLayout(new BoxLayout(browsePanel, BoxLayout.Y_AXIS));
        browsePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 999));
        String[][] challenges = {
            {"AI-powered supply chain optimizer", "Acme Corp",   "₹5,00,000", "30 May"},
            {"Smart inventory management",        "TechVision",  "₹3,50,000", "15 Jun"},
            {"Renewable energy dashboard",        "GreenTech",   "₹1,50,000", "20 Jun"},
            {"AI crop disease detection",         "AgroTech",    "₹2,50,000", "10 Jul"},
        };
        for (int i = 0; i < challenges.length; i++) {
            browsePanel.add(challengeBrowseRow(challenges[i][0], challenges[i][1], challenges[i][2], challenges[i][3]));
            if (i < challenges.length - 1) browsePanel.add(Components.sep());
        }

        // Mine panel
        minePanel = Components.card();
        minePanel.setLayout(new BoxLayout(minePanel, BoxLayout.Y_AXIS));
        minePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 999));
        minePanel.setVisible(false);
        Object[][] mySubmissions = {
            {"AK", "Carbon footprint tracker",     "ML-based solution",        "Under review", Theme.AMBER_BG, Theme.AMBER_TEXT, "#FAEEDA", "#854F0B"},
            {"AK", "Supply chain AI optimizer",    "Graph neural network approach","Accepted",  Theme.TEAL_BG,  Theme.TEAL_TEXT,  "#E1F5EE", "#0F6E56"},
            {"AK", "Smart inventory system",       "Predictive restocking",    "Under review", Theme.AMBER_BG, Theme.AMBER_TEXT, "#FAEEDA", "#854F0B"},
        };
        for (int i = 0; i < mySubmissions.length; i++) {
            minePanel.add(submissionRow(
                    (String) mySubmissions[i][0],
                    (String) mySubmissions[i][1],
                    (String) mySubmissions[i][2],
                    (String) mySubmissions[i][3],
                    (Color) mySubmissions[i][4],
                    (Color) mySubmissions[i][5]
            ));
            if (i < mySubmissions.length - 1) minePanel.add(Components.sep());
        }

        body.add(titleRow);
        body.add(Box.createVerticalStrut(16));
        body.add(metrics);
        body.add(Box.createVerticalStrut(16));
        body.add(tabBar);
        body.add(Box.createVerticalStrut(12));
        body.add(browsePanel);
        body.add(minePanel);
        body.add(Box.createVerticalGlue());
        return body;
    }

    private JButton tabButton(String text, boolean active) {
        JButton b = new JButton(text);
        b.setFont(Theme.FONT_BODY);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, active ? 2 : 0, 0, Theme.PRIMARY),
                new EmptyBorder(8, 16, 8, 16)));
        b.setForeground(active ? Theme.PRIMARY : Theme.TEXT_MUTED);
        return b;
    }

    private void switchTab(boolean browse) {
        browsePanel.setVisible(browse);
        minePanel.setVisible(!browse);
        browseTab.setForeground(browse  ? Theme.PRIMARY : Theme.TEXT_MUTED);
        mineTab.setForeground(!browse ? Theme.PRIMARY : Theme.TEXT_MUTED);
        browseTab.setBorder(new CompoundBorder(new MatteBorder(0,0,browse  ? 2 : 0, 0, Theme.PRIMARY), new EmptyBorder(8,16,8,16)));
        mineTab.setBorder(new CompoundBorder(new MatteBorder(0,0,!browse ? 2 : 0, 0, Theme.PRIMARY), new EmptyBorder(8,16,8,16)));
        revalidate();
    }

    private JPanel challengeBrowseRow(String name, String company, String prize, String deadline) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(12, 0, 12, 0));
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JLabel n = new JLabel(name);
        n.setFont(Theme.FONT_BODY);
        n.setForeground(Theme.TEXT_PRIMARY);
        JPanel meta = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        meta.setOpaque(false);
        for (String m : new String[]{company, "Prize: " + prize, "Deadline: " + deadline}) {
            JLabel ml = new JLabel(m); ml.setFont(Theme.FONT_SMALL); ml.setForeground(Theme.TEXT_MUTED);
            meta.add(ml);
        }
        meta.add(Components.badge("Open", Theme.GREEN_BG, Theme.GREEN_TEXT));
        left.add(n); left.add(meta);
        JButton apply = Components.primaryBtn("Apply");
        apply.setPreferredSize(new Dimension(80, 28));
        apply.addActionListener(e -> new SubmitSolutionUI(this, name).setVisible(true));
        row.add(left, BorderLayout.CENTER);
        row.add(apply, BorderLayout.EAST);
        return row;
    }

    private JPanel submissionRow(String initials, String name, String desc, String status, Color sbg, Color sfg) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 0, 10, 0));
        row.add(Components.avatar(initials, new Color(206, 203, 246), new Color(60, 52, 137)), BorderLayout.WEST);
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel n = new JLabel(name);    n.setFont(Theme.FONT_BODY); n.setForeground(Theme.TEXT_PRIMARY);
        JLabel d = new JLabel(desc);    d.setFont(Theme.FONT_SMALL); d.setForeground(Theme.TEXT_MUTED);
        info.add(n); info.add(d);
        row.add(info, BorderLayout.CENTER);
        row.add(Components.badge(status, sbg, sfg), BorderLayout.EAST);
        return row;
    }

    private void returnToLogin() {
        new LoginUI().setVisible(true);
        this.dispose();
    }
}
