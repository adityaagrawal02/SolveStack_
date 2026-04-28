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

        // Main content area - we use a wrapper so we can swap the center easily
        BorderPane contentArea = new BorderPane();
        contentArea.setTop(buildTopBar(stage, role));
        
        // Initial body
        contentArea.setCenter(buildBody(stage, role, "Dashboard"));

        // Sidebar - pass contentArea so it can switch views
        VBox sidebar = buildSidebar(stage, role, contentArea);
        root.setLeft(sidebar);
        root.setCenter(contentArea);

        Scene scene = new Scene(root, Theme.WINDOW_WIDTH, Theme.WINDOW_HEIGHT);
        FxStyles.apply(scene);
        return scene;
    }

    private static VBox buildSidebar(Stage stage, String role, BorderPane contentArea) {
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

        List<Button> navButtons = List.of(dash, explore, myWork, messages, settings);

        dash.setOnAction(e -> updateActiveNav(navButtons, dash, contentArea, stage, role, "Dashboard"));
        explore.setOnAction(e -> updateActiveNav(navButtons, explore, contentArea, stage, role, "Explore"));
        myWork.setOnAction(e -> updateActiveNav(navButtons, myWork, contentArea, stage, role, "My Projects"));
        messages.setOnAction(e -> updateActiveNav(navButtons, messages, contentArea, stage, role, "Messages"));
        settings.setOnAction(e -> updateActiveNav(navButtons, settings, contentArea, stage, role, "Settings"));

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

    private static void updateActiveNav(List<Button> buttons, Button active, BorderPane contentArea, Stage stage, String role, String view) {
        buttons.forEach(b -> b.getStyleClass().remove("nav-item-active"));
        active.getStyleClass().add("nav-item-active");
        contentArea.setCenter(buildBody(stage, role, view));
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

    private static Node buildBody(Stage stage, String role, String view) {
        return switch (view) {
            case "Explore" -> buildExploreView(stage);
            case "My Projects" -> buildMyProjectsView(stage);
            case "Messages" -> buildMessagesView();
            case "Settings" -> buildSettingsView();
            default -> buildDashboardView(stage, role);
        };
    }

    private static ScrollPane buildDashboardView(Stage stage, String role) {
        Label heading = new Label(role + " Overview");
        heading.getStyleClass().add("page-title");

        Label subtitle = new Label(roleSubtitle(role));
        subtitle.getStyleClass().add("muted");

        HBox metrics = new HBox(16,
                FxComponents.metric(metricLabel(role, 0), metricValue(role, 0)),
                FxComponents.metric(metricLabel(role, 1), metricValue(role, 1)),
                FxComponents.metric(metricLabel(role, 2), metricValue(role, 2)),
                FxComponents.metric(metricLabel(role, 3), metricValue(role, 3))
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

    private static ScrollPane buildExploreView(Stage stage) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(40));

        Label heading = new Label("Explore Challenges");
        heading.getStyleClass().add("page-title");

        Label sub = new Label("Discover active innovation tracks and submit your ideas.");
        sub.getStyleClass().add("muted");

        VBox list = FxComponents.glassCard();
        list.getChildren().add(FxComponents.sectionHeader("Open Challenges"));

        List<models.Challenge> challenges = ChallengeRepository.getInstance().getAllChallenges();
        for (models.Challenge c : challenges) {
            Label name = new Label(c.getTitle());
            name.getStyleClass().add("table-title");
            Label meta = new Label(c.getPostedBy().getUsername() + " • Prize: INR " + c.getPrizeAmount());
            meta.getStyleClass().add("muted");

            Button applyBtn = FxComponents.primaryBtn("View Details", () -> new ChallengeUI(stage).setVisible(true));
            applyBtn.getStyleClass().add("compact-btn");

            HBox right = new HBox(applyBtn);
            right.setAlignment(Pos.CENTER_RIGHT);
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox row = new HBox(12, new VBox(4, name, meta), spacer, right);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12, 0, 12, 0));
            row.setStyle("-fx-border-color: transparent transparent #334155 transparent; -fx-border-width: 1px;");

            list.getChildren().add(row);
        }

        content.getChildren().addAll(heading, sub, list);
        animateIn(content);

        ScrollPane scroller = new ScrollPane(content);
        scroller.setFitToWidth(true);
        scroller.getStyleClass().add("page-scroll");
        return scroller;
    }

    private static ScrollPane buildMyProjectsView(Stage stage) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(40));

        Label heading = new Label("My Projects");
        heading.getStyleClass().add("page-title");

        Label sub = new Label("Track the status of your submissions and ongoing work.");
        sub.getStyleClass().add("muted");

        VBox list = FxComponents.glassCard();
        models.User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser instanceof models.Developer dev) {
            List<models.Submission> mySubs = dev.getMySubmissions();
            if (mySubs.isEmpty()) {
                list.getChildren().add(new Label("No active projects found."));
            } else {
                for (models.Submission s : mySubs) {
                    Label name = new Label(s.getSubmissionId());
                    name.getStyleClass().add("table-title");
                    Label meta = new Label("Status: " + s.getStatus());
                    meta.getStyleClass().add("muted");
                    list.getChildren().add(new VBox(4, name, meta));
                }
            }
        }
        list.getChildren().add(FxComponents.sectionHeader("Recent Submissions"));

        List<String[]> projects = List.of(
            new String[]{"Graph Neural Network Optimizer", "Supply Chain AI", "Under Review", "chip-warning"},
            new String[]{"Reinforcement Learning Planner", "Carbon Tracker", "Accepted", "chip-success"},
            new String[]{"MILP Optimization Model", "Supply Chain AI", "Pending", "chip-info"}
        );

        for (String[] p : projects) {
            Label name = new Label(p[0]);
            name.getStyleClass().add("table-title");
            Label meta = new Label("Challenge: " + p[1]);
            meta.getStyleClass().add("muted");
            
            Label status = new Label(p[2]);
            status.getStyleClass().addAll("status-pill", p[3]);
            
            HBox right = new HBox(status);
            right.setAlignment(Pos.CENTER_RIGHT);
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            HBox row = new HBox(12, new VBox(4, name, meta), spacer, right);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12, 0, 12, 0));
            row.setStyle("-fx-border-color: transparent transparent #334155 transparent; -fx-border-width: 1px;");
            
            list.getChildren().add(row);
        }

        content.getChildren().addAll(heading, sub, list);
        animateIn(content);

        ScrollPane scroller = new ScrollPane(content);
        scroller.setFitToWidth(true);
        scroller.getStyleClass().add("page-scroll");
        return scroller;
    }

    private static ScrollPane buildMessagesView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(40));

        Label heading = new Label("Messages");
        heading.getStyleClass().add("page-title");

        Label sub = new Label("Communicate with evaluators and team members.");
        sub.getStyleClass().add("muted");

        VBox list = FxComponents.glassCard();
        list.getChildren().add(FxComponents.sectionHeader("Inbox"));

        List<String[]> messages = List.of(
            new String[]{"Evaluator Feedback", "Your submission for Supply Chain AI has been reviewed.", "2 hours ago"},
            new String[]{"Acme Corp", "We have updated the requirements for the tracking module.", "1 day ago"},
            new String[]{"System Admin", "Scheduled maintenance tonight at 02:00 AM UTC.", "3 days ago"}
        );

        for (String[] m : messages) {
            Label sender = new Label(m[0]);
            sender.getStyleClass().add("table-title");
            Label time = new Label(m[2]);
            time.getStyleClass().add("muted");
            
            Label body = new Label(m[1]);
            body.getStyleClass().add("activity-text");
            
            Region r = new Region();
            HBox.setHgrow(r, Priority.ALWAYS);
            HBox top = new HBox(sender, r, time);
            
            VBox row = new VBox(8, top, body);
            row.setPadding(new Insets(16, 0, 16, 0));
            row.setStyle("-fx-border-color: transparent transparent #334155 transparent; -fx-border-width: 1px;");
            
            list.getChildren().add(row);
        }

        content.getChildren().addAll(heading, sub, list);
        animateIn(content);

        ScrollPane scroller = new ScrollPane(content);
        scroller.setFitToWidth(true);
        scroller.getStyleClass().add("page-scroll");
        return scroller;
    }

    private static ScrollPane buildSettingsView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(40));

        Label heading = new Label("Settings");
        heading.getStyleClass().add("page-title");

        Label sub = new Label("Manage your account preferences and profile.");
        sub.getStyleClass().add("muted");

        models.User currentUser = UserSession.getInstance().getCurrentUser();
        
        TextField usernameField = FxComponents.textField(currentUser != null ? currentUser.getUsername() : "Username");
        if (currentUser != null) usernameField.setText(currentUser.getUsername());
        
        TextField emailField = FxComponents.textField(currentUser != null ? currentUser.getEmail() : "Email");
        if (currentUser != null) emailField.setText(currentUser.getEmail());

        Region spacer = new Region();
        spacer.setMinHeight(10);

        VBox form = FxComponents.card(
            FxComponents.sectionHeader("Profile Information"),
            FxComponents.formLabel("Username"),
            usernameField,
            FxComponents.formLabel("Email Address"),
            emailField,
            FxComponents.formLabel("Bio"),
            FxComponents.textArea("Add a short bio about your expertise..."),
            spacer,
            FxComponents.primaryBtn("Save Changes", () -> FxComponents.showInfo("Settings", "Profile updated successfully."))
        );
        form.getStyleClass().add("form-card");
        form.setMaxWidth(600);

        content.getChildren().addAll(heading, sub, form);
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
                    FxComponents.outlineBtn("My Submissions", () -> new SubmissionsUI(stage, null).setVisible(true)),
                    FxComponents.ghostBtn("Leaderboard", () -> new LeaderboardUI(stage).setVisible(true))
            );
            case "evaluator" -> {
                List<models.Submission> subs = ChallengeRepository.getInstance().getAllSubmissions();
                String firstSubId = subs.isEmpty() ? "" : subs.get(0).getSubmissionId();
                yield List.of(
                        FxComponents.primaryBtn("Evaluate", () -> {
                            if (!firstSubId.isEmpty())
                                new EvaluationUI(stage, firstSubId).setVisible(true);
                            else
                                FxComponents.showInfo("Queue", "No pending submissions.");
                        }),
                        FxComponents.outlineBtn("Leaderboard", () -> new LeaderboardUI(stage).setVisible(true)),
                        FxComponents.ghostBtn("Pending Queue", () -> new SubmissionsUI(stage, null).setVisible(true))
                );
            }
            case "admin" -> List.of(
                    FxComponents.primaryBtn("Pending Verifications", () -> FxComponents.showInfo("Verification Queue", "4 company and evaluator accounts need approval.")),
                    FxComponents.outlineBtn("Moderation", () -> FxComponents.showInfo("Moderation", "Challenge moderation queue has 2 flagged entries.")),
                    FxComponents.ghostBtn("Generate Report", () -> FxComponents.showInfo("Daily Report", "Integrity and usage report generated."))
            );
            case "company" -> List.of(
                    FxComponents.primaryBtn("New Challenge", () -> new CreateChallengeUI(stage).setVisible(true)),
                    FxComponents.outlineBtn("View Challenges", () -> new ChallengeUI(stage).setVisible(true)),
                    FxComponents.ghostBtn("View Submissions", () -> new SubmissionsUI(stage, "Active Challenge").setVisible(true))
            );
            default -> List.of(
                    FxComponents.primaryBtn("Quick Action", () -> FxComponents.showInfo("Notice", "Welcome to SolveStack!")),
                    FxComponents.outlineBtn("Help Center", () -> FxComponents.showInfo("Support", "Support documentation is available in the portal."))
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

    private static String metricLabel(String role, int index) {
        return switch (role.toLowerCase()) {
            case "developer" -> List.of("Active Challenges", "My Submissions", "Earned Prizes", "Success Rate").get(index);
            case "evaluator" -> List.of("Assigned Reviews", "Completed", "Pending", "Avg. Turnaround").get(index);
            case "admin" -> List.of("Pending Approvals", "Active Users", "Flagged Items", "System Health").get(index);
            case "company" -> List.of("Live Challenges", "Total Submissions", "Hiring Pipeline", "Conversion").get(index);
            default -> List.of("Metric A", "Metric B", "Metric C", "Metric D").get(index);
        };
    }

    private static String metricValue(String role, int index) {
        return switch (role.toLowerCase()) {
            case "developer" -> List.of("08", "05", "02", "74%").get(index);
            case "evaluator" -> List.of("14", "27", "09", "91%").get(index);
            case "admin" -> List.of("04", "1.2k", "23", "99%").get(index);
            case "company" -> List.of("03", "46", "12", "88%").get(index);
            default -> List.of("0", "0", "0", "0%").get(index);
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
            case "evaluator" -> {
                List<models.Submission> subs = ChallengeRepository.getInstance().getAllSubmissions();
                if (!subs.isEmpty()) {
                    new EvaluationUI(stage, subs.get(0).getSubmissionId()).setVisible(true);
                } else {
                    FxComponents.showInfo("Evaluator", "No pending submissions to spotlight.");
                }
            }
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

