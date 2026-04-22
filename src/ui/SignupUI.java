package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class SignupUI extends JFrame {

    private String selectedRole = "Developer";
    private JButton[] roleBtns;
    private JPanel dynamicFieldsPanel;

    public SignupUI() {
        setTitle("SolveStack — Create Account");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(new Dimension(450, 700));
        setLocationRelativeTo(null);
        setResizable(false);
        setContentPane(buildContent());
    }

    private JPanel buildContent() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(Theme.BG_LIGHT);
        root.setBorder(new EmptyBorder(30, 30, 30, 30));

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

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Join SolveStack and start collaborating");
        sub.setFont(Theme.FONT_SMALL);
        sub.setForeground(Theme.TEXT_MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        // Role selection
        JLabel roleLabel = new JLabel("Select your role");
        roleLabel.setFont(Theme.FONT_SMALL);
        roleLabel.setForeground(Theme.TEXT_MUTED);
        roleLabel.setAlignmentX(LEFT_ALIGNMENT);

        String[] roles = {"Developer", "Company", "Evaluator"};
        roleBtns = new JButton[roles.length];
        JPanel roleGrid = new JPanel(new GridLayout(1, 3, 8, 0));
        roleGrid.setOpaque(false);
        roleGrid.setAlignmentX(LEFT_ALIGNMENT);
        roleGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

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
                updateDynamicFields();
            });
            roleBtns[i] = b;
            roleGrid.add(b);
        }

        // Common fields
        JLabel userLabel = Components.formLabel("Username");
        userLabel.setAlignmentX(LEFT_ALIGNMENT);
        JTextField userField = Components.textField("e.g. john_dev");
        userField.setAlignmentX(LEFT_ALIGNMENT);
        userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JLabel emailLabel = Components.formLabel("Email");
        emailLabel.setAlignmentX(LEFT_ALIGNMENT);
        JTextField emailField = Components.textField("e.g. john@example.com");
        emailField.setAlignmentX(LEFT_ALIGNMENT);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JLabel passLabel = Components.formLabel("Password");
        passLabel.setAlignmentX(LEFT_ALIGNMENT);
        JPasswordField passField = Components.passwordField();
        passField.setAlignmentX(LEFT_ALIGNMENT);
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        // Dynamic fields panel (changes based on role)
        dynamicFieldsPanel = new JPanel();
        dynamicFieldsPanel.setLayout(new BoxLayout(dynamicFieldsPanel, BoxLayout.Y_AXIS));
        dynamicFieldsPanel.setOpaque(false);
        updateDynamicFields();

        // Error message
        JLabel errorMsg = new JLabel();
        errorMsg.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
        errorMsg.setForeground(new Color(211, 47, 47));
        errorMsg.setAlignmentX(LEFT_ALIGNMENT);
        errorMsg.setVisible(false);

        // Sign up button
        JButton signUp = Components.primaryBtn("Create Account");
        signUp.setAlignmentX(LEFT_ALIGNMENT);
        signUp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        signUp.addActionListener(e -> registerUser(userField, emailField, passField, dynamicFieldsPanel, errorMsg));

        // Back to login
        JButton backBtn = Components.outlineBtn("Back to Login");
        backBtn.setAlignmentX(LEFT_ALIGNMENT);
        backBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        backBtn.addActionListener(e -> {
            new LoginUI().setVisible(true);
            this.dispose();
        });

        root.add(logoRow);
        root.add(Box.createVerticalStrut(4));
        root.add(title);
        root.add(Box.createVerticalStrut(4));
        root.add(sub);
        root.add(Box.createVerticalStrut(20));
        root.add(roleLabel);
        root.add(Box.createVerticalStrut(6));
        root.add(roleGrid);
        root.add(Box.createVerticalStrut(16));
        root.add(userLabel);
        root.add(Box.createVerticalStrut(4));
        root.add(userField);
        root.add(Box.createVerticalStrut(10));
        root.add(emailLabel);
        root.add(Box.createVerticalStrut(4));
        root.add(emailField);
        root.add(Box.createVerticalStrut(10));
        root.add(dynamicFieldsPanel);
        root.add(Box.createVerticalStrut(10));
        root.add(passLabel);
        root.add(Box.createVerticalStrut(4));
        root.add(passField);
        root.add(Box.createVerticalStrut(12));
        root.add(errorMsg);
        root.add(Box.createVerticalStrut(12));
        root.add(signUp);
        root.add(Box.createVerticalStrut(8));
        root.add(backBtn);
        root.add(Box.createVerticalGlue());

        JPanel wrapper = new JPanel();
        wrapper.setBackground(Theme.BG_LIGHT);
        wrapper.add(root);
        return wrapper;
    }

    private void updateDynamicFields() {
        dynamicFieldsPanel.removeAll();
        dynamicFieldsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        if ("Developer".equals(selectedRole)) {
            JLabel skillLabel = Components.formLabel("Skills (comma-separated)");
            skillLabel.setAlignmentX(LEFT_ALIGNMENT);
            JTextField skillField = Components.textField("e.g. Java, Python, ML");
            skillField.setAlignmentX(LEFT_ALIGNMENT);
            skillField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            skillField.setName("skillSet");
            dynamicFieldsPanel.add(skillLabel);
            dynamicFieldsPanel.add(Box.createVerticalStrut(4));
            dynamicFieldsPanel.add(skillField);
        } else if ("Company".equals(selectedRole)) {
            JLabel companyLabel = Components.formLabel("Company Name");
            companyLabel.setAlignmentX(LEFT_ALIGNMENT);
            JTextField companyField = Components.textField("e.g. Acme Corp");
            companyField.setAlignmentX(LEFT_ALIGNMENT);
            companyField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            companyField.setName("companyName");

            JLabel industryLabel = Components.formLabel("Industry");
            industryLabel.setAlignmentX(LEFT_ALIGNMENT);
            JTextField industryField = Components.textField("e.g. FinTech");
            industryField.setAlignmentX(LEFT_ALIGNMENT);
            industryField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            industryField.setName("industry");

            dynamicFieldsPanel.add(companyLabel);
            dynamicFieldsPanel.add(Box.createVerticalStrut(4));
            dynamicFieldsPanel.add(companyField);
            dynamicFieldsPanel.add(Box.createVerticalStrut(10));
            dynamicFieldsPanel.add(industryLabel);
            dynamicFieldsPanel.add(Box.createVerticalStrut(4));
            dynamicFieldsPanel.add(industryField);
        } else if ("Evaluator".equals(selectedRole)) {
            JLabel expertiseLabel = Components.formLabel("Area of Expertise");
            expertiseLabel.setAlignmentX(LEFT_ALIGNMENT);
            JTextField expertiseField = Components.textField("e.g. AI/ML, Cloud Architecture");
            expertiseField.setAlignmentX(LEFT_ALIGNMENT);
            expertiseField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            expertiseField.setName("expertise");
            dynamicFieldsPanel.add(expertiseLabel);
            dynamicFieldsPanel.add(Box.createVerticalStrut(4));
            dynamicFieldsPanel.add(expertiseField);
        }
        dynamicFieldsPanel.revalidate();
        dynamicFieldsPanel.repaint();
    }

    private void registerUser(JTextField userField, JTextField emailField, JPasswordField passField, JPanel fieldsPanel, JLabel errorMsg) {
        String username = userField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passField.getPassword());

        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            errorMsg.setText("⚠ Please fill in all fields");
            errorMsg.setVisible(true);
            return;
        }

        if (!email.contains("@")) {
            errorMsg.setText("⚠ Please enter a valid email");
            errorMsg.setVisible(true);
            return;
        }

        // Get dynamic field values
        String dynamicValue1 = "";
        String dynamicValue2 = "";
        int fieldCount = 0;
        
        for (Component c : fieldsPanel.getComponents()) {
            if (c instanceof JTextField) {
                String val = ((JTextField) c).getText().trim();
                if (!val.isEmpty()) {
                    if (fieldCount == 0) {
                        dynamicValue1 = val;
                    } else if (fieldCount == 1) {
                        dynamicValue2 = val;
                    }
                    fieldCount++;
                }
            }
        }

        if (dynamicValue1.isEmpty()) {
            errorMsg.setText("⚠ Please fill in all required fields");
            errorMsg.setVisible(true);
            return;
        }

        // Register user
        UserRepository repo = UserRepository.getInstance();
        boolean success;
        
        if ("Company".equals(selectedRole)) {
            success = repo.registerCompany(username, email, password, dynamicValue1, dynamicValue2);
        } else {
            success = repo.registerUser(username, email, password, selectedRole, dynamicValue1);
        }

        if (!success) {
            errorMsg.setText("⚠ Username already exists. Please choose another.");
            errorMsg.setVisible(true);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Account created successfully! You can now login.",
                "Welcome to SolveStack",
                JOptionPane.INFORMATION_MESSAGE);
        new LoginUI().setVisible(true);
        this.dispose();
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
}
