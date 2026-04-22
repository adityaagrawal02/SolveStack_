package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class EvaluationUI extends JFrame {

    private JLabel s1Val, s2Val, s3Val, totalVal;
    private JSlider s1, s2, s3;

    public EvaluationUI(JFrame parent, String developerName, String solutionName) {
        setTitle("SolveStack — Evaluate");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(560, 640);
        setLocationRelativeTo(parent);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_LIGHT);

        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(Theme.BG_WHITE);
        nav.setBorder(new CompoundBorder(new MatteBorder(0,0,1,0,Theme.BORDER), new EmptyBorder(10,16,10,16)));
        JLabel brandLbl = new JLabel("SolveStack — Evaluate Submission");
        brandLbl.setFont(Theme.FONT_HEAD); brandLbl.setForeground(Theme.TEXT_PRIMARY);
        JButton back = Components.smallBtn("← Back");
        back.addActionListener(e -> dispose());
        nav.add(brandLbl, BorderLayout.WEST);
        nav.add(back, BorderLayout.EAST);

        root.add(nav, BorderLayout.NORTH);
        root.add(buildBody(developerName, solutionName), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JScrollPane buildBody(String developerName, String solutionName) {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Theme.BG_LIGHT);
        body.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Evaluate submission");
        title.setFont(Theme.FONT_TITLE); title.setForeground(Theme.TEXT_PRIMARY);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = new JLabel(developerName + " — " + solutionName);
        sub.setFont(Theme.FONT_SMALL); sub.setForeground(Theme.TEXT_MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        // Submission info card
        JPanel infoCard = Components.card();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setAlignmentX(LEFT_ALIGNMENT);

        JPanel devRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        devRow.setOpaque(false);
        devRow.add(Components.avatar("RK", new Color(250, 199, 117), new Color(99, 56, 6)));
        JPanel devInfo = new JPanel();
        devInfo.setOpaque(false);
        devInfo.setLayout(new BoxLayout(devInfo, BoxLayout.Y_AXIS));
        JLabel dn = new JLabel(developerName); dn.setFont(Theme.FONT_BODY); dn.setForeground(Theme.TEXT_PRIMARY);
        JLabel ds = new JLabel("AI-powered supply chain optimizer"); ds.setFont(Theme.FONT_SMALL); ds.setForeground(Theme.TEXT_MUTED);
        devInfo.add(dn); devInfo.add(ds);
        devRow.add(devInfo);

        JTextArea desc = new JTextArea("Proposed a graph neural network model to optimize supply chain routing. Uses real-time sensor data with a 40% reduction in logistics cost demonstrated on test dataset.");
        desc.setFont(Theme.FONT_SMALL);
        desc.setForeground(Theme.TEXT_MUTED);
        desc.setEditable(false);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setBackground(Theme.BG_WHITE);
        desc.setBorder(new EmptyBorder(8, 0, 8, 0));

        JLabel link = new JLabel("<html><a href='#' style='color:#534AB7'>github.com/rahulk/supply-ai</a></html>");
        link.setFont(Theme.FONT_SMALL);

        infoCard.add(devRow);
        infoCard.add(Box.createVerticalStrut(10));
        infoCard.add(Components.sep());
        infoCard.add(desc);
        infoCard.add(link);

        // Scoring card
        JPanel scoreCard = Components.card();
        scoreCard.setLayout(new BoxLayout(scoreCard, BoxLayout.Y_AXIS));
        scoreCard.setAlignmentX(LEFT_ALIGNMENT);

        JLabel scoreHeader = Components.sectionHeader("Scoring");
        scoreHeader.setBorder(new EmptyBorder(0, 0, 12, 0));
        scoreCard.add(scoreHeader);

        s1 = slider(75); s2 = slider(80); s3 = slider(70);
        s1Val = scoreLabel("75"); s2Val = scoreLabel("80"); s3Val = scoreLabel("70");
        totalVal = new JLabel("75"); totalVal.setFont(new Font("Segoe UI", Font.BOLD, 20)); totalVal.setForeground(Theme.PRIMARY);

        ChangeListener listener = e -> updateTotal();
        s1.addChangeListener(listener); s2.addChangeListener(listener); s3.addChangeListener(listener);

        scoreCard.add(sliderRow("Innovation",        s1, s1Val));
        scoreCard.add(Box.createVerticalStrut(12));
        scoreCard.add(sliderRow("Technical quality", s2, s2Val));
        scoreCard.add(Box.createVerticalStrut(12));
        scoreCard.add(sliderRow("Feasibility",       s3, s3Val));
        scoreCard.add(Box.createVerticalStrut(14));

        // Total row
        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setBackground(Theme.BG_SECONDARY);
        totalRow.setBorder(new EmptyBorder(10, 14, 10, 14));
        totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        totalRow.setAlignmentX(LEFT_ALIGNMENT);
        JLabel totalLbl = new JLabel("Overall score"); totalLbl.setFont(Theme.FONT_BODY); totalLbl.setForeground(Theme.TEXT_PRIMARY);
        totalRow.add(totalLbl, BorderLayout.WEST);
        totalRow.add(totalVal, BorderLayout.EAST);
        scoreCard.add(totalRow);
        scoreCard.add(Box.createVerticalStrut(14));

        // Feedback
        JLabel fbLabel = Components.formLabel("Feedback");
        fbLabel.setAlignmentX(LEFT_ALIGNMENT);
        JTextArea feedbackArea = Components.textArea("Provide detailed feedback for the developer...");
        JScrollPane fbScroll = new JScrollPane(feedbackArea);
        fbScroll.setBorder(new LineBorder(Theme.BORDER, 1, true));
        fbScroll.setAlignmentX(LEFT_ALIGNMENT);
        fbScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        scoreCard.add(fbLabel);
        scoreCard.add(Box.createVerticalStrut(4));
        scoreCard.add(fbScroll);
        scoreCard.add(Box.createVerticalStrut(14));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        JButton submit = Components.primaryBtn("Submit evaluation");
        JButton cancel = Components.outlineBtn("Cancel");
        cancel.addActionListener(e -> dispose());
        submit.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Evaluation submitted! Score: " + totalVal.getText() + "/100", "Done", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });
        btnRow.add(submit); btnRow.add(cancel);
        scoreCard.add(btnRow);

        body.add(title);
        body.add(Box.createVerticalStrut(4));
        body.add(sub);
        body.add(Box.createVerticalStrut(14));
        body.add(infoCard);
        body.add(Box.createVerticalStrut(14));
        body.add(scoreCard);
        return Components.scroll(body);
    }

    private JSlider slider(int value) {
        JSlider s = new JSlider(0, 100, value);
        s.setPaintTicks(false);
        s.setPaintLabels(false);
        s.setOpaque(false);
        return s;
    }

    private JLabel scoreLabel(String value) {
        JLabel l = new JLabel(value);
        l.setFont(new Font("Segoe UI", Font.BOLD, 18));
        l.setForeground(Theme.PRIMARY);
        l.setPreferredSize(new Dimension(34, 24));
        l.setHorizontalAlignment(SwingConstants.RIGHT);
        return l;
    }

    private JPanel sliderRow(String label, JSlider slider, JLabel valLabel) {
        JPanel row = new JPanel(new BorderLayout(8, 4));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lbl = Components.formLabel(label);
        top.add(lbl, BorderLayout.WEST);
        top.add(valLabel, BorderLayout.EAST);
        row.add(top, BorderLayout.NORTH);
        row.add(slider, BorderLayout.CENTER);
        slider.addChangeListener(e -> valLabel.setText(String.valueOf(slider.getValue())));
        return row;
    }

    private void updateTotal() {
        int avg = (s1.getValue() + s2.getValue() + s3.getValue()) / 3;
        totalVal.setText(String.valueOf(avg));
    }
}
