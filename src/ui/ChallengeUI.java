package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class ChallengeUI extends JFrame {

    public ChallengeUI(JFrame parent) {
        setTitle("SolveStack — Challenges");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(Theme.WINDOW);
        setLocationRelativeTo(parent);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_LIGHT);

        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(Theme.BG_WHITE);
        nav.setBorder(new CompoundBorder(new MatteBorder(0,0,1,0,Theme.BORDER), new EmptyBorder(10,16,10,16)));
        JLabel brandLbl = new JLabel("SolveStack — Challenges");
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

        JLabel title = new JLabel("Challenges");
        title.setFont(Theme.FONT_TITLE); title.setForeground(Theme.TEXT_PRIMARY);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = new JLabel("Browse all open innovation challenges");
        sub.setFont(Theme.FONT_SMALL); sub.setForeground(Theme.TEXT_MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        JPanel card = Components.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(LEFT_ALIGNMENT);

        Object[][] challenges = {
            {"AI-powered supply chain optimizer", "Acme Corp",    "₹5,00,000", "30 May", "9",  "Open",         Theme.GREEN_BG,  Theme.GREEN_TEXT,  true},
            {"Carbon footprint tracker app",      "GreenTech",    "₹2,00,000", "15 Jun", "14", "Under review", Theme.AMBER_BG,  Theme.AMBER_TEXT,  false},
            {"Smart inventory management",        "TechVision",   "₹3,50,000", "15 Jun", "8",  "Open",         Theme.GREEN_BG,  Theme.GREEN_TEXT,  true},
            {"Renewable energy dashboard",        "GreenTech",    "₹1,50,000", "20 Jun", "5",  "Open",         Theme.GREEN_BG,  Theme.GREEN_TEXT,  true},
            {"AI crop disease detection",         "AgroTech",     "₹2,50,000", "10 Jul", "2",  "Open",         Theme.GREEN_BG,  Theme.GREEN_TEXT,  true},
        };

        for (int i = 0; i < challenges.length; i++) {
            final Object[] c = challenges[i];
            card.add(challengeRow((String)c[0], (String)c[1], (String)c[2], (String)c[3], (String)c[4],
                    (String)c[5], (Color)c[6], (Color)c[7], (Boolean)c[8]));
            if (i < challenges.length - 1) card.add(Components.sep());
        }

        body.add(title);
        body.add(Box.createVerticalStrut(4));
        body.add(sub);
        body.add(Box.createVerticalStrut(16));
        body.add(card);
        body.add(Box.createVerticalGlue());
        return Components.scroll(body);
    }

    private JPanel challengeRow(String name, String company, String prize, String deadline,
                                 String subs, String status, Color sbg, Color sfg, boolean canApply) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(12, 0, 12, 0));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JLabel n = new JLabel(name); n.setFont(Theme.FONT_BODY); n.setForeground(Theme.TEXT_PRIMARY);
        JPanel meta = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        meta.setOpaque(false);
        for (String m : new String[]{company, "Prize: " + prize, "Deadline: " + deadline, "Submissions: " + subs})
            meta.add(smallMeta(m));
        meta.add(Components.badge(status, sbg, sfg));
        left.add(n); left.add(meta);

        JButton btn = canApply ? Components.primaryBtn("Apply") : Components.smallBtn("Closed");
        btn.setPreferredSize(new Dimension(80, 28));
        if (canApply) {
            final String cname = name;
            btn.addActionListener(e -> new SubmitSolutionUI(this, cname).setVisible(true));
        }

        row.add(left, BorderLayout.CENTER);
        row.add(btn,  BorderLayout.EAST);
        return row;
    }

    private JLabel smallMeta(String text) {
        JLabel l = new JLabel(text); l.setFont(Theme.FONT_SMALL); l.setForeground(Theme.TEXT_MUTED);
        return l;
    }
}
