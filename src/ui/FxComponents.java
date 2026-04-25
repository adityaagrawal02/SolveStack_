package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public final class FxComponents {

    private FxComponents() {
    }

    public static void runFx(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }

    public static HBox navbar(String role, Runnable onLogout) {
        LogoPanel logo = new LogoPanel(false, 24);

        Label subtitle = new Label("Open Innovation Collaboration Platform");
        subtitle.getStyleClass().add("muted");

        VBox brandBox = new VBox(0, logo, subtitle);
        brandBox.setAlignment(Pos.CENTER_LEFT);

        Label badge = roleBadge(role);

        Button logout = smallBtn("Sign out", onLogout);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox nav = new HBox(14, brandBox, spacer, badge, logout);
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.getStyleClass().add("top-nav");
        return nav;
    }

    public static Label roleBadge(String role) {
        String normalizedRole = DashboardRouter.normalizeRole(role);
        Label l = new Label(normalizedRole);
        l.getStyleClass().add("role-badge");
        switch (normalizedRole.toLowerCase()) {
            case "developer" -> l.getStyleClass().add("developer-badge");
            case "evaluator" -> l.getStyleClass().add("evaluator-badge");
            case "admin" -> l.getStyleClass().add("admin-badge");
            default -> l.getStyleClass().add("company-badge");
        }
        return l;
    }

    public static StackPane avatar(String initials, String tone) {
        Label text = new Label(initials.toUpperCase());
        text.getStyleClass().add("avatar-text");

        StackPane pane = new StackPane(text);
        pane.getStyleClass().addAll("avatar", tone == null || tone.isBlank() ? "avatar-sky" : tone);
        pane.setPrefSize(34, 34);
        pane.setMinSize(34, 34);
        return pane;
    }

    public static Button primaryBtn(String text, Runnable action) {
        Button btn = new Button(text);
        btn.getStyleClass().add("primary-btn");
        if (action != null) {
            btn.setOnAction(e -> action.run());
        }
        return btn;
    }

    public static Button outlineBtn(String text, Runnable action) {
        Button btn = new Button(text);
        btn.getStyleClass().add("outline-btn");
        if (action != null) {
            btn.setOnAction(e -> action.run());
        }
        return btn;
    }

    public static Button ghostBtn(String text, Runnable action) {
        Button btn = new Button(text);
        btn.getStyleClass().add("ghost-btn");
        if (action != null) {
            btn.setOnAction(e -> action.run());
        }
        return btn;
    }

    public static Button smallBtn(String text, Runnable action) {
        Button btn = ghostBtn(text, action);
        btn.getStyleClass().add("compact-btn");
        return btn;
    }

    public static VBox metric(String label, String value) {
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("metric-value");

        Label labelLabel = new Label(label);
        labelLabel.getStyleClass().add("metric-label");

        VBox card = new VBox(4, valueLabel, labelLabel);
        card.getStyleClass().add("metric-card");
        card.setMinWidth(200);
        card.setAlignment(Pos.CENTER_LEFT);
        
        return card;
    }

    public static VBox card(Node... nodes) {
        VBox card = new VBox(12);
        card.getStyleClass().add("surface-card");
        card.setPadding(new Insets(16));
        if (nodes != null) {
            card.getChildren().addAll(nodes);
        }
        return card;
    }

    public static VBox glassCard(Node... nodes) {
        VBox card = new VBox(12);
        card.getStyleClass().add("glass-card");
        card.setPadding(new Insets(24));
        if (nodes != null) {
            card.getChildren().addAll(nodes);
        }
        return card;
    }

    public static Label sectionHeader(String text) {
        Label header = new Label(text);
        header.getStyleClass().add("section-title");
        return header;
    }

    public static Label formLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("field-label");
        return label;
    }

    public static Separator sep() {
        return new Separator();
    }

    public static TextField textField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add("text-input");
        return field;
    }

    public static PasswordField passwordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.getStyleClass().add("password-input");
        return field;
    }

    public static TextArea textArea(String prompt) {
        TextArea area = new TextArea();
        area.setPromptText(prompt);
        area.setWrapText(true);
        area.setPrefRowCount(4);
        area.getStyleClass().add("text-area-input");
        return area;
    }

    public static Label notifBanner(String text) {
        Label notice = new Label(text);
        notice.getStyleClass().add("notice");
        notice.setWrapText(true);
        return notice;
    }

    public static HBox row(Node... nodes) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        if (nodes != null) {
            row.getChildren().addAll(nodes);
        }
        return row;
    }

    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
