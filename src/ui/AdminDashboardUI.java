package ui;

import dao.ChallengeDAO;
import dao.SubmissionDAO;
import db.DBConnection;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/*
 FULL DB CONNECTED ADMIN DASHBOARD
 Uses:
 - users table
 - challenges table
 - submissions table
*/

public class AdminDashboardUI {

    private final ChallengeDAO challengeDAO =
            new ChallengeDAO();

    private final SubmissionDAO submissionDAO =
            new SubmissionDAO();

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
                && session.hasRole("Admin");
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
                new Label("Admin Dashboard");

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
                new VBox(20);

        content.setPadding(new Insets(20));

        Label welcome =
                new Label(
                        "Platform Analytics & Control Panel"
                );

        welcome.setStyle(
                "-fx-font-size:18px;"
        );

        /* ======================================
           STATS
           ====================================== */

        Label totalUsers =
                new Label(
                        "Total Users: " +
                                getTotalUsers()
                );

        Label totalChallenges =
                new Label(
                        "Total Challenges: " +
                                challengeDAO
                                        .getTotalChallenges()
                );

        Label openChallenges =
                new Label(
                        "Open Challenges: " +
                                challengeDAO
                                        .getOpenChallengesCount()
                );

        Label totalSubmissions =
                new Label(
                        "Total Submissions: " +
                                submissionDAO
                                        .getTotalSubmissions()
                );

        VBox statsBox =
                new VBox(
                        10,
                        totalUsers,
                        totalChallenges,
                        openChallenges,
                        totalSubmissions
                );

        statsBox.setPadding(
                new Insets(15)
        );

        statsBox.setStyle(
                "-fx-border-color:#cccccc;" +
                        "-fx-border-radius:8;" +
                        "-fx-background-radius:8;"
        );

        /* ======================================
           USER LIST
           ====================================== */

        ListView<String> userList =
                new ListView<>();

        userList.setPrefHeight(300);

        loadUsers(userList);

        Button refreshUsers =
                new Button("Refresh Users");

        refreshUsers.setOnAction(e ->
                loadUsers(userList));

        VBox usersBox =
                new VBox(
                        10,
                        new Label("Registered Users"),
                        userList,
                        refreshUsers
                );

        /* ======================================
           DELETE USER
           ====================================== */

        TextField userIdField =
                new TextField();

        userIdField.setPromptText(
                "User ID to delete"
        );

        Label msg = new Label();

        Button deleteBtn =
                new Button("Delete User");

        deleteBtn.setOnAction(e ->
                deleteUser(
                        userIdField,
                        msg,
                        userList
                )
        );

        VBox controlBox =
                new VBox(
                        10,
                        new Label("Administrative Actions"),
                        userIdField,
                        deleteBtn,
                        msg
                );

        controlBox.setPadding(
                new Insets(15)
        );

        controlBox.setStyle(
                "-fx-border-color:#cccccc;" +
                        "-fx-border-radius:8;" +
                        "-fx-background-radius:8;"
        );

        content.getChildren().addAll(
                welcome,
                statsBox,
                usersBox,
                controlBox
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
                "SolveStack - Admin Dashboard"
        );

        stage.setScene(scene);
        stage.show();
    }

    /* ======================================
       TOTAL USERS
       ====================================== */

    private int getTotalUsers() {

        String sql =
                "SELECT COUNT(*) FROM users";

        try (Connection con =
                     DBConnection.getConnection();

             PreparedStatement ps =
                     con.prepareStatement(sql);

             ResultSet rs =
                     ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception ignored) {
        }

        return 0;
    }

    /* ======================================
       LOAD USERS
       ====================================== */

    private void loadUsers(
            ListView<String> listView
    ) {

        listView.getItems().clear();

        String sql =
                "SELECT user_id, username, role, email " +
                        "FROM users " +
                        "ORDER BY username";

        try (Connection con =
                     DBConnection.getConnection();

             PreparedStatement ps =
                     con.prepareStatement(sql);

             ResultSet rs =
                     ps.executeQuery()) {

            while (rs.next()) {

                listView.getItems().add(
                        rs.getString("user_id")
                                + " | "
                                + rs.getString("username")
                                + " | "
                                + rs.getString("role")
                                + " | "
                                + rs.getString("email")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ======================================
       DELETE USER
       ====================================== */

    private void deleteUser(
            TextField field,
            Label msg,
            ListView<String> listView
    ) {

        String userId =
                field.getText().trim();

        if (userId.isBlank()) {

            msg.setText(
                    "Enter user ID."
            );
            return;
        }

        String sql =
                "DELETE FROM users WHERE user_id=?";

        try (Connection con =
                     DBConnection.getConnection();

             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, userId);

            boolean success =
                    ps.executeUpdate() > 0;

            if (success) {

                msg.setText(
                        "User deleted."
                );

                field.clear();
                loadUsers(listView);

            } else {

                msg.setText(
                        "User not found."
                );
            }

        } catch (Exception e) {

            msg.setText(
                    "Delete failed."
            );
        }
    }
}