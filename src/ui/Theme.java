package ui;

import java.awt.*;

public class Theme {
    // Colors
    public static final Color PRIMARY       = new Color(22, 109, 108);
    public static final Color PRIMARY_DARK  = new Color(14, 82, 81);
    public static final Color PRIMARY_LIGHT = new Color(224, 246, 245);
    public static final Color BG_WHITE      = Color.WHITE;
    public static final Color BG_LIGHT      = new Color(247, 245, 239);
    public static final Color BG_SECONDARY  = new Color(236, 242, 240);
    public static final Color BORDER        = new Color(202, 209, 205);
    public static final Color TEXT_PRIMARY  = new Color(26, 34, 36);
    public static final Color TEXT_MUTED    = new Color(94, 106, 110);

    public static final Color GREEN_BG   = new Color(225, 241, 226);
    public static final Color GREEN_TEXT = new Color(36, 104, 49);
    public static final Color AMBER_BG   = new Color(252, 239, 214);
    public static final Color AMBER_TEXT = new Color(137, 82, 14);
    public static final Color TEAL_BG    = new Color(222, 243, 240);
    public static final Color TEAL_TEXT  = new Color(12, 104, 101);
    public static final Color CORAL_BG   = new Color(250, 229, 220);
    public static final Color CORAL_TEXT = new Color(142, 66, 36);
    public static final Color GRAY_BG    = new Color(237, 238, 236);
    public static final Color GRAY_TEXT  = new Color(93, 98, 96);

    // Fonts
    public static final Font FONT_TITLE  = new Font("Trebuchet MS", Font.BOLD,   20);
    public static final Font FONT_HEAD   = new Font("Trebuchet MS", Font.BOLD,   16);
    public static final Font FONT_BODY   = new Font("Trebuchet MS", Font.PLAIN,  13);
    public static final Font FONT_SMALL  = new Font("Trebuchet MS", Font.PLAIN,  11);
    public static final Font FONT_METRIC = new Font("Trebuchet MS", Font.BOLD,   24);

    // Dimension helpers
    public static final Dimension WINDOW = new Dimension(900, 650);

    private Theme() {}
}
