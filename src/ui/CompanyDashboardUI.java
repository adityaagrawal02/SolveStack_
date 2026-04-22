package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class CompanyDashboardUI extends JFrame {

    public CompanyDashboardUI() {
        // Check if user is logged in and has Company role
        UserSession session = UserSession.getInstance();
        if (!session.isLoggedIn() || !session.hasRole("Company")) {
            JOptionPane.showMessageDialog(null, "Unauthorized access. Please login as a Company.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            new LoginUI().setVisible(true);
            System.exit(0);
        }

        setTitle("SolveStack — Company Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(Theme.WINDOW);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_LIGHT);

        root.add(Components.navbar("Company", e -> logout()), BorderLayout.NORTH);
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
        JPanel titleLeft = new JPanel(new BorderLayout(0, 3));
        titleLeft.setOpaque(false);
        JLabel title = new JLabel("Company dashboard");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        JLabel sub = new JLabel("Manage challenges and track submissions");
        sub.setFont(Theme.FONT_SMALL);
        sub.setForeground(Theme.TEXT_MUTED);
        titleLeft.add(title, BorderLayout.NORTH);
        titleLeft.add(sub, BorderLayout.CENTER);
        JButton newChallenge = Components.primaryBtn("+ New challenge");
        newChallenge.addActionListener(e -> new CreateChallengeUI(this).setVisible(true));
        titleRow.add(titleLeft, BorderLayout.WEST);
        titleRow.add(newChallenge, BorderLayout.EAST);
        titleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        // Metrics
        JPanel metrics = new JPanel(new GridLayout(1, 4, 12, 0));
        metrics.setOpaque(false);
        metrics.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        metrics.add(Components.metric("Active challenges", "4"));
        metrics.add(Components.metric("Total submissions", "31"));
        metrics.add(Components.metric("Under review",      "12"));
        metrics.add(Components.metric("Accepted solutions","3"));

        // Challenges card
        JPanel challengeCard = Components.card();
        challengeCard.setLayout(new BorderLayout());
        challengeCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 999));

        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);
        cardHeader.setBorder(new EmptyBorder(0, 0, 10, 0));
        JLabel ch = Components.sectionHeader("My challenges");
        JButton viewAll = Components.smallBtn("View all");
        viewAll.addActionListener(e -> new ChallengeUI(this).setVisible(true));
        cardHeader.add(ch, BorderLayout.WEST);
        cardHeader.add(viewAll, BorderLayout.EAST);

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        list.add(challengeRow("AI-powered supply chain optimizer",     "₹5,00,000", "9",  "Open",         Theme.GREEN_BG,  Theme.GREEN_TEXT));
        list.add(Components.sep());
        list.add(challengeRow("Carbon footprint tracker app",          "₹2,00,000", "14", "Under review", Theme.AMBER_BG,  Theme.AMBER_TEXT));
        list.add(Components.sep());
        list.add(challengeRow("Smart inventory management",            "₹3,50,000", "8",  "Open",         Theme.GREEN_BG,  Theme.GREEN_TEXT));

        challengeCard.add(cardHeader, BorderLayout.NORTH);
        challengeCard.add(list, BorderLayout.CENTER);

        body.add(titleRow);
        body.add(Box.createVerticalStrut(16));
        body.add(metrics);
        body.add(Box.createVerticalStrut(16));
        body.add(challengeCard);
        body.add(Box.createVerticalGlue());
        return body;
    }

    private JPanel challengeRow(String name, String prize, String subs, String status, Color sbg, Color sfg) {
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
        meta.add(metaLabel("Prize: " + prize));
        meta.add(metaLabel("Submissions: " + subs));
        meta.add(Components.badge(status, sbg, sfg));
        left.add(n);
        left.add(meta);

        JButton viewSubs = Components.smallBtn("View subs");
        viewSubs.addActionListener(e -> new SubmissionsUI(this, name).setVisible(true));

        row.add(left,    BorderLayout.CENTER);
        row.add(viewSubs, BorderLayout.EAST);
        return row;
    }

    private JLabel metaLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(Theme.TEXT_MUTED);
        return l;
    }

    private void logout() {
        UserSession.getInstance().logout();
        new LoginUI().setVisible(true);
        this.dispose();
    }
}
