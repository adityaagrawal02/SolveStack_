package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class CreateChallengeUI extends FxModalWindow {

    public CreateChallengeUI() {
        this(null);
    }

    public CreateChallengeUI(Object parent) {
        super("SolveStack - Create Challenge", 880, 760, parent);
    }

    @Override
    protected Parent buildContent() {
        BorderPane root = new BorderPane();
        root.getStyleClass().addAll("modal-root", "app-root");
        root.setTop(topBar());

        VBox content = new VBox(14);
        content.setPadding(new Insets(24));

        Label title = new Label("Launch a New Challenge");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Define the problem, set rewards, and attract top solution builders.");
        subtitle.getStyleClass().add("muted");

        TextField challengeTitle = FxComponents.textField("e.g. AI-powered logistics optimizer");
        TextArea description = FxComponents.textArea("Describe the problem statement, expected outcomes, and constraints...");
        description.setPrefRowCount(5);

        ComboBox<String> category = new ComboBox<>();
        category.getItems().addAll("Technology", "Sustainability", "Healthcare", "Finance", "Agritech");
        category.getSelectionModel().selectFirst();
        category.getStyleClass().add("text-input");

        TextField prize = FxComponents.textField("e.g. 500000");
        DatePicker deadline = new DatePicker();
        deadline.getStyleClass().add("text-input");

        TextArea criteria = FxComponents.textArea("Evaluation criteria, quality bar, and judging rubric...");
        criteria.setPrefRowCount(4);

        VBox form = FxComponents.card(
                FxComponents.formLabel("Challenge title"), challengeTitle,
                FxComponents.formLabel("Description"), description,
                FxComponents.formLabel("Category"), category,
                doubleFieldRow("Prize Amount (INR)", prize, "Submission Deadline", deadline),
                FxComponents.formLabel("Evaluation criteria"), criteria,
                actionRow(challengeTitle)
        );
        form.getStyleClass().add("form-card");

        content.getChildren().addAll(title, subtitle, form);

        ScrollPane scroller = new ScrollPane(content);
        scroller.setFitToWidth(true);
        scroller.getStyleClass().add("page-scroll");

        root.setCenter(scroller);
        return root;
    }

    private HBox doubleFieldRow(String leftLabel, TextField leftField, String rightLabel, DatePicker rightField) {
        VBox left = new VBox(4, FxComponents.formLabel(leftLabel), leftField);
        VBox right = new VBox(4, FxComponents.formLabel(rightLabel), rightField);
        HBox row = new HBox(12, left, right);
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        leftField.setMaxWidth(Double.MAX_VALUE);
        rightField.setMaxWidth(Double.MAX_VALUE);
        return row;
    }

    private HBox actionRow(TextField challengeTitle) {
        Button post = FxComponents.primaryBtn("Post Challenge", () -> {
            String title = challengeTitle.getText() == null ? "" : challengeTitle.getText().trim();
            if (title.isBlank()) {
                FxComponents.showError("Missing title", "Please enter a challenge title before posting.");
                return;
            }
            FxComponents.showInfo("Challenge published", "Your challenge is now live in the discovery feed.");
            dispose();
        });

        Button cancel = FxComponents.outlineBtn("Cancel", this::dispose);

        HBox row = new HBox(8, post, cancel);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox topBar() {
        Label title = new Label("Create Challenge");
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

