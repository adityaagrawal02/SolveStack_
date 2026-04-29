package ui;

import dao.ChallengeDAO;
import dao.SubmissionDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.User;

import java.util.List;

/*
 FULL DB CONNECTED DEVELOPER DASHBOARD
 Uses:
 - ChallengeDAO
 - SubmissionDAO
 - UserSession
*/

public class DeveloperDashboardUI {

    private final ChallengeDAO challengeDAO = new ChallengeDAO();
    private final SubmissionDAO submissionDAO = new SubmissionDAO();

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

        UserSession session = UserSession.getInstance();

        return session.isLoggedIn()
                && session.hasRole("Developer");
    }

    private void showDashboard() {

        Stage stage = FxSolveStackApp.getPrimaryStage();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        /* ==========================================
           TOP BAR
           ========================================== */

        Label title = new Label("Developer Dashboard");
        title.setStyle("-fx-font-size:24px; -fx-font-weight:bold;");

        Button logout = new Button("Logout");

        logout.setOnAction(e -> {
            UserSession.getInstance().logout();
            new LoginUI().show(stage);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox top = new HBox(15, title, spacer, logout);
        top.setAlignment(Pos.CENTER_LEFT);

        root.setTop(top);

        /* ==========================================
           MAIN CONTENT
           ========================================== */

        VBox content = new VBox(18);
        content.setPadding(new Insets(20));

        User currentUser =
                UserSession.getInstance()
                        .getCurrentUser();

        Label welcome =
                new Label("Welcome, " + currentUser.getUsername());

        welcome.setStyle("-fx-font-size:18px;");

        /* ==========================================
           AVAILABLE CHALLENGES
           ========================================== */

        ListView<String> challengeList =
                new ListView<>();

        challengeList.setPrefHeight(260);

        loadChallenges(challengeList);

        Button refreshChallenges =
                new Button("Refresh Challenges");

        refreshChallenges.setOnAction(e ->
                loadChallenges(challengeList));

        VBox challengeBox =
                new VBox(
                        10,
                        new Label("Open Challenges"),
                        challengeList,
                        refreshChallenges
                );

        /* ==========================================
           SUBMIT SOLUTION FORM
           ========================================== */

        TextField challengeIdField =
                new TextField();

        challengeIdField.setPromptText("Challenge ID");

        TextArea summaryField =
                new TextArea();

        summaryField.setPromptText(
                "Describe your solution..."
        );

        summaryField.setPrefHeight(120);

        TextField githubField =
                new TextField();

        githubField.setPromptText(
                "GitHub Link (optional)"
        );

        Label submitMsg = new Label();

        Button submitBtn =
                new Button("Submit Solution");

        submitBtn.setOnAction(e ->
                submitSolution(
                        challengeIdField,
                        summaryField,
                        githubField,
                        submitMsg
                )
        );

        VBox submitBox =
                new VBox(
                        10,
                        new Label("Submit Solution"),
                        challengeIdField,
                        summaryField,
                        githubField,
                        submitBtn,
                        submitMsg
                );

        submitBox.setPadding(new Insets(15));
        submitBox.setStyle(
                "-fx-border-color:#cccccc;" +
                        "-fx-border-radius:8;" +
                        "-fx-background-radius:8;"
        );

        /* ==========================================
           MY SUBMISSIONS
           ========================================== */

        ListView<String> mySubmissions =
                new ListView<>();

        mySubmissions.setPrefHeight(220);

        loadMySubmissions(mySubmissions);

        Button refreshSubs =
                new Button("Refresh My Submissions");

        refreshSubs.setOnAction(e ->
                loadMySubmissions(mySubmissions));

        VBox myBox =
                new VBox(
                        10,
                        new Label("My Submissions"),
                        mySubmissions,
                        refreshSubs
                );

        content.getChildren().addAll(
                welcome,
                challengeBox,
                submitBox,
                myBox
        );

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);

        root.setCenter(scroll);

        Scene scene = new Scene(root, 1180, 780);

        stage.setTitle("SolveStack - Developer Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    /* ==========================================
       LOAD OPEN CHALLENGES
       ========================================== */

    private void loadChallenges(ListView<String> listView) {

        listView.getItems().clear();

        List<String[]> rows =
                challengeDAO.getAllOpenChallenges();

        for (String[] row : rows) {

            listView.getItems().add(
                    row[0] + " | " +
                            row[1] + " | Company: " +
                            row[2] + " | ₹" +
                            row[3] + " | Due: " +
                            row[4]
            );
        }
    }

    /* ==========================================
       SUBMIT SOLUTION
       ========================================== */

    private void submitSolution(TextField challengeIdField,
                                TextArea summaryField,
                                TextField githubField,
                                Label msg) {

        String challengeId =
                challengeIdField.getText().trim();

        String summary =
                summaryField.getText().trim();

        String github =
                githubField.getText().trim();

        if (challengeId.isBlank()
                || summary.isBlank()) {

            msg.setText(
                    "Challenge ID and solution are required."
            );
            return;
        }

        User user =
                UserSession.getInstance()
                        .getCurrentUser();

        String developerId = user.getUserId();

        if (submissionDAO.hasSubmitted(
                challengeId,
                developerId
        )) {

            msg.setText(
                    "You already submitted for this challenge."
            );
            return;
        }

        boolean success =
                submissionDAO.submitSolution(
                        challengeId,
                        developerId,
                        summary,
                        github,
                        null
                );

        if (success) {

            msg.setText("Solution submitted successfully.");

            challengeIdField.clear();
            summaryField.clear();
            githubField.clear();

        } else {
            msg.setText("Submission failed.");
        }
    }

    /* ==========================================
       LOAD MY SUBMISSIONS
       ========================================== */

    private void loadMySubmissions(ListView<String> listView) {

        listView.getItems().clear();

        String developerId =
                UserSession.getInstance()
                        .getCurrentUser()
                        .getUserId();

        List<String[]> rows =
                submissionDAO.getSubmissionsByDeveloper(
                        developerId
                );

        for (String[] row : rows) {

            listView.getItems().add(
                    row[0] + " | " +
                            row[1] + " | " +
                            row[2] + " | Score: " +
                            row[3] + " | " +
                            row[4]
            );
        }
    }
}