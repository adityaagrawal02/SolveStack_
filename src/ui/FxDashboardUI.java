package ui;

import java.util.List;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public final class FxDashboardUI {

    private FxDashboardUI() {
    }

    public static Scene createScene(Stage stage, String role) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");

        // Sidebar
        VBox sidebar = buildSidebar(stage, role);
        root.setLeft(sidebar);

        // Main content area
        BorderPane contentArea = new BorderPane();
        contentArea.setTop(buildTopBar(stage, role));
        contentArea.setCenter(buildBody(stage, role));

        root.setCenter(contentArea);

        Scene scene = new Scene(root, Theme.WINDOW_WIDTH, Theme.WINDOW_HEIGHT);
        FxStyles.apply(scene);
        return scene;
    }

    private static VBox buildSidebar(Stage stage, String role) {
        VBox sidebar = new VBox(8);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(240);
        sidebar.setPadding(new Insets(32, 16, 24, 16));

        Label brand = new Label("SOLVESTACK");
        brand.getStyleClass().addAll("brand", "logo-solve");
        brand.setPadding(new Insets(0, 0, 40, 12));

        Button dash = navItem("\u25A3 Dashboard", true);
        Button explore = navItem("\uD83D\uDD0D Explore", false);
        Button myWork = navItem("\uD83D\uDCC1 My Projects", false);
        Button messages = navItem("\uD83D\uDCE4 Messages", false);
        Button settings = navItem("\u2699 Settings", false);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logout = navItem("\u21AA Sign Out", false);
        logout.setOnAction(e -> {
            UserSession.getInstance().logout();
            new LoginUI().show(stage);
        });

        sidebar.getChildren().addAll(brand, dash, explore, myWork, messages, settings, spacer, logout);
        return sidebar;
    }

    private static Button navItem(String text, boolean active) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-item");
        if (active) btn.getStyleClass().add("nav-item-active");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(12, 16, 12, 16));
        return btn;
    }

    private static HBox buildTopBar(Stage stage, String role) {
        HBox topBar = new HBox(24);
        topBar.getStyleClass().add("top-bar");
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(16, 32, 16, 32));

        HBox searchBox = new HBox(8);
        searchBox.getStyleClass().add("input-field-container");
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(0, 12, 0, 12));
        
        Label searchIcon = new Label("\uD83D\uDD0D");
        searchIcon.getStyleClass().add("muted");
        
        TextField search = new TextField();
        search.setPromptText("Search anything...");
        search.setPrefWidth(320);
        search.getStyleClass().add("text-input");
        searchBox.getChildren().addAll(searchIcon, search);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label(UserSession.getInstance().getCurrentUser().getUsername());
        userLabel.getStyleClass().add("field-label");

        topBar.getChildren().addAll(searchBox, spacer, userLabel, FxComponents.roleBadge(role));
        return topBar;
    }

    private static ScrollPane buildBody(Stage stage, String role) {
        Label heading = new Label(role + " Overview");
        heading.getStyleClass().add("page-title");

        Label subtitle = new Label(roleSubtitle(role));
        subtitle.getStyleClass().add("muted");

        HBox metrics = new HBox(16,
                FxComponents.metric("Active Challenges", metricValue(role, 0)),
                FxComponents.metric("Submissions", metricValue(role, 1)),
                FxComponents.metric("In Review", metricValue(role, 2)),
                FxComponents.metric("Success Rate", metricValue(role, 3))
        );
        metrics.setPadding(new Insets(24, 0, 24, 0));

        HBox actions = new HBox(12);
        actions.getChildren().addAll(actionButtons(stage, role));
        actions.setPadding(new Insets(0, 0, 24, 0));

        VBox timeline = FxComponents.glassCard();
        timeline.getChildren().add(FxComponents.sectionHeader("Recent Activity"));
        for (String item : activityItems(role)) {
            timeline.getChildren().add(activityRow(item));
        }

        VBox spotlight = FxComponents.glassCard(
                FxComponents.sectionHeader("Spotlight"),
                new Label(spotlightTitle(role)),
                new Label(spotlightBody(role)),
                FxComponents.primaryBtn(spotlightActionLabel(role), () -> spotlightAction(stage, role))
        );
        ((Label) spotlight.getChildren().get(1)).getStyleClass().add("table-title");
        ((Label) spotlight.getChildren().get(2)).getStyleClass().add("muted");
        ((Label) spotlight.getChildren().get(2)).setPadding(new Insets(0, 0, 16, 0));

        HBox lower = new HBox(16, timeline, spotlight);
        HBox.setHgrow(timeline, Priority.ALWAYS);
        HBox.setHgrow(spotlight, Priority.ALWAYS);

        VBox content = new VBox(0, heading, subtitle, metrics, actions, lower);
        content.setPadding(new Insets(32));
        animateIn(content);

        ScrollPane scroller = new ScrollPane(content);
        scroller.setFitToWidth(true);
        scroller.getStyleClass().add("page-scroll");
        return scroller;
    }

    private static List<Button> actionButtons(Stage stage, String role) {
        return switch (role.toLowerCase()) {
            case "developer" -> List.of(
                    FxComponents.primaryBtn("Browse Challenges", () -> new ChallengeUI(stage).setVisible(true)),
                    FxComponents.outlineBtn("Submit Solution", () -> new SubmitSolutionUI(stage, "AI-Powered Supply Chain Optimizer").setVisible(true)),
                    FxComponents.ghostBtn("My Submissions", () -> new SubmissionsUI(stage, "AI-Powered Supply Chain Optimizer").setVisible(true))
            );
            case "evaluator" -> List.of(
                    FxComponents.primaryBtn("Evaluate", () -> new EvaluationUI(stage, "Rahul K.", "Graph Neural Network Optimizer").setVisible(true)),
                    FxComponents.outlineBtn("Leaderboard", () -> new LeaderboardUI(stage).setVisible(true)),
                    FxComponents.ghostBtn("Pending Queue", () -> new SubmissionsUI(stage, "AI-Powered Supply Chain Optimizer").setVisible(true))
            );
            case "admin" -> List.of(
                    FxComponents.primaryBtn("Pending Verifications", () -> FxComponents.showInfo("Verification Queue", "4 company and evaluator accounts need approval.")),
                    FxComponents.outlineBtn("Moderation", () -> FxComponents.showInfo("Moderation", "Challenge moderation queue has 2 flagged entries.")),
                    FxComponents.ghostBtn("Generate Report", () -> FxComponents.showInfo("Daily Report", "Integrity and usage report generated."))
            );
            default -> List.of(
                    FxComponents.primaryBtn("New Challenge", () -> new CreateChallengeUI(stage).setVisible(true)),
                    FxComponents.outlineBtn("View Challenges", () -> new ChallengeUI(stage).setVisible(true)),
                    FxComponents.ghostBtn("View Submissions", () -> new SubmissionsUI(stage, "AI-Powered Supply Chain Optimizer").setVisible(true))
            );
        };
    }

    private static Node activityRow(String text) {
        Label dot = new Label("\u25CF");
        dot.getStyleClass().add("accent-dot");

        Label label = new Label(text);
        label.getStyleClass().add("activity-text");

        HBox row = new HBox(10, dot, label);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("activity-row");
        
        return row;
    }

    private static String roleSubtitle(String role) {
        return switch (role.toLowerCase()) {
            case "developer" -> "Build, submit, and iterate rapidly with live challenge intelligence.";
            case "evaluator" -> "Review submissions with consistency and publish high-signal feedback.";
            case "admin" -> "Protect platform quality with moderation, governance, and verification.";
            default -> "Launch bold challenges and convert ideas into validated solution pipelines.";
        };
    }

    private static String metricValue(String role, int index) {
        return switch (role.toLowerCase()) {
            case "developer" -> List.of("08", "05", "02", "74%").get(index);
            case "evaluator" -> List.of("14", "27", "09", "91%").get(index);
            case "admin" -> List.of("32", "148", "23", "96%").get(index);
            default -> List.of("11", "46", "07", "88%").get(index);
        };
    }

    private static List<String> activityItems(String role) {
        return switch (role.toLowerCase()) {
            case "developer" -> List.of(
                    "AI Supply Chain challenge deadline in 5 days",
                    "Your Smart Inventory submission moved to Under Review",
                    "Evaluator feedback published for Carbon Tracker"
            );
            case "evaluator" -> List.of(
                    "3 new submissions assigned in sustainability category",
                    "Scoring rubric v2 enabled for fintech challenges",
                    "Acme Corp requested expedited review"
            );
            case "admin" -> List.of(
                    "4 user verification requests waiting",
                    "2 challenges flagged by policy scanner",
                    "Daily integrity report delivered"
            );
            default -> List.of(
                    "Top-ranked submission received for AI Crop Detection",
                    "Smart Logistics challenge crossed 120 participants",
                    "Evaluator panel finalized for fintech track"
            );
        };
    }

    private static String spotlightTitle(String role) {
        return switch (role.toLowerCase()) {
            case "developer" -> "High Reward Alert";
            case "evaluator" -> "Priority Review Batch";
            case "admin" -> "Governance Snapshot";
            default -> "Top Talent Momentum";
        };
    }

    private static String spotlightBody(String role) {
        return switch (role.toLowerCase()) {
            case "developer" -> "Acme Corp increased prize pool to INR 7,00,000 for supply chain optimization.";
            case "evaluator" -> "8 submissions are waiting with SLA under 24 hours. Boost score turnaround.";
            case "admin" -> "Verification throughput improved by 18% this week with zero false approvals.";
            default -> "Average submission quality jumped 22% after your last challenge redesign.";
        };
    }

    private static String spotlightActionLabel(String role) {
        return switch (role.toLowerCase()) {
            case "developer" -> "Open Challenge";
            case "evaluator" -> "Start Review";
            case "admin" -> "View Audit";
            default -> "Launch Next Challenge";
        };
    }

    private static void spotlightAction(Stage stage, String role) {
        switch (role.toLowerCase()) {
            case "developer" -> new ChallengeUI(stage).setVisible(true);
            case "evaluator" -> new EvaluationUI(stage, "Rahul K.", "Graph Neural Network Optimizer").setVisible(true);
            case "admin" -> FxComponents.showInfo("Audit", "No critical issues detected in latest compliance sweep.");
            default -> new CreateChallengeUI(stage).setVisible(true);
        }
    }

    private static void animateIn(VBox content) {
        FadeTransition fade = new FadeTransition(Duration.millis(420), content);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(420), content);
        slide.setFromY(16);
        slide.setToY(0);

        fade.play();
        slide.play();
    }
}

