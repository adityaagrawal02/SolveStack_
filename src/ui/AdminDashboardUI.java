package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class AdminDashboardUI extends JFrame {

    public AdminDashboardUI() {
        setTitle("SolveStack — Admin Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(ui.Theme.WINDOW);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_LIGHT);
        root.add(Components.navbar("Admin", e -> returnToLogin()), BorderLayout.NORTH);
        root.add(buildBody(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Theme.BG_LIGHT);
        body.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Admin dashboard");
        title.setFont(ui.Theme.FONT_TITLE); title.setForeground(Theme.TEXT_PRIMARY);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = new JLabel("Manage users, approve challenges, and monitor platform activity");
        sub.setFont(Theme.FONT_SMALL); sub.setForeground(Theme.TEXT_MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        JPanel metrics = new JPanel(new GridLayout(1, 4, 12, 0));
        metrics.setOpaque(false);
        metrics.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        metrics.add(Components.metric("Total users",         "148"));
        metrics.add(Components.metric("Pending approvals",   "5"));
        metrics.add(Components.metric("Active challenges",   "11"));
        metrics.add(Components.metric("Platform submissions","203"));

        // Three-column cards
        JPanel threeCol = new JPanel(new GridLayout(1, 3, 14, 0));
        threeCol.setOpaque(false);
        threeCol.setMaximumSize(new Dimension(Integer.MAX_VALUE, 999));
        threeCol.add(buildVerificationCard());
        threeCol.add(buildApprovalCard());
        threeCol.add(buildActivityCard());

        body.add(title);
        body.add(Box.createVerticalStrut(4));
        body.add(sub);
        body.add(Box.createVerticalStrut(16));
        body.add(metrics);
        body.add(Box.createVerticalStrut(16));
        body.add(threeCol);
        body.add(Box.createVerticalGlue());
        return body;
    }

    private JPanel buildVerificationCard() {
        JPanel card = Components.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel h = Components.sectionHeader("Pending verifications");
        h.setBorder(new EmptyBorder(0, 0, 10, 0));
        card.add(h);
        card.add(Components.sep());
        card.add(Box.createVerticalStrut(8));
        card.add(verifyRow("NV", "NovaTech Solutions", "Company",   new Color(206, 203, 246), new Color(60,52,137)));
        card.add(Box.createVerticalStrut(6));
        card.add(Components.sep());
        card.add(Box.createVerticalStrut(6));
        card.add(verifyRow("AR", "Ananya R.",          "Developer", new Color(245, 196, 179), new Color(113,43,19)));
        return card;
    }

    private JPanel verifyRow(String initials, String name, String role, Color avBg, Color avFg) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.add(Components.avatar(initials, avBg, avFg), BorderLayout.WEST);
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel n = new JLabel(name); n.setFont(Theme.FONT_SMALL); n.setForeground(Theme.TEXT_PRIMARY);
        JLabel r = new JLabel(role); r.setFont(Theme.FONT_SMALL); r.setForeground(Theme.TEXT_MUTED);
        info.add(n); info.add(r);
        JButton btn = Components.smallBtn("Verify");
        btn.addActionListener(e -> JOptionPane.showMessageDialog(this, name + " verified successfully.", "Verified", JOptionPane.INFORMATION_MESSAGE));
        row.add(info, BorderLayout.CENTER);
        row.add(btn, BorderLayout.EAST);
        return row;
    }

    private JPanel buildApprovalCard() {
        JPanel card = Components.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel h = Components.sectionHeader("Awaiting approval");
        h.setBorder(new EmptyBorder(0, 0, 10, 0));
        card.add(h);
        card.add(Components.sep());
        card.add(Box.createVerticalStrut(8));
        card.add(approvalRow("Blockchain logistics tracker", "Acme Corp",  "₹4,00,000"));
        card.add(Box.createVerticalStrut(8));
        card.add(Components.sep());
        card.add(Box.createVerticalStrut(8));
        card.add(approvalRow("AI crop disease detection",   "AgroTech",   "₹2,50,000"));
        return card;
    }

    private JPanel approvalRow(String name, String company, String prize) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        JLabel n = new JLabel(name); n.setFont(new Font("Segoe UI", Font.BOLD, 12)); n.setForeground(Theme.TEXT_PRIMARY);
        JLabel m = new JLabel(company + " · " + prize); m.setFont(Theme.FONT_SMALL); m.setForeground(Theme.TEXT_MUTED);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        btns.setOpaque(false);
        JButton approve = Components.smallBtn("Approve");
        approve.setBackground(Theme.GREEN_BG);
        approve.setForeground(Theme.GREEN_TEXT);
        approve.addActionListener(e -> JOptionPane.showMessageDialog(this, "'" + name + "' approved.", "Approved", JOptionPane.INFORMATION_MESSAGE));
        JButton reject = Components.smallBtn("Reject");
        reject.setBackground(new Color(252, 235, 235));
        reject.setForeground(new Color(163, 45, 45));
        reject.addActionListener(e -> JOptionPane.showMessageDialog(this, "'" + name + "' rejected.", "Rejected", JOptionPane.WARNING_MESSAGE));
        btns.add(approve); btns.add(reject);
        row.add(n); row.add(m); row.add(btns);
        return row;
    }

    private JPanel buildActivityCard() {
        JPanel card = Components.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel h = Components.sectionHeader("Platform activity");
        h.setBorder(new EmptyBorder(0, 0, 10, 0));
        card.add(h);
        card.add(Components.sep());
        card.add(Box.createVerticalStrut(8));
        String[][] rows = {
            {"New registrations (today)", "12"},
            {"Submissions (this week)",   "47"},
            {"Evaluations (this week)",   "31"},
            {"Challenges closed (month)", "2"},
        };
        for (String[] r : rows) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setBorder(new EmptyBorder(4, 0, 4, 0));
            JLabel lbl = new JLabel(r[0]); lbl.setFont(Theme.FONT_SMALL); lbl.setForeground(Theme.TEXT_MUTED);
            JLabel val = new JLabel(r[1]); val.setFont(new Font("Segoe UI", Font.BOLD, 12)); val.setForeground(Theme.TEXT_PRIMARY);
            row.add(lbl, BorderLayout.WEST);
            row.add(val, BorderLayout.EAST);
            card.add(row);
        }
        card.add(Box.createVerticalStrut(10));
        JButton report = Components.outlineBtn("Generate report");
        report.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        report.addActionListener(e -> JOptionPane.showMessageDialog(this, "Report generation would connect to your ReportService.", "Generate Report", JOptionPane.INFORMATION_MESSAGE));
        card.add(report);
        return card;
    }

    private void returnToLogin() {
        new LoginUI().setVisible(true);
        this.dispose();
    }
}
