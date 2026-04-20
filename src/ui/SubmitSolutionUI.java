package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class SubmitSolutionUI extends JFrame {

    public SubmitSolutionUI(JFrame parent, String challengeName) {
        setTitle("SolveStack — Submit Solution");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(560, 580);
        setLocationRelativeTo(parent);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_LIGHT);

        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(Theme.BG_WHITE);
        nav.setBorder(new CompoundBorder(new MatteBorder(0,0,1,0,Theme.BORDER), new EmptyBorder(10,16,10,16)));
        JLabel brandLbl = new JLabel("SolveStack — Submit Solution");
        brandLbl.setFont(Theme.FONT_HEAD); brandLbl.setForeground(Theme.TEXT_PRIMARY);
        JButton back = Components.smallBtn("← Back");
        back.addActionListener(e -> dispose());
        nav.add(brandLbl, BorderLayout.WEST);
        nav.add(back, BorderLayout.EAST);

        root.add(nav, BorderLayout.NORTH);
        root.add(buildForm(challengeName), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JScrollPane buildForm(String challengeName) {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Theme.BG_LIGHT);
        body.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Submit solution");
        title.setFont(Theme.FONT_TITLE); title.setForeground(Theme.TEXT_PRIMARY);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel(challengeName + " · Acme Corp");
        sub.setFont(Theme.FONT_SMALL); sub.setForeground(Theme.TEXT_MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        JPanel card = Components.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(LEFT_ALIGNMENT);

        JTextField solTitle  = Components.textField("Give your solution a name");
        JTextArea  solDesc   = Components.textArea("Explain your approach and how it solves the problem...");
        JTextField githubUrl = Components.textField("https://github.com/username/repo");
        JTextField techStack = Components.textField("e.g. Python, TensorFlow, FastAPI");

        JScrollPane descScroll = new JScrollPane(solDesc);
        descScroll.setBorder(new LineBorder(Theme.BORDER, 1, true));

        addField(card, "Solution title", solTitle);
        addScrollField(card, "Description", descScroll);
        addField(card, "GitHub repository URL", githubUrl);
        addField(card, "Tech stack", techStack);

        // Upload area
        JLabel uploadLabel = Components.formLabel("Attach document");
        uploadLabel.setAlignmentX(LEFT_ALIGNMENT);
        JPanel uploadArea = new JPanel(new BorderLayout());
        uploadArea.setBackground(Theme.BG_WHITE);
        uploadArea.setBorder(new CompoundBorder(
                new LineBorder(Theme.BORDER, 1, true),
                new EmptyBorder(18, 16, 18, 16)));
        uploadArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        uploadArea.setAlignmentX(LEFT_ALIGNMENT);
        JLabel uploadHint = new JLabel("Drop PDF or click to upload", SwingConstants.CENTER);
        uploadHint.setFont(Theme.FONT_SMALL);
        uploadHint.setForeground(Theme.TEXT_MUTED);
        uploadArea.add(uploadHint, BorderLayout.CENTER);
        uploadArea.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        uploadArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JFileChooser fc = new JFileChooser();
                int res = fc.showOpenDialog(SubmitSolutionUI.this);
                if (res == JFileChooser.APPROVE_OPTION)
                    uploadHint.setText("Selected: " + fc.getSelectedFile().getName());
            }
        });

        card.add(uploadLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(uploadArea);
        card.add(Box.createVerticalStrut(16));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        JButton submit = Components.primaryBtn("Submit solution");
        JButton cancel = Components.outlineBtn("Cancel");
        cancel.addActionListener(e -> dispose());
        submit.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Solution submitted successfully!", "Submitted", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });
        btnRow.add(submit); btnRow.add(cancel);
        card.add(btnRow);

        body.add(title);
        body.add(Box.createVerticalStrut(4));
        body.add(sub);
        body.add(Box.createVerticalStrut(16));
        body.add(card);
        return Components.scroll(body);
    }

    private void addField(JPanel card, String label, JComponent field) {
        JLabel lbl = Components.formLabel(label); lbl.setAlignmentX(LEFT_ALIGNMENT);
        field.setAlignmentX(LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        card.add(lbl); card.add(Box.createVerticalStrut(4)); card.add(field); card.add(Box.createVerticalStrut(12));
    }

    private void addScrollField(JPanel card, String label, JScrollPane sp) {
        JLabel lbl = Components.formLabel(label); lbl.setAlignmentX(LEFT_ALIGNMENT);
        sp.setAlignmentX(LEFT_ALIGNMENT); sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        card.add(lbl); card.add(Box.createVerticalStrut(4)); card.add(sp); card.add(Box.createVerticalStrut(12));
    }
}
