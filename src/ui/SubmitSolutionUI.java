package ui;

import java.io.File;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class SubmitSolutionUI extends FxModalWindow {

    private final String challengeName;

    public SubmitSolutionUI(Object parent, String challengeName) {
        super("SolveStack - Submit Solution", 900, 760, parent);
        this.challengeName = challengeName == null || challengeName.isBlank()
                ? "Untitled Challenge"
                : challengeName;
    }

    @Override
    protected Parent buildContent() {
        BorderPane root = new BorderPane();
        root.getStyleClass().addAll("modal-root", "app-root");
        root.setTop(topBar());

        VBox content = new VBox(14);
        content.setPadding(new Insets(24));

        Label title = new Label("Submit Your Solution");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label(challengeName + "   Build something bold and production-ready.");
        subtitle.getStyleClass().add("muted");

        TextField solutionTitle = FxComponents.textField("Give your solution a title");
        TextArea description = FxComponents.textArea("Describe architecture, technical approach, assumptions, and expected impact...");
        description.setPrefRowCount(5);

        TextField github = FxComponents.textField("https://github.com/username/repository");
        TextField techStack = FxComponents.textField("e.g. Java, Python, TensorFlow, FastAPI");

        Label fileName = new Label("No file selected");
        fileName.getStyleClass().add("muted");

        Button upload = FxComponents.outlineBtn("Attach Proposal (PDF)", () -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Attach Proposal");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));
            File selected = chooser.showOpenDialog(getStage());
            if (selected != null) {
                fileName.setText(selected.getName());
            }
        });

        HBox uploadRow = new HBox(10, upload, fileName);
        uploadRow.setAlignment(Pos.CENTER_LEFT);

        Button submit = FxComponents.primaryBtn("Submit Solution", () -> {
            String titleValue = trim(solutionTitle.getText());
            String descValue = trim(description.getText());
            if (titleValue.isBlank() || descValue.isBlank()) {
                FxComponents.showError("Incomplete submission", "Please add a solution title and description.");
                return;
            }
            FxComponents.showInfo("Submitted", "Your solution has been submitted successfully.");
            dispose();
        });

        Button cancel = FxComponents.ghostBtn("Cancel", this::dispose);
        HBox actions = new HBox(8, submit, cancel);
        actions.setAlignment(Pos.CENTER_LEFT);

        VBox form = FxComponents.card(
                FxComponents.formLabel("Solution title"), solutionTitle,
                FxComponents.formLabel("Description"), description,
                FxComponents.formLabel("GitHub repository URL"), github,
                FxComponents.formLabel("Tech stack"), techStack,
                FxComponents.formLabel("Supporting document"), uploadRow,
                actions
        );
        form.getStyleClass().add("form-card");

        content.getChildren().addAll(title, subtitle, form);

        ScrollPane scroller = new ScrollPane(content);
        scroller.setFitToWidth(true);
        scroller.getStyleClass().add("page-scroll");

        root.setCenter(scroller);
        return root;
    }

    private HBox topBar() {
        Label title = new Label("Submit Solution");
        title.getStyleClass().add("section-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button close = FxComponents.smallBtn("Close", this::dispose);

        HBox top = new HBox(12, new LogoPanel(true), title, spacer, close);
        top.setAlignment(Pos.CENTER_LEFT);
        top.getStyleClass().add("top-nav");
        return top;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}

