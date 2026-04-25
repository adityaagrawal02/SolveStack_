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

public class LeaderboardUI extends FxModalWindow {

    public LeaderboardUI(Object parent) {
        super("SolveStack - Leaderboard", 1120, 760, parent);
    }

    @Override
    protected Parent buildContent() {
        BorderPane root = new BorderPane();
        root.getStyleClass().addAll("modal-root", "app-root");
        root.setTop(topBar());

        VBox content = new VBox(14);
        content.setPadding(new Insets(24));

        Label title = new Label("Leaderboard");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Top-performing submissions ranked by final evaluator score.");
        subtitle.getStyleClass().add("muted");

        VBox board = new VBox(10);
        for (Entry entry : entries()) {
            board.getChildren().add(entryCard(entry));
        }

        content.getChildren().addAll(title, subtitle, board);

        ScrollPane scroller = new ScrollPane(content);
        scroller.setFitToWidth(true);
        scroller.getStyleClass().add("page-scroll");

        root.setCenter(scroller);
        return root;
    }

    private VBox entryCard(Entry entry) {
        Label rank = new Label("#" + entry.rank());
        rank.getStyleClass().addAll("rank-pill", entry.rankClass());

        Label name = new Label(entry.name() + "   " + entry.solution());
        name.getStyleClass().add("table-title");

        Label challenge = new Label(entry.challenge());
        challenge.getStyleClass().add("muted");

        Label score = new Label(entry.score() + " / 100");
        score.getStyleClass().add("score-highlight");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox top = new HBox(10, rank, name, spacer, score);
        top.setAlignment(Pos.CENTER_LEFT);

        Label status = new Label(entry.status());
        status.getStyleClass().addAll("status-pill", entry.statusClass());

        return FxComponents.card(top, challenge, status);
    }

    private HBox topBar() {
        Label title = new Label("Global Leaderboard");
        title.getStyleClass().add("section-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button close = FxComponents.smallBtn("Close", this::dispose);

        HBox top = new HBox(12, new LogoPanel(true), title, spacer, close);
        top.setAlignment(Pos.CENTER_LEFT);
        top.getStyleClass().add("top-nav");
        return top;
    }

    private List<Entry> entries() {
        return List.of(
                new Entry(1, "Priya M.", "Reinforcement Learning Planner", "Carbon Footprint Tracker", "Accepted", "chip-success", 94, "rank-gold"),
                new Entry(2, "Rahul K.", "Graph Neural Network Optimizer", "Supply Chain AI", "Under Review", "chip-warning", 88, "rank-silver"),
                new Entry(3, "Siddharth A.", "Genetic Algorithm Solver", "Smart Inventory", "Under Review", "chip-warning", 82, "rank-bronze"),
                new Entry(4, "Vishal R.", "MILP Optimization Model", "Supply Chain AI", "Pending", "chip-info", 76, "rank-default")
        );
    }

    private record Entry(int rank, String name, String solution, String challenge,
                         String status, String statusClass, int score, String rankClass) {
    }
}

