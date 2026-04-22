package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class Components {

    // ── Pill badge ────────────────────────────────────────────────────────────
    public static JLabel badge(String text, Color bg, Color fg) {
        JLabel l = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(fg);
        l.setOpaque(false);
        l.setBorder(new EmptyBorder(4, 10, 4, 10));
        return l;
    }

    public static JLabel roleBadge(String role) {
        return switch (role) {
            case "Developer" -> badge(role, Theme.GREEN_BG,   Theme.GREEN_TEXT);
            case "Evaluator" -> badge(role, Theme.AMBER_BG,   Theme.AMBER_TEXT);
            case "Admin"     -> badge(role, Theme.CORAL_BG,   Theme.CORAL_TEXT);
            default          -> badge(role, Theme.PRIMARY_LIGHT, Theme.PRIMARY);
        };
    }

    // ── Avatar circle ─────────────────────────────────────────────────────────
    public static JPanel avatar(String initials, Color bg, Color fg) {
        return new JPanel() {
            { setPreferredSize(new Dimension(36, 36)); setOpaque(false); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillOval(0, 0, 35, 35);
                g2.setColor(fg);
                g2.setFont(new Font("Trebuchet MS", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                int x = (35 - fm.stringWidth(initials)) / 2;
                int y = (35 + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(initials, x, y);
            }
        };
    }

    // ── Primary / secondary buttons ───────────────────────────────────────────
    public static JButton primaryBtn(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? Theme.PRIMARY_DARK
                          : getModel().isRollover() ? new Color(18, 96, 95) : Theme.PRIMARY);
                g2.fill(new RoundRectangle2D.Float(0, 1, getWidth(), getHeight() - 1, 10, 10));
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        b.setFont(Theme.FONT_BODY);
        b.setForeground(Color.WHITE);
        b.setPreferredSize(new Dimension(140, 34));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JButton outlineBtn(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? Theme.PRIMARY_LIGHT : Theme.BG_WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.setColor(Theme.BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.setColor(Theme.TEXT_PRIMARY);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        b.setFont(Theme.FONT_BODY);
        b.setPreferredSize(new Dimension(120, 34));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JButton smallBtn(String text) {
        JButton b = outlineBtn(text);
        b.setFont(Theme.FONT_SMALL);
        b.setPreferredSize(new Dimension(90, 28));
        return b;
    }

    // ── Card panel ────────────────────────────────────────────────────────────
    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(Theme.BG_WHITE);
        p.setBorder(new CompoundBorder(
            new CompoundBorder(
                new MatteBorder(0, 0, 2, 0, new Color(225, 228, 224)),
                new LineBorder(Theme.BORDER, 1, true)
            ),
                new EmptyBorder(14, 16, 14, 16)));
        return p;
    }

    // ── Top nav bar ───────────────────────────────────────────────────────────
    public static JPanel navbar(String role, ActionListener onLogout) {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(Theme.BG_WHITE);
        nav.setBorder(new MatteBorder(0, 0, 1, 0, Theme.BORDER));
        nav.setPreferredSize(new Dimension(0, 56));

        // brand
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        brand.setOpaque(false);
        brand.setBorder(new EmptyBorder(0, 16, 0, 0));
        JPanel dot = new JPanel() {
            { setPreferredSize(new Dimension(10, 10)); setOpaque(false); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.PRIMARY_DARK);
                g2.fillOval(1, 3, 7, 7);
                g2.setColor(Theme.PRIMARY);
                g2.fillOval(4, 0, 7, 7);
            }
        };
        JLabel title = new JLabel("  SolveStack");
        title.setFont(Theme.FONT_HEAD);
        title.setForeground(Theme.TEXT_PRIMARY);
        brand.add(dot);
        brand.add(title);

        // right side
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        right.setOpaque(false);
        right.add(roleBadge(role));

        String[] initials = {"Company", "Developer", "Evaluator", "Admin"};
        String[][] avatarData = {{"AC", "#CFE9E7", "#0D5E5D"}, {"DV", "#BDE7D6", "#0D5E5D"},
                      {"EV", "#FADAA4", "#8A520E"}, {"AD", "#F6D0C3", "#8D4425"}};
        int idx = java.util.Arrays.asList(initials).indexOf(role);
        if (idx < 0) idx = 0;
        Color avBg  = Color.decode(avatarData[idx][1]);
        Color avFg  = Color.decode(avatarData[idx][2]);
        right.add(avatar(avatarData[idx][0], avBg, avFg));

        JButton logout = smallBtn("Sign out");
        logout.addActionListener(onLogout);
        right.add(logout);

        nav.add(brand, BorderLayout.WEST);
        nav.add(right,  BorderLayout.EAST);
        return nav;
    }

    // ── Metric card ───────────────────────────────────────────────────────────
    public static JPanel metric(String label, String value) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(Theme.BG_SECONDARY);
        p.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(Theme.TEXT_MUTED);

        JLabel val = new JLabel(value);
        val.setFont(Theme.FONT_METRIC);
        val.setForeground(Theme.TEXT_PRIMARY);

        p.add(lbl, BorderLayout.NORTH);
        p.add(val, BorderLayout.CENTER);
        return p;
    }

    // ── Section header ────────────────────────────────────────────────────────
    public static JLabel sectionHeader(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_HEAD);
        l.setForeground(Theme.TEXT_PRIMARY);
        return l;
    }

    // ── Horizontal separator ─────────────────────────────────────────────────
    public static JSeparator sep() {
        JSeparator s = new JSeparator();
        s.setForeground(Theme.BORDER);
        return s;
    }

    // ── Styled text field ─────────────────────────────────────────────────────
    public static JTextField textField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(Theme.FONT_BODY);
        f.setBorder(new CompoundBorder(
                new LineBorder(Theme.BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        f.setForeground(Theme.TEXT_MUTED);
        f.setText(placeholder);
        f.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(placeholder)) { f.setText(""); f.setForeground(Theme.TEXT_PRIMARY); }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) { f.setText(placeholder); f.setForeground(Theme.TEXT_MUTED); }
            }
        });
        return f;
    }

    public static JPasswordField passwordField() {
        JPasswordField f = new JPasswordField();
        f.setFont(Theme.FONT_BODY);
        f.setBorder(new CompoundBorder(
                new LineBorder(Theme.BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        return f;
    }

    public static JTextArea textArea(String placeholder) {
        JTextArea a = new JTextArea(4, 20);
        a.setFont(Theme.FONT_BODY);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setBorder(new EmptyBorder(6, 10, 6, 10));
        a.setForeground(Theme.TEXT_MUTED);
        a.setText(placeholder);
        a.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (a.getText().equals(placeholder)) { a.setText(""); a.setForeground(Theme.TEXT_PRIMARY); }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (a.getText().isEmpty()) { a.setText(placeholder); a.setForeground(Theme.TEXT_MUTED); }
            }
        });
        return a;
    }

    // ── Form label ────────────────────────────────────────────────────────────
    public static JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(Theme.TEXT_MUTED);
        return l;
    }

    // ── Notification banner ───────────────────────────────────────────────────
    public static JLabel notifBanner(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_BODY);
        l.setForeground(Theme.PRIMARY_DARK);
        l.setBackground(Theme.PRIMARY_LIGHT);
        l.setOpaque(true);
        l.setBorder(new CompoundBorder(
            new LineBorder(new Color(170, 218, 216), 1, true),
                new EmptyBorder(10, 14, 10, 14)));
        return l;
    }

    // ── Scroll pane ───────────────────────────────────────────────────────────
    public static JScrollPane scroll(JComponent c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUnitIncrement(12);
        return sp;
    }
}
