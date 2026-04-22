package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class CreateChallengeUI extends JFrame {

    public CreateChallengeUI(JFrame parent) {
        setTitle("SolveStack — Create Challenge");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(560, 600);
        setLocationRelativeTo(parent);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_LIGHT);

        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(Theme.BG_WHITE);
        nav.setBorder(new CompoundBorder(new MatteBorder(0,0,1,0,Theme.BORDER), new EmptyBorder(10,16,10,16)));
        JLabel brandLbl = new JLabel("SolveStack — Create Challenge");
        brandLbl.setFont(Theme.FONT_HEAD); brandLbl.setForeground(Theme.TEXT_PRIMARY);
        JButton back = Components.smallBtn("← Back");
        back.addActionListener(e -> dispose());
        nav.add(brandLbl, BorderLayout.WEST);
        nav.add(back, BorderLayout.EAST);

        root.add(nav, BorderLayout.NORTH);
        root.add(buildForm(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JScrollPane buildForm() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Theme.BG_LIGHT);
        body.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Create challenge");
        title.setFont(Theme.FONT_TITLE); title.setForeground(Theme.TEXT_PRIMARY);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = new JLabel("Post a new innovation challenge for developers");
        sub.setFont(Theme.FONT_SMALL); sub.setForeground(Theme.TEXT_MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        JPanel card = Components.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(LEFT_ALIGNMENT);

        JTextField titleField = Components.textField("e.g. AI-powered logistics optimizer");
        JTextArea  descField  = Components.textArea("Describe the problem you want solved...");
        JTextField prizeField = Components.textField("e.g. 500000");
        JTextField dateField  = Components.textField("DD/MM/YYYY");
        JTextArea  criteriaField = Components.textArea("What criteria will submissions be judged on?");

        String[] categories = {"Technology", "Sustainability", "Healthcare", "Logistics", "Finance"};
        JComboBox<String> categoryBox = new JComboBox<>(categories);
        categoryBox.setFont(Theme.FONT_BODY);
        categoryBox.setBorder(new LineBorder(Theme.BORDER, 1, true));

        JScrollPane descScroll = new JScrollPane(descField);
        descScroll.setBorder(new LineBorder(Theme.BORDER, 1, true));
        JScrollPane critScroll = new JScrollPane(criteriaField);
        critScroll.setBorder(new LineBorder(Theme.BORDER, 1, true));

        addField(card, "Challenge title", titleField);
        addScrollField(card, "Description", descScroll);
        addField(card, "Category", categoryBox);

        JPanel prizeRow = new JPanel(new GridLayout(1, 2, 12, 0));
        prizeRow.setOpaque(false);
        prizeRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        prizeRow.setAlignmentX(LEFT_ALIGNMENT);
        JPanel prizePanel = new JPanel(); prizePanel.setOpaque(false);
        prizePanel.setLayout(new BoxLayout(prizePanel, BoxLayout.Y_AXIS));
        prizePanel.add(Components.formLabel("Prize amount (₹)")); prizePanel.add(Box.createVerticalStrut(4)); prizePanel.add(prizeField);
        JPanel datePanel = new JPanel(); datePanel.setOpaque(false);
        datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.Y_AXIS));
        datePanel.add(Components.formLabel("Submission deadline")); datePanel.add(Box.createVerticalStrut(4)); datePanel.add(dateField);
        prizeRow.add(prizePanel); prizeRow.add(datePanel);
        card.add(prizeRow);
        card.add(Box.createVerticalStrut(12));

        addScrollField(card, "Evaluation criteria", critScroll);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        JButton post = Components.primaryBtn("Post challenge");
        JButton cancel = Components.outlineBtn("Cancel");
        cancel.addActionListener(e -> dispose());
        JLabel notif = new JLabel();
        notif.setFont(Theme.FONT_SMALL);
        notif.setForeground(Theme.PRIMARY);

        post.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Challenge posted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });
        btnRow.add(post); btnRow.add(cancel);
        card.add(btnRow);

        body.add(title);
        body.add(Box.createVerticalStrut(4));
        body.add(sub);
        body.add(Box.createVerticalStrut(16));
        body.add(card);
        return Components.scroll(body);
    }

    private void addField(JPanel card, String label, JComponent field) {
        JLabel lbl = Components.formLabel(label);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        field.setAlignmentX(LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        card.add(lbl);
        card.add(Box.createVerticalStrut(4));
        card.add(field);
        card.add(Box.createVerticalStrut(12));
    }

    private void addScrollField(JPanel card, String label, JScrollPane sp) {
        JLabel lbl = Components.formLabel(label);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        sp.setAlignmentX(LEFT_ALIGNMENT);
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        card.add(lbl);
        card.add(Box.createVerticalStrut(4));
        card.add(sp);
        card.add(Box.createVerticalStrut(12));
    }
}
