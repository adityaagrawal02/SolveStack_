package ui;

import dao.EvaluationDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.User;

import java.util.List;

/*
 FULL DB CONNECTED EVALUATOR DASHBOARD
 Uses:
 - EvaluationDAO
 - UserSession
*/

public class EvaluatorDashboardUI {

    private final EvaluationDAO evaluationDAO =
            new EvaluationDAO();

    public void setVisible(boolean visible) {

        if (!visible) {
            return;
        }

        FxComponents.runFx(() -> {

            if (!isAuthorized()) {
                new LoginUI().setVisible(true);
                return;
            }

            showDashboard();
        });
    }

    private boolean isAuthorized() {

        UserSession session =
                UserSession.getInstance();

        return session.isLoggedIn()
                && session.hasRole("Evaluator");
    }

    private void showDashboard() {

        Stage stage =
                FxSolveStackApp.getPrimaryStage();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        /* ======================================
           TOP BAR
           ====================================== */

        Label title =
                new Label("Evaluator Dashboard");

        title.setStyle(
                "-fx-font-size:24px;" +
                        "-fx-font-weight:bold;"
        );

        Button logout =
                new Button("Logout");

        logout.setOnAction(e -> {
            UserSession.getInstance().logout();
            new LoginUI().show(stage);
        });

        Region spacer = new Region();
        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox top =
                new HBox(
                        15,
                        title,
                        spacer,
                        logout
                );

        top.setAlignment(Pos.CENTER_LEFT);

        root.setTop(top);

        /* ======================================
           MAIN CONTENT
           ====================================== */

        VBox content =
                new VBox(18);

        content.setPadding(new Insets(20));

        User currentUser =
                UserSession.getInstance()
                        .getCurrentUser();

        Label welcome =
                new Label(
                        "Welcome, " +
                                currentUser.getUsername()
                );

        welcome.setStyle(
                "-fx-font-size:18px;"
        );

        /* ======================================
           PENDING SUBMISSIONS
           ====================================== */

        ListView<String> pendingList =
                new ListView<>();

        pendingList.setPrefHeight(260);

        loadPending(pendingList);

        Button refreshPending =
                new Button("Refresh");

        refreshPending.setOnAction(e ->
                loadPending(pendingList));

        VBox pendingBox =
                new VBox(
                        10,
                        new Label(
                                "Pending Reviews"
                        ),
                        pendingList,
                        refreshPending
                );

        /* ======================================
           REVIEW FORM
           ====================================== */

        TextField submissionIdField =
                new TextField();

        submissionIdField.setPromptText(
                "Submission ID"
        );

        TextField scoreField =
                new TextField();

        scoreField.setPromptText(
                "Score (0 - 100)"
        );

        TextArea feedbackField =
                new TextArea();

        feedbackField.setPromptText(
                "Write feedback..."
        );

        feedbackField.setPrefHeight(120);

        Label msg = new Label();

        Button approveBtn =
                new Button("Accept");

        Button rejectBtn =
                new Button("Reject");

        approveBtn.setOnAction(e ->
                acceptSubmission(
                        submissionIdField,
                        scoreField,
                        feedbackField,
                        msg
                )
        );

        rejectBtn.setOnAction(e ->
                rejectSubmission(
                        submissionIdField,
                        feedbackField,
                        msg
                )
        );

        HBox btnRow =
                new HBox(
                        10,
                        approveBtn,
                        rejectBtn
                );

        VBox reviewBox =
                new VBox(
                        10,
                        new Label(
                                "Review Submission"
                        ),
                        submissionIdField,
                        scoreField,
                        feedbackField,
                        btnRow,
                        msg
                );

        reviewBox.setPadding(
                new Insets(15)
        );

        reviewBox.setStyle(
                "-fx-border-color:#cccccc;" +
                        "-fx-border-radius:8;" +
                        "-fx-background-radius:8;"
        );

        /* ======================================
           REVIEWED SUBMISSIONS
           ====================================== */

        ListView<String> reviewedList =
                new ListView<>();

        reviewedList.setPrefHeight(220);

        loadReviewed(reviewedList);

        Button refreshReviewed =
                new Button("Refresh Reviewed");

        refreshReviewed.setOnAction(e ->
                loadReviewed(reviewedList));

        VBox reviewedBox =
                new VBox(
                        10,
                        new Label(
                                "Completed Reviews"
                        ),
                        reviewedList,
                        refreshReviewed
                );

        content.getChildren().addAll(
                welcome,
                pendingBox,
                reviewBox,
                reviewedBox
        );

        ScrollPane scroll =
                new ScrollPane(content);

        scroll.setFitToWidth(true);

        root.setCenter(scroll);

        Scene scene =
                new Scene(
                        root,
                        1180,
                        780
                );

        stage.setTitle(
                "SolveStack - Evaluator Dashboard"
        );

        stage.setScene(scene);
        stage.show();
    }

    /* ======================================
       LOAD PENDING
       ====================================== */

    private void loadPending(
            ListView<String> listView
    ) {

        listView.getItems().clear();

        String evaluatorId =
                UserSession.getInstance()
                        .getCurrentUser()
                        .getUserId();

        List<String[]> rows =
                evaluationDAO
                        .getPendingSubmissions(
                                evaluatorId
                        );

        for (String[] row : rows) {

            listView.getItems().add(
                    row[0] + " | " +
                            row[1] + " | " +
                            row[2] + " | " +
                            row[5]
            );
        }
    }

    /* ======================================
       ACCEPT
       ====================================== */

    private void acceptSubmission(
            TextField idField,
            TextField scoreField,
            TextArea feedbackField,
            Label msg
    ) {

        String id =
                idField.getText().trim();

        String scoreText =
                scoreField.getText().trim();

        String feedback =
                feedbackField
                        .getText()
                        .trim();

        if (id.isBlank()
                || scoreText.isBlank()) {

            msg.setText(
                    "Submission ID and score required."
            );
            return;
        }

        try {

            double score =
                    Double.parseDouble(
                            scoreText
                    );

            boolean success =
                    evaluationDAO
                            .evaluateSubmission(
                                    id,
                                    score,
                                    feedback
                            );

            if (success) {

                msg.setText(
                        "Submission accepted."
                );

                idField.clear();
                scoreField.clear();
                feedbackField.clear();

            } else {

                msg.setText(
                        "Operation failed."
                );
            }

        } catch (Exception e) {

            msg.setText(
                    "Invalid score."
            );
        }
    }

    /* ======================================
       REJECT
       ====================================== */

    private void rejectSubmission(
            TextField idField,
            TextArea feedbackField,
            Label msg
    ) {

        String id =
                idField.getText().trim();

        String feedback =
                feedbackField
                        .getText()
                        .trim();

        if (id.isBlank()) {

            msg.setText(
                    "Submission ID required."
            );
            return;
        }

        boolean success =
                evaluationDAO
                        .rejectSubmission(
                                id,
                                feedback
                        );

        if (success) {

            msg.setText(
                    "Submission rejected."
            );

            idField.clear();
            feedbackField.clear();

        } else {

            msg.setText(
                    "Operation failed."
            );
        }
    }

    /* ======================================
       LOAD REVIEWED
       ====================================== */

    private void loadReviewed(
            ListView<String> listView
    ) {

        listView.getItems().clear();

        String evaluatorId =
                UserSession.getInstance()
                        .getCurrentUser()
                        .getUserId();

        List<String[]> rows =
                evaluationDAO
                        .getReviewedSubmissions(
                                evaluatorId
                        );

        for (String[] row : rows) {

            listView.getItems().add(
                    row[0] + " | " +
                            row[1] + " | " +
                            row[3] + " | Score: " +
                            row[4]
            );
        }
    }
}