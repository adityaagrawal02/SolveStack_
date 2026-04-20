package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class SubmissionsUI extends JFrame {

    public SubmissionsUI(JFrame parent, String challengeName) {
        setTitle("SolveStack — Submissions");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(Theme.WINDOW);
        setLocationRelativeTo(parent);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_LIGHT);

        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(Theme.BG_WHITE);
        nav.setBorder(new CompoundBorder(new MatteBorder(0,0,1,0,Theme.BORDER), new EmptyBorder(10,16,10,16)));
        JLabel brandLbl = new JLabel("SolveStack — Submissions");
        brandLbl.setFont(Theme.FONT_HEAD); brandLbl.setForeground(Theme.TEXT_PRIMARY);
        JButton back = Components.smallBtn("← Back");
        back.addActionListener(e -> dispose());
        nav.add(brandLbl, BorderLayout.WEST);
        nav.add(back, BorderLayout.EAST);

        root.add(nav, BorderLayout.NORTH);
        root.add(buildBody(challengeName), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JScrollPane buildBody(String challengeName) {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Theme.BG_LIGHT);
        body.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Submissions");
        title.setFont(Theme.FONT_TITLE); title.setForeground(Theme.TEXT_PRIMARY);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = new JLabel(challengeName + " · 9 submissions");
        sub.setFont(Theme.FONT_SMALL); sub.setForeground(Theme.TEXT_MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        JPanel card = Components.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(LEFT_ALIGNMENT);

        Object[][] rows = {
            {"RK", "Rahul K.",      "Graph neural network optimizer", "Python, PyTorch, FastAPI",   "Under review", Theme.AMBER_BG, Theme.AMBER_TEXT},
            {"PM", "Priya M.",      "Reinforcement learning approach","Python, TensorFlow, Docker", "Accepted",     Theme.TEAL_BG,  Theme.TEAL_TEXT},
            {"SA", "Siddharth A.", "Genetic algorithm solver",       "Java, Spring Boot",          "Under review", Theme.AMBER_BG, Theme.AMBER_TEXT},
            {"VR", "Vishal R.",    "MILP optimization model",        "Python, PuLP, Flask",        "Pending",      Theme.GREEN_BG, Theme.GREEN_TEXT},
        };

        for (int i = 0; i < rows.length; i++) {
            Object[] r = rows[i];
            card.add(subRow((String)r[0], (String)r[1], (String)r[2], (String)r[3], (String)r[4], (Color)r[5], (Color)r[6]));
            if (i < rows.length - 1) card.add(Components.sep());
        }

        body.add(title);
        body.add(Box.createVerticalStrut(4));
        body.add(sub);
        body.add(Box.createVerticalStrut(16));
        body.add(card);
        body.add(Box.createVerticalGlue());
        return Components.scroll(body);
    }

    private JPanel subRow(String initials, String name, String solution, String tech, String status, Color sbg, Color sfg) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(12, 0, 12, 0));

        row.add(Components.avatar(initials, new Color(206, 203, 246), new Color(60, 52, 137)), BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel n = new JLabel(name + " — " + solution);
        n.setFont(Theme.FONT_BODY); n.setForeground(Theme.TEXT_PRIMARY);
        JLabel t = new JLabel(tech);
        t.setFont(Theme.FONT_SMALL); t.setForeground(Theme.TEXT_MUTED);
        info.add(n); info.add(t);

        row.add(info, BorderLayout.CENTER);
        row.add(Components.badge(status, sbg, sfg), BorderLayout.EAST);
        return row;
    }
}
