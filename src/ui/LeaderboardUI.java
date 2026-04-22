package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class LeaderboardUI extends JFrame {

    public LeaderboardUI(JFrame parent) {
        setTitle("SolveStack — Leaderboard");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(Theme.WINDOW);
        setLocationRelativeTo(parent);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_LIGHT);

        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(Theme.BG_WHITE);
        nav.setBorder(new CompoundBorder(new MatteBorder(0,0,1,0,Theme.BORDER), new EmptyBorder(10,16,10,16)));
        JLabel brandLbl = new JLabel("SolveStack — Leaderboard");
        brandLbl.setFont(Theme.FONT_HEAD); brandLbl.setForeground(Theme.TEXT_PRIMARY);
        JButton back = Components.smallBtn("← Back");
        back.addActionListener(e -> dispose());
        nav.add(brandLbl, BorderLayout.WEST);
        nav.add(back, BorderLayout.EAST);

        root.add(nav, BorderLayout.NORTH);
        root.add(buildBody(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JScrollPane buildBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Theme.BG_LIGHT);
        body.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Leaderboard");
        title.setFont(Theme.FONT_TITLE); title.setForeground(Theme.TEXT_PRIMARY);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = new JLabel("Top solutions ranked by evaluation score");
        sub.setFont(Theme.FONT_SMALL); sub.setForeground(Theme.TEXT_MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        JPanel card = Components.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(LEFT_ALIGNMENT);

        Object[][] entries = {
            {1,  "PM", "Priya M.",      new Color(250,199,117), new Color(99,56,6),    "Reinforcement learning approach",    "Carbon footprint tracker",  "Accepted",     Theme.TEAL_BG,  Theme.TEAL_TEXT,  94},
            {2,  "RK", "Rahul K.",      new Color(206,203,246), new Color(60,52,137),  "Graph neural network optimizer",     "Supply chain AI",           "Under review", Theme.AMBER_BG, Theme.AMBER_TEXT, 88},
            {3,  "SA", "Siddharth A.", new Color(245,196,179), new Color(113,43,19),  "Genetic algorithm solver",           "Smart inventory",           "Under review", Theme.AMBER_BG, Theme.AMBER_TEXT, 82},
            {4,  "VR", "Vishal R.",    new Color(159,225,203), new Color(8,80,65),    "MILP optimization model",            "Supply chain AI",           "Pending",      Theme.GREEN_BG, Theme.GREEN_TEXT, 76},
            {5,  "AK", "Ananya K.",    new Color(238,237,254), new Color(60,52,137),  "IoT sensor integration",             "Renewable energy dashboard","Open",         Theme.GREEN_BG, Theme.GREEN_TEXT, 71},
        };

        for (int i = 0; i < entries.length; i++) {
            Object[] e = entries[i];
            card.add(leaderRow((int)e[0], (String)e[1], (String)e[2], (Color)e[3], (Color)e[4],
                    (String)e[5], (String)e[6], (String)e[7], (Color)e[8], (Color)e[9], (int)e[10]));
            if (i < entries.length - 1) card.add(Components.sep());
        }

        body.add(title);
        body.add(Box.createVerticalStrut(4));
        body.add(sub);
        body.add(Box.createVerticalStrut(16));
        body.add(card);
        body.add(Box.createVerticalGlue());
        return Components.scroll(body);
    }

    private JPanel leaderRow(int rank, String initials, String name, Color avBg, Color avFg,
                              String solution, String challenge, String status,
                              Color sbg, Color sfg, int score) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(12, 0, 12, 0));

        // Rank badge
        JPanel rankBadge = rankBadge(rank);
        row.add(rankBadge, BorderLayout.WEST);

        // Avatar + info
        JPanel mid = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        mid.setOpaque(false);
        mid.add(Components.avatar(initials, avBg, avFg));
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel n = new JLabel(name + " — " + solution);
        n.setFont(Theme.FONT_BODY); n.setForeground(Theme.TEXT_PRIMARY);
        JLabel d = new JLabel(challenge + " · " + status);
        d.setFont(Theme.FONT_SMALL); d.setForeground(Theme.TEXT_MUTED);
        info.add(n); info.add(d);
        mid.add(info);
        row.add(mid, BorderLayout.CENTER);

        // Score
        JPanel scorePanel = new JPanel(new BorderLayout(0, 2));
        scorePanel.setOpaque(false);
        JLabel sv = new JLabel(String.valueOf(score)); sv.setFont(new Font("Segoe UI", Font.BOLD, 20)); sv.setForeground(Theme.PRIMARY); sv.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel so = new JLabel("/ 100"); so.setFont(Theme.FONT_SMALL); so.setForeground(Theme.TEXT_MUTED); so.setHorizontalAlignment(SwingConstants.RIGHT);
        scorePanel.add(sv, BorderLayout.NORTH);
        scorePanel.add(so, BorderLayout.CENTER);
        row.add(scorePanel, BorderLayout.EAST);
        return row;
    }

    private JPanel rankBadge(int rank) {
        Color bg, fg;
        switch (rank) {
            case 1  -> { bg = new Color(250, 238, 218); fg = new Color(133, 79, 11); }
            case 2  -> { bg = new Color(241, 239, 232); fg = new Color(95, 94, 90); }
            case 3  -> { bg = new Color(250, 236, 231); fg = new Color(153, 60, 29); }
            default -> { bg = Theme.BG_SECONDARY;       fg = Theme.TEXT_MUTED; }
        }
        final Color bg2 = bg, fg2 = fg;
        return new JPanel() {
            { setPreferredSize(new Dimension(30, 30)); setOpaque(false); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg2); g2.fillOval(0, 0, 29, 29);
                g2.setColor(fg2); g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String s = String.valueOf(rank);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(s, (29 - fm.stringWidth(s)) / 2, (29 + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
    }
}
