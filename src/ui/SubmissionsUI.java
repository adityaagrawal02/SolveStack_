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

public class SubmissionsUI extends FxModalWindow {

    private final String challengeName;

    public SubmissionsUI(Object parent, String challengeName) {
        super("SolveStack - Submissions", 1120, 760, parent);
        this.challengeName = challengeName == null || challengeName.isBlank() ? "Challenge" : challengeName;
    }

    @Override
    protected Parent buildContent() {
        BorderPane root = new BorderPane();
        root.getStyleClass().addAll("modal-root", "app-root");
        root.setTop(topBar());

        VBox content = new VBox(14);
        content.setPadding(new Insets(24));

        Label title = new Label("Submissions");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label(challengeName + "   Review participants and progress states.");
        subtitle.getStyleClass().add("muted");

        VBox list = new VBox(10);
        for (SubmissionRow row : rows()) {
            list.getChildren().add(submissionCard(row));
        }

        content.getChildren().addAll(title, subtitle, list);

        ScrollPane scroller = new ScrollPane(content);
        scroller.setFitToWidth(true);
        scroller.getStyleClass().add("page-scroll");

        root.setCenter(scroller);
        return root;
    }

    private VBox submissionCard(SubmissionRow row) {
        Label person = new Label(row.author() + "   " + row.solution());
        person.getStyleClass().add("table-title");

        Label stack = new Label(row.stack());
        stack.getStyleClass().add("muted");

        Label status = new Label(row.status());
        status.getStyleClass().addAll("status-pill", row.statusClass());

        HBox footer = new HBox(status);
        footer.setAlignment(Pos.CENTER_LEFT);

        return FxComponents.card(person, stack, footer);
    }

    private HBox topBar() {
        Label title = new Label("Submission Review");
        title.getStyleClass().add("section-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button close = FxComponents.smallBtn("Close", this::dispose);

        HBox top = new HBox(12, new LogoPanel(true), title, spacer, close);
        top.setAlignment(Pos.CENTER_LEFT);
        top.getStyleClass().add("top-nav");
        return top;
    }

    private List<SubmissionRow> rows() {
        return List.of(
                new SubmissionRow("Rahul K.", "Graph Neural Network Optimizer", "Python, PyTorch, FastAPI", "Under Review", "chip-warning"),
                new SubmissionRow("Priya M.", "Reinforcement Learning Planner", "TensorFlow, Docker", "Accepted", "chip-success"),
                new SubmissionRow("Siddharth A.", "Genetic Algorithm Solver", "Java, Spring Boot", "Under Review", "chip-warning"),
                new SubmissionRow("Vishal R.", "MILP Optimization Model", "Python, PuLP, Flask", "Pending", "chip-info")
        );
    }

    private record SubmissionRow(String author, String solution, String stack, String status, String statusClass) {
    }
}

