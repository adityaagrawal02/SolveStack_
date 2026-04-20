package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class LoginUI extends JFrame {

    private String selectedRole = "Company";
    private JButton[] roleBtns;

    public LoginUI() {
        setTitle("SolveStack — Sign In");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(Theme.WINDOW);
        setLocationRelativeTo(null);
        setResizable(false);
        setContentPane(buildContent());
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new GridLayout(1, 2));
        root.setBackground(Theme.BG_LIGHT);

        JPanel hero = buildHeroPanel();

        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Theme.BG_LIGHT);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.BG_WHITE);
        card.setBorder(new CompoundBorder(
            new CompoundBorder(
                new MatteBorder(0, 0, 2, 0, new Color(225, 228, 224)),
                new LineBorder(Theme.BORDER, 1, true)
            ),
            new EmptyBorder(30, 30, 30, 30)));
        card.setPreferredSize(new Dimension(380, 520));

        // Logo
        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoRow.setOpaque(false);
        logoRow.setAlignmentX(LEFT_ALIGNMENT);
        JPanel dot = new JPanel() {
            { setPreferredSize(new Dimension(12, 12)); setOpaque(false); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.PRIMARY);
                g2.fillOval(0, 2, 10, 10);
            }
        };
        JLabel logo = new JLabel("  SolveStack");
        logo.setFont(new Font("Trebuchet MS", Font.BOLD, 21));
        logo.setForeground(Theme.TEXT_PRIMARY);
        logoRow.add(dot);
        logoRow.add(logo);

        JLabel sub = new JLabel("Open innovation collaboration platform");
        sub.setFont(Theme.FONT_SMALL);
        sub.setForeground(Theme.TEXT_MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        // Role selection
        JLabel roleLabel = new JLabel("Sign in as");
        roleLabel.setFont(Theme.FONT_SMALL);
        roleLabel.setForeground(Theme.TEXT_MUTED);
        roleLabel.setAlignmentX(LEFT_ALIGNMENT);

        String[] roles = {"Company", "Developer", "Evaluator", "Admin"};
        roleBtns = new JButton[roles.length];
        JPanel roleGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        roleGrid.setOpaque(false);
        roleGrid.setAlignmentX(LEFT_ALIGNMENT);
        roleGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        for (int i = 0; i < roles.length; i++) {
            final String r = roles[i];
            JButton b = new JButton(r);
            b.setFont(Theme.FONT_SMALL);
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            styleRoleBtn(b, r.equals(selectedRole));
            b.addActionListener(e -> {
                selectedRole = r;
                for (int j = 0; j < roles.length; j++)
                    styleRoleBtn(roleBtns[j], roles[j].equals(r));
            });
            roleBtns[i] = b;
            roleGrid.add(b);
        }

        // Username
        JLabel userLabel = Components.formLabel("Username");
        userLabel.setAlignmentX(LEFT_ALIGNMENT);
        JTextField userField = Components.textField("e.g. acme_corp");
        userField.setAlignmentX(LEFT_ALIGNMENT);
        userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        // Password
        JLabel passLabel = Components.formLabel("Password");
        passLabel.setAlignmentX(LEFT_ALIGNMENT);
        JPasswordField passField = Components.passwordField();
        passField.setAlignmentX(LEFT_ALIGNMENT);
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        // Error message label
        JLabel errorMsg = new JLabel();
        errorMsg.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
        errorMsg.setForeground(new Color(211, 47, 47));
        errorMsg.setAlignmentX(LEFT_ALIGNMENT);
        errorMsg.setVisible(false);

        // Sign in button
        JButton signIn = Components.primaryBtn("Sign in");
        signIn.setAlignmentX(LEFT_ALIGNMENT);
        signIn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        signIn.addActionListener(e -> openDashboard(userField, passField, errorMsg));

        // Sign up link
        JPanel signupPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        signupPanel.setOpaque(false);
        signupPanel.setAlignmentX(LEFT_ALIGNMENT);
        JLabel noAccount = new JLabel("Don't have an account? ");
        noAccount.setFont(Theme.FONT_SMALL);
        noAccount.setForeground(Theme.TEXT_MUTED);
        JButton signupLink = new JButton("Create one");
        signupLink.setFont(Theme.FONT_SMALL);
        signupLink.setForeground(Theme.PRIMARY);
        signupLink.setBorderPainted(false);
        signupLink.setContentAreaFilled(false);
        signupLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signupLink.addActionListener(e -> {
            new SignupUI().setVisible(true);
            this.dispose();
        });
        signupPanel.add(noAccount);
        signupPanel.add(signupLink);

        // Hint
        JLabel hint = Components.notifBanner("Use demo credentials: alex_kumar / password123 (Developer), acme_corp / company123 (Company), admin_user / admin999 (Admin)");
        hint.setAlignmentX(LEFT_ALIGNMENT);

        card.add(logoRow);
        card.add(Box.createVerticalStrut(6));
        card.add(sub);
        card.add(Box.createVerticalStrut(22));
        card.add(roleLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(roleGrid);
        card.add(Box.createVerticalStrut(16));
        card.add(userLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(userField);
        card.add(Box.createVerticalStrut(12));
        card.add(passLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(passField);
        card.add(Box.createVerticalStrut(12));
        card.add(errorMsg);
        card.add(Box.createVerticalStrut(8));
        card.add(signIn);
        card.add(Box.createVerticalStrut(8));
        card.add(signupPanel);
        card.add(Box.createVerticalStrut(14));
        card.add(hint);

        right.add(card);
        root.add(hero);
        root.add(right);
        return root;
    }

    private JPanel buildHeroPanel() {
        JPanel hero = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(19, 91, 90),
                        getWidth(), getHeight(), new Color(34, 139, 136)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(255, 255, 255, 45));
                g2.fillOval(getWidth() - 220, 70, 180, 180);
                g2.setColor(new Color(255, 255, 255, 28));
                g2.fillOval(30, getHeight() - 230, 210, 210);
                g2.dispose();
            }
        };
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setBorder(new EmptyBorder(80, 56, 80, 56));

        JLabel title = new JLabel("Build solutions that matter.");
        title.setFont(new Font("Trebuchet MS", Font.BOLD, 33));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("<html>SolveStack connects companies and developers for real-world innovation challenges.</html>");
        subtitle.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
        subtitle.setForeground(new Color(235, 250, 248));
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        JPanel stats = new JPanel(new GridLayout(1, 3, 10, 0));
        stats.setOpaque(false);
        stats.setAlignmentX(LEFT_ALIGNMENT);
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));
        stats.add(heroMetric("120+", "Challenges"));
        stats.add(heroMetric("3.2k", "Developers"));
        stats.add(heroMetric("92%", "Resolution"));

        hero.add(title);
        hero.add(Box.createVerticalStrut(12));
        hero.add(subtitle);
        hero.add(Box.createVerticalStrut(34));
        hero.add(stats);
        hero.add(Box.createVerticalGlue());
        return hero;
    }

    private JPanel heroMetric(String value, String label) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new CompoundBorder(
                new LineBorder(new Color(255, 255, 255, 70), 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JLabel v = new JLabel(value);
        v.setFont(new Font("Trebuchet MS", Font.BOLD, 21));
        v.setForeground(Color.WHITE);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Trebuchet MS", Font.PLAIN, 11));
        l.setForeground(new Color(235, 250, 248));

        p.add(v);
        p.add(Box.createVerticalStrut(2));
        p.add(l);
        return p;
    }

    private void styleRoleBtn(JButton b, boolean selected) {
        if (selected) {
            b.setBackground(Theme.PRIMARY_LIGHT);
            b.setForeground(Theme.PRIMARY);
            b.setBorder(new CompoundBorder(
                    new LineBorder(new Color(170, 218, 216), 1, true),
                    new EmptyBorder(6, 10, 6, 10)));
        } else {
            b.setBackground(Theme.BG_WHITE);
            b.setForeground(Theme.TEXT_MUTED);
            b.setBorder(new CompoundBorder(
                    new LineBorder(Theme.BORDER, 1, true),
                    new EmptyBorder(6, 10, 6, 10)));
        }
        b.setContentAreaFilled(false);
        b.setOpaque(true);
    }

    private void openDashboard(JTextField userField, JPasswordField passField, JLabel errorMsg) {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword());

        if (username.isBlank() || password.isBlank()) {
            errorMsg.setText("⚠ Please enter username and password");
            errorMsg.setVisible(true);
            return;
        }

        // Authenticate user
        UserRepository repo = UserRepository.getInstance();
        models.User user = repo.authenticate(username, password);

        if (user == null) {
            errorMsg.setText("⚠ Invalid username or password");
            errorMsg.setVisible(true);
            passField.setText("");
            return;
        }

        // Store user session
        String userRole = user.getRole();
        UserSession.getInstance().setCurrentUser(user, userRole);

        // Route to appropriate dashboard based on actual user role
        JFrame dash = switch (userRole) {
            case "Developer" -> new DeveloperDashboardUI();
            case "Evaluator" -> new EvaluatorDashboardUI();
            case "Admin"     -> new AdminDashboardUI();
            case "Company"   -> new CompanyDashboardUI();
            default          -> new CompanyDashboardUI();
        };
        dash.setVisible(true);
        this.dispose();
    }
}
