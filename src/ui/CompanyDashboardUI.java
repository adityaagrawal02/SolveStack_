package ui;

import dao.ChallengeDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.User;

import java.util.List;

/*
 FULL DB CONNECTED COMPANY DASHBOARD
 Uses ChallengeDAO + UserSession
*/

public class CompanyDashboardUI {

    private final ChallengeDAO challengeDAO = new ChallengeDAO();

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
                && session.hasRole("Company");
    }

    private void showDashboard() {

        Stage stage = FxSolveStackApp.getPrimaryStage();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        /* ======================================
           TOP BAR
           ====================================== */

        Label title = new Label("Company Dashboard");
        title.setStyle("-fx-font-size:24px; -fx-font-weight:bold;");

        Button logout = new Button("Logout");
        logout.setOnAction(e -> {
            UserSession.getInstance().logout();
            new LoginUI().show(stage);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox top =
                new HBox(15, title, spacer, logout);

        top.setAlignment(Pos.CENTER_LEFT);

        root.setTop(top);

        /* ======================================
           CENTER CONTENT
           ====================================== */

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label welcome = new Label(
                "Welcome, " +
                        UserSession.getInstance()
                                .getCurrentUser()
                                .getUsername()
        );

        welcome.setStyle("-fx-font-size:18px;");

        /* ======================================
           CREATE CHALLENGE FORM
           ====================================== */

        TextField titleField = new TextField();
        titleField.setPromptText("Challenge Title");

        TextArea descField = new TextArea();
        descField.setPromptText("Challenge Description");
        descField.setPrefHeight(120);

        TextField rewardField = new TextField();
        rewardField.setPromptText("Prize Amount");

        Label msg = new Label();

        Button createBtn = new Button("Post Challenge");

        createBtn.setOnAction(e ->
                createChallenge(
                        titleField,
                        descField,
                        rewardField,
                        msg
                )
        );

        VBox form =
                new VBox(
                        10,
                        new Label("Create New Challenge"),
                        titleField,
                        descField,
                        rewardField,
                        createBtn,
                        msg
                );

        form.setPadding(new Insets(15));
        form.setStyle(
                "-fx-border-color:#cccccc;" +
                        "-fx-border-radius:8;" +
                        "-fx-background-radius:8;"
        );

        /* ======================================
           MY CHALLENGES TABLE
           ====================================== */

        ListView<String> challengeList =
                new ListView<>();

        loadMyChallenges(challengeList);

        Button refresh = new Button("Refresh");
        refresh.setOnAction(e ->
                loadMyChallenges(challengeList));

        VBox tableBox =
                new VBox(
                        10,
                        new Label("My Challenges"),
                        challengeList,
                        refresh
                );

        content.getChildren().addAll(
                welcome,
                form,
                tableBox
        );

        root.setCenter(content);

        Scene scene = new Scene(root, 1100, 760);
        stage.setTitle("SolveStack - Company Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    /* ======================================
       CREATE CHALLENGE
       ====================================== */

    private void createChallenge(TextField titleField,
                                 TextArea descField,
                                 TextField rewardField,
                                 Label msg) {

        String title = titleField.getText().trim();
        String desc = descField.getText().trim();
        String rewardText = rewardField.getText().trim();

        if (title.isBlank()
                || desc.isBlank()
                || rewardText.isBlank()) {

            msg.setText("All fields required.");
            return;
        }

        try {

            double reward =
                    Double.parseDouble(rewardText);

            User user =
                    UserSession.getInstance()
                            .getCurrentUser();

            boolean success =
                    challengeDAO.addChallenge(
                            title,
                            desc,
                            user.getUserId(),
                            reward
                    );

            if (success) {

                msg.setText("Challenge posted successfully.");

                titleField.clear();
                descField.clear();
                rewardField.clear();

            } else {
                msg.setText("Failed to create challenge.");
            }

        } catch (Exception e) {
            msg.setText("Invalid reward amount.");
        }
    }

    /* ======================================
       LOAD COMPANY CHALLENGES
       ====================================== */

    private void loadMyChallenges(ListView<String> listView) {

        listView.getItems().clear();

        String companyId =
                UserSession.getInstance()
                        .getCurrentUser()
                        .getUserId();

        List<String[]> rows =
                challengeDAO.getChallengesByCompany(companyId);

        for (String[] row : rows) {

            listView.getItems().add(
                    row[0] + " | " +
                            row[1] + " | " +
                            row[2] + " | ₹" +
                            row[3] + " | Due: " +
                            row[4]
            );
        }
    }
}