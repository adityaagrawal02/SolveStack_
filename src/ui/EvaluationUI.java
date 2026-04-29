// =============================================
// FILE 3: ui/EvaluationUI.java
// FINAL VERSION
// SAVES EVALUATION TO MYSQL
// =============================================

package ui;

import dao.SubmissionDAO;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import models.User;

public class EvaluationUI extends FxModalWindow {

    private final String submissionId;

    private final SubmissionDAO submissionDAO =
            new SubmissionDAO();

    public EvaluationUI(
            Object parent,
            String submissionId) {

        super(
                "SolveStack - Evaluate Submission",
                920,
                760,
                parent
        );

        this.submissionId =
                submissionId;
    }

    @Override
    protected Parent buildContent() {

        BorderPane root =
                new BorderPane();

        root.getStyleClass()
                .addAll(
                        "modal-root",
                        "app-root"
                );

        root.setTop(topBar());

        VBox content =
                new VBox(14);

        content.setPadding(
                new Insets(24)
        );

        Label title =
                new Label(
                        "Evaluate Submission"
                );

        title.getStyleClass()
                .add("page-title");

        Label subtitle =
                new Label(
                        "Reviewing submission: "
                                + submissionId
                );

        subtitle.getStyleClass()
                .add("muted");

        Slider innovation =
                slider(75);

        Slider quality =
                slider(80);

        Slider feasibility =
                slider(70);

        IntegerProperty total =
                new SimpleIntegerProperty();

        total.bind(
                Bindings.createIntegerBinding(
                        () ->
                                (int) Math.round(
                                        (
                                                innovation.getValue()
                                                        + quality.getValue()
                                                        + feasibility.getValue()
                                        ) / 3.0
                                ),
                        innovation.valueProperty(),
                        quality.valueProperty(),
                        feasibility.valueProperty()
                )
        );

        Label totalValue =
                new Label();

        totalValue.textProperty()
                .bind(
                        total.asString(
                                "%d / 100"
                        )
                );

        totalValue.getStyleClass()
                .add("score-highlight");

        TextArea feedback =
                FxComponents.textArea(
                        "Provide concise, actionable feedback..."
                );

        feedback.setPrefRowCount(4);

        VBox scoreCard =
                FxComponents.card(
                        scoreRow(
                                "Innovation",
                                innovation
                        ),
                        scoreRow(
                                "Technical Quality",
                                quality
                        ),
                        scoreRow(
                                "Feasibility",
                                feasibility
                        ),
                        FxComponents.sep(),
                        totalRow(totalValue),
                        feedback,
                        actionRow(
                                feedback,
                                total
                        )
                );

        scoreCard.getStyleClass()
                .add("form-card");

        content.getChildren()
                .addAll(
                        title,
                        subtitle,
                        scoreCard
                );

        ScrollPane scroller =
                new ScrollPane(content);

        scroller.setFitToWidth(true);

        scroller.getStyleClass()
                .add("page-scroll");

        root.setCenter(scroller);

        return root;
    }

    /* =====================================
       SLIDER
       ===================================== */

    private Slider slider(
            double value) {

        Slider slider =
                new Slider(
                        0,
                        100,
                        value
                );

        slider.setShowTickLabels(false);
        slider.setShowTickMarks(false);

        slider.getStyleClass()
                .add("score-slider");

        return slider;
    }

    /* =====================================
       SCORE ROW
       ===================================== */

    private VBox scoreRow(
            String labelText,
            Slider slider) {

        Label label =
                new Label(labelText);

        label.getStyleClass()
                .add("field-label");

        Label value =
                new Label();

        value.textProperty()
                .bind(
                        slider.valueProperty()
                                .asString("%.0f")
                );

        value.getStyleClass()
                .add("score-mini");

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox top =
                new HBox(
                        8,
                        label,
                        spacer,
                        value
                );

        top.setAlignment(
                Pos.CENTER_LEFT
        );

        return new VBox(
                4,
                top,
                slider
        );
    }

    /* =====================================
       TOTAL ROW
       ===================================== */

    private HBox totalRow(
            Label totalValue) {

        Label totalLabel =
                new Label(
                        "Overall Score"
                );

        totalLabel.getStyleClass()
                .add("field-label");

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox row =
                new HBox(
                        8,
                        totalLabel,
                        spacer,
                        totalValue
                );

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.getStyleClass()
                .add("total-row");

        return row;
    }

    /* =====================================
       ACTION ROW
       ===================================== */

    private HBox actionRow(
            TextArea feedback,
            IntegerProperty total) {

        Button submit =
                FxComponents.primaryBtn(
                        "Finalize Evaluation",
                        () -> {

                            boolean success =
                                    submissionDAO
                                            .evaluateSubmission(
                                                    submissionId,
                                                    total.get(),
                                                    feedback.getText(),
                                                    "UNDER_REVIEW"
                                            );

                            if (success) {

                                FxComponents.showInfo(
                                        "Evaluation Saved",
                                        "Score of "
                                                + total.get()
                                                + " assigned to "
                                                + submissionId
                                );

                                dispose();

                            } else {

                                FxComponents.showError(
                                        "Error",
                                        "Unable to save evaluation."
                                );
                            }
                        }
                );

        Button accept =
                FxComponents.outlineBtn(
                        "Accept",
                        () -> {

                            boolean success =
                                    submissionDAO
                                            .evaluateSubmission(
                                                    submissionId,
                                                    total.get(),
                                                    feedback.getText(),
                                                    "ACCEPTED"
                                            );

                            if (success) {
                                FxComponents.showInfo(
                                        "Accepted",
                                        "Submission accepted."
                                );
                                dispose();
                            }
                        }
                );

        Button reject =
                FxComponents.outlineBtn(
                        "Reject",
                        () -> {

                            boolean success =
                                    submissionDAO
                                            .evaluateSubmission(
                                                    submissionId,
                                                    total.get(),
                                                    feedback.getText(),
                                                    "REJECTED"
                                            );

                            if (success) {
                                FxComponents.showInfo(
                                        "Rejected",
                                        "Submission rejected."
                                );
                                dispose();
                            }
                        }
                );

        Button cancel =
                FxComponents.outlineBtn(
                        "Cancel",
                        this::dispose
                );

        HBox row =
                new HBox(
                        8,
                        submit,
                        accept,
                        reject,
                        cancel
                );

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        return row;
    }

    /* =====================================
       TOP BAR
       ===================================== */

    private HBox topBar() {

        Label title =
                new Label(
                        "Submission Evaluation"
                );

        title.getStyleClass()
                .add("section-title");

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button close =
                FxComponents.smallBtn(
                        "Close",
                        this::dispose
                );

        HBox top =
                new HBox(
                        12,
                        new LogoPanel(true),
                        title,
                        spacer,
                        close
                );

        top.setAlignment(
                Pos.CENTER_LEFT
        );

        top.getStyleClass()
                .add("top-nav");

        return top;
    }
}