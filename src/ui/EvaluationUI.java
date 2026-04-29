package ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class EvaluationUI extends FxModalWindow {

    private final String submissionId;

    public EvaluationUI(Object parent, String submissionId) {
        super("SolveStack - Evaluate Submission", 920, 760, parent);
        this.submissionId = submissionId;
    }

    @Override
    protected Parent buildContent() {
        BorderPane root = new BorderPane();
        root.getStyleClass().addAll("modal-root", "app-root");
        root.setTop(topBar());

        VBox content = new VBox(14);
        content.setPadding(new Insets(24));

        Label title = new Label("Evaluate Submission");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Reviewing submission: " + submissionId);
        subtitle.getStyleClass().add("muted");

        Slider innovation = slider(75);
        Slider quality = slider(80);
        Slider feasibility = slider(70);

        IntegerProperty total = new SimpleIntegerProperty(75);
        total.bind(Bindings.createIntegerBinding(
                () -> (int) Math.round((innovation.getValue() + quality.getValue() + feasibility.getValue()) / 3.0),
                innovation.valueProperty(), quality.valueProperty(), feasibility.valueProperty()
        ));

        Label totalValue = new Label();
        totalValue.textProperty().bind(total.asString("%d / 100"));
        totalValue.getStyleClass().add("score-highlight");

        TextArea feedback = FxComponents.textArea("Provide concise, actionable feedback for the participant...");
        feedback.setPrefRowCount(4);

        VBox scoreCard = FxComponents.card(
                scoreRow("Innovation", innovation),
                scoreRow("Technical quality", quality),
                scoreRow("Feasibility", feasibility),
                FxComponents.sep(),
                totalRow(totalValue),
                feedback,
                actionRow(feedback, total)
        );
        scoreCard.getStyleClass().add("form-card");

        content.getChildren().addAll(title, subtitle, scoreCard);

        ScrollPane scroller = new ScrollPane(content);
        scroller.setFitToWidth(true);
        scroller.getStyleClass().add("page-scroll");

        root.setCenter(scroller);
        return root;
    }

    private Slider slider(double value) {
        Slider slider = new Slider(0, 100, value);
        slider.setShowTickLabels(false);
        slider.setShowTickMarks(false);
        slider.getStyleClass().add("score-slider");
        return slider;
    }

    private VBox scoreRow(String labelText, Slider slider) {
        Label label = new Label(labelText);
        label.getStyleClass().add("field-label");

        Label value = new Label();
        value.textProperty().bind(slider.valueProperty().asString("%.0f"));
        value.getStyleClass().add("score-mini");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox top = new HBox(8, label, spacer, value);
        top.setAlignment(Pos.CENTER_LEFT);

        return new VBox(4, top, slider);
    }

    private HBox totalRow(Label totalValue) {
        Label totalLabel = new Label("Overall score");
        totalLabel.getStyleClass().add("field-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(8, totalLabel, spacer, totalValue);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("total-row");
        return row;
    }

    private HBox actionRow(TextArea feedback, IntegerProperty total) {
        Button submit = FxComponents.primaryBtn("Finalize Evaluation", () -> {
            models.Submission s = ChallengeRepository.getInstance().getSubmissionById(submissionId);
            if (s != null) {
                s.evaluate(total.get(), feedback.getText());
                FxComponents.showInfo("Evaluation Saved", "Score of " + total.get() + " assigned to " + submissionId);
                dispose();
            } else {
                FxComponents.showError("Error", "Submission not found.");
            }
        });

        Button cancel = FxComponents.outlineBtn("Cancel", this::dispose);

        HBox row = new HBox(8, submit, cancel);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox topBar() {
        Label title = new Label("Submission Evaluation");
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

