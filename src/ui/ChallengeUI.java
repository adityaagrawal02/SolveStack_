package ui;

import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ChallengeUI extends FxModalWindow {

    public ChallengeUI() {
        this(null);
    }

    public ChallengeUI(Object parent) {
        super("SolveStack - Challenges", 1180, 760, parent);
    }

    @Override
    protected Parent buildContent() {
        BorderPane root = new BorderPane();
        root.getStyleClass().addAll("modal-root", "app-root");

        root.setTop(topBar("Challenge Arena", "Discover active innovation tracks"));

        VBox list = new VBox(12);
        list.setPadding(new Insets(24));

        Label title = new Label("Open Challenges");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Pick a challenge, submit your idea, and climb the leaderboard.");
        subtitle.getStyleClass().add("muted");

        list.getChildren().addAll(title, subtitle);

        List<ChallengeRow> rows = List.of(
                new ChallengeRow("AI-Powered Supply Chain Optimizer", "Acme Corp", "INR 5,00,000", "30 May", "Open", "chip-success"),
                new ChallengeRow("Carbon Footprint Tracker", "GreenTech", "INR 2,00,000", "15 Jun", "Under Review", "chip-warning"),
                new ChallengeRow("Smart Inventory Management", "TechVision", "INR 3,50,000", "15 Jun", "Open", "chip-success"),
                new ChallengeRow("AI Crop Disease Detection", "AgroTech", "INR 2,50,000", "10 Jul", "Open", "chip-success"),
                new ChallengeRow("Renewable Energy Analytics", "SolarGrid", "INR 1,75,000", "22 Jul", "Open", "chip-info")
        );

        for (ChallengeRow row : rows) {
            list.getChildren().add(challengeCard(row));
        }

        ScrollPane scroller = new ScrollPane(list);
        scroller.setFitToWidth(true);
        scroller.getStyleClass().add("page-scroll");
        root.setCenter(scroller);

        return root;
    }

    private VBox challengeCard(ChallengeRow row) {
        Label name = new Label(row.name());
        name.getStyleClass().add("table-title");

        Label meta = new Label(row.company() + "   Prize: " + row.prize() + "   Deadline: " + row.deadline());
        meta.getStyleClass().add("muted");

        Label status = new Label(row.status());
        status.getStyleClass().addAll("status-pill", row.statusClass());

        Button apply = FxComponents.primaryBtn("Apply", () -> new SubmitSolutionUI(getStage(), row.name()).setVisible(true));
        apply.getStyleClass().add("compact-btn");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bottom = new HBox(10, status, spacer, apply);
        bottom.setAlignment(Pos.CENTER_LEFT);

        VBox card = FxComponents.card(name, meta, bottom);
        card.getStyleClass().add("challenge-card");
        return card;
    }

    private HBox topBar(String heading, String subtext) {
        Label title = new Label(heading);
        title.getStyleClass().add("section-title");

        Label sub = new Label(subtext);
        sub.getStyleClass().add("muted");

        VBox text = new VBox(2, title, sub);

        Button close = FxComponents.smallBtn("Close", this::dispose);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox top = new HBox(12, new LogoPanel(true), text, spacer, close);
        top.setAlignment(Pos.CENTER_LEFT);
        top.getStyleClass().add("top-nav");
        return top;
    }

    private record ChallengeRow(String name, String company, String prize, String deadline, String status, String statusClass) {
    }
}

