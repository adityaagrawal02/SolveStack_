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
        List<models.Submission> subs = ChallengeRepository.getInstance().getAllSubmissions();
        for (models.Submission row : subs) {
            list.getChildren().add(submissionCard(row));
        }

        content.getChildren().addAll(title, subtitle, list);

        ScrollPane scroller = new ScrollPane(content);
        scroller.setFitToWidth(true);
        scroller.getStyleClass().add("page-scroll");

        root.setCenter(scroller);
        return root;
    }

    private VBox submissionCard(models.Submission s) {
        Label person = new Label(s.getDeveloperUsername() + "   " + s.getSubmissionId());
        person.getStyleClass().add("table-title");

        Label summary = new Label(s.getSolutionSummary());
        summary.getStyleClass().add("muted");

        Label status = new Label(s.getStatus().toString());
        status.getStyleClass().addAll("status-pill", "chip-info");

        Button eval = FxComponents.primaryBtn("Evaluate", () -> new EvaluationUI(getStage(), s.getSubmissionId()).setVisible(true));
        eval.getStyleClass().add("compact-btn");

        models.User u = UserSession.getInstance().getCurrentUser();
        eval.setVisible(u instanceof models.Evaluator);
        eval.setManaged(u instanceof models.Evaluator);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bottom = new HBox(10, status, spacer, eval);
        bottom.setAlignment(Pos.CENTER_LEFT);

        VBox card = FxComponents.card(person, summary, bottom);
        card.getStyleClass().add("submission-card");
        return card;
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

}

