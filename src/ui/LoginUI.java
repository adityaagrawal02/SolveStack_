package ui;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginUI {

    private String selectedRole = "Admin";
    private Stage stage;

    public void show(Stage stage) {
        this.stage = stage;
        FxSolveStackApp.setPrimaryStage(stage);
        stage.setTitle("SolveStack - Sign In");
        stage.setScene(buildScene());
        stage.show();
    }

    public void setVisible(boolean visible) {
        if (!visible) {
            dispose();
            return;
        }

        Runnable showTask = () -> {
            Stage target = stage != null ? stage : new Stage();
            show(target);
        };

        if (Platform.isFxApplicationThread()) {
            showTask.run();
        } else {
            Platform.runLater(showTask);
        }
    }

    public void dispose() {
        if (stage != null) {
            stage.close();
        }
    }

    private Scene buildScene() {
        HBox shell = new HBox(0);
        shell.getStyleClass().add("auth-shell");
        
        StackPane visualSide = buildVisualSide();
        VBox formSide = buildFormSide();
        
        HBox.setHgrow(visualSide, Priority.ALWAYS);
        HBox.setHgrow(formSide, Priority.ALWAYS);
        
        visualSide.prefWidthProperty().bind(shell.widthProperty().multiply(0.5));
        formSide.prefWidthProperty().bind(shell.widthProperty().multiply(0.5));

        shell.getChildren().addAll(visualSide, formSide);
        Scene scene = new Scene(shell, 1280, 820);
        FxStyles.apply(scene);

        return scene;
    }

    private StackPane buildVisualSide() {
        StackPane pane = new StackPane();
        pane.getStyleClass().add("visual-panel");
        
        // Background Glow
        Circle glow = new Circle(280);
        glow.getStyleClass().add("eclipse-background");
        
        // Ring
        Circle ring = new Circle(240);
        ring.getStyleClass().add("eclipse-ring");
        
        // Logo Text
        VBox logoContent = new VBox(-5);
        logoContent.setAlignment(Pos.CENTER);
        
        LogoPanel logo = new LogoPanel(false, 64);
        logo.setAlignment(Pos.CENTER);
        VBox.setMargin(logo, new Insets(0, 0, 20, 0));
        
        Label motto = new Label("Where innovation meets execution.");
        motto.getStyleClass().add("auth-sub");
        motto.setStyle("-fx-font-size: 18px; -fx-font-weight: 500;");
        
        logoContent.getChildren().addAll(logo, motto);
        
        pane.getChildren().addAll(glow, ring, logoContent);
        StackPane.setAlignment(logoContent, Pos.CENTER);
        
        // Subtle animation
        Timeline anim = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(ring.scaleXProperty(), 1.0), new KeyValue(ring.scaleYProperty(), 1.0)),
            new KeyFrame(Duration.millis(3000), 
                new KeyValue(ring.scaleXProperty(), 1.02, Interpolator.EASE_BOTH), 
                new KeyValue(ring.scaleYProperty(), 1.02, Interpolator.EASE_BOTH)
            )
        );
        anim.setCycleCount(Animation.INDEFINITE);
        anim.setAutoReverse(true);
        anim.play();

        return pane;
    }

    private VBox buildFormSide() {
        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(40));
        
        VBox card = new VBox(2);
        card.getStyleClass().add("auth-card");
        card.setMaxWidth(500);
        
        Label welcome = new Label("Welcome back");
        welcome.getStyleClass().add("welcome-label");
        
        Label title = new Label("Sign in to your account");
        title.getStyleClass().add("auth-title");
        
        Label sub = new Label("Enter your details to access your workspace.");
        sub.getStyleClass().add("auth-sub");
        VBox.setMargin(sub, new Insets(0, 0, 10, 0));
        
        // Role Selection
        Label roleLabel = new Label("Sign in as");
        roleLabel.getStyleClass().add("field-label");
        
        HBox roleBox = new HBox(12);
        ToggleGroup group = new ToggleGroup();
        String[] roles = {"Company", "Developer", "Evaluator", "Admin"};
        for (String r : roles) {
            ToggleButton tb = new ToggleButton(r);
            tb.getStyleClass().add("role-chip");
            tb.setToggleGroup(group);
            if (r.equals(selectedRole)) tb.setSelected(true);
            roleBox.getChildren().add(tb);
        }
        group.selectedToggleProperty().addListener((obs, old, nv) -> {
            if (nv instanceof ToggleButton tb) selectedRole = tb.getText();
        });
        
        // Username
        Label userLabel = new Label("Username");
        userLabel.getStyleClass().add("field-label");
        HBox userInput = createInputField("\uD83D\uDC64", "alex_kumar");
        TextField uField = (TextField) userInput.getChildren().get(1);

        // Password
        Label passLabel = new Label("Password");
        passLabel.getStyleClass().add("field-label");
        HBox passInput = createPassField("\uD83D\uDD12", "••••••••••••");
        PasswordField pField = (PasswordField) passInput.getChildren().get(1);
        
        // Controls Row
        HBox controls = new HBox(0);
        controls.setAlignment(Pos.CENTER_LEFT);
        CheckBox remember = new CheckBox("Remember me");
        remember.getStyleClass().add("remember-me");
        remember.setSelected(true);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button forgot = new Button("Forgot password?");
        forgot.getStyleClass().add("link-btn");
        forgot.setOnAction(e -> showForgotPasswordDialog());
        controls.getChildren().addAll(remember, spacer, forgot);
        VBox.setMargin(controls, new Insets(16, 0, 24, 0));

        Label errorMsg = new Label();
        errorMsg.getStyleClass().add("error-text");
        errorMsg.setVisible(false);
        errorMsg.setManaged(false);

        Button signIn = new Button("Sign in");
        signIn.getStyleClass().add("primary-btn");
        signIn.setMaxWidth(Double.MAX_VALUE);
        signIn.setOnAction(e -> authenticate(uField, pField, errorMsg));
        
        HBox footer = new HBox(6);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(16, 0, 0, 0));
        Label noAcc = new Label("New here?");
        noAcc.getStyleClass().add("auth-sub");
        Button create = new Button("Create account");
        create.getStyleClass().add("link-btn");
        create.setOnAction(e -> new SignupUI().show(stage));
        footer.getChildren().addAll(noAcc, create);
        
        // Demo
        VBox demoBox = new VBox(8);
        demoBox.getStyleClass().add("demo-divider");
        demoBox.setAlignment(Pos.CENTER);
        Label demoText = new Label("Demo: alex_kumar / password123");
        demoText.getStyleClass().add("demo-text");
        demoBox.getChildren().add(demoText);
        VBox.setMargin(demoBox, new Insets(32, 0, 0, 0));

        card.getChildren().addAll(
            welcome, title, sub, 
            roleLabel, roleBox,
            userLabel, userInput,
            passLabel, passInput,
            controls, errorMsg,
            signIn, footer, demoBox
        );
        
        container.getChildren().add(card);
        return container;
    }

    private HBox createInputField(String iconChar, String prompt) {
        HBox box = new HBox(0);
        box.getStyleClass().add("input-field-container");
        box.setAlignment(Pos.CENTER_LEFT);
        
        Label icon = new Label(iconChar);
        icon.getStyleClass().add("auth-sub");
        icon.setPadding(new Insets(0, 0, 0, 12));
        
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add("text-input");
        HBox.setHgrow(field, Priority.ALWAYS);
        
        box.getChildren().addAll(icon, field);
        return box;
    }

    private HBox createPassField(String iconChar, String prompt) {
        HBox box = new HBox(0);
        box.getStyleClass().add("input-field-container");
        box.setAlignment(Pos.CENTER_LEFT);
        
        Label icon = new Label(iconChar);
        icon.getStyleClass().add("auth-sub");
        icon.setPadding(new Insets(0, 0, 0, 12));
        
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.getStyleClass().add("password-input");
        HBox.setHgrow(field, Priority.ALWAYS);
        
        Label eye = new Label("\uD83D\uDC41"); // Eye icon
        eye.getStyleClass().add("ghost-btn");
        eye.setPadding(new Insets(0, 12, 0, 0));
        
        box.getChildren().addAll(icon, field, eye);
        return box;
    }

    private void authenticate(TextField userField, PasswordField passField, Label errorMsg) {
        String u = userField.getText() == null ? "" : userField.getText().trim();
        String p = passField.getText() == null ? "" : passField.getText();

        if (u.isBlank() || p.isBlank()) {
            showError(errorMsg, "All fields required.");
            return;
        }

        models.User user = UserRepository.getInstance().authenticate(u, p);
        if (user == null) {
            showError(errorMsg, "Invalid credentials.");
            return;
        }
        user.login(p);

        String userRole = DashboardRouter.normalizeRole(user.getRole());
        String selRole = DashboardRouter.normalizeRole(selectedRole);
        if (!selRole.equalsIgnoreCase(userRole)) {
            showError(errorMsg, "Role mismatch (Account: " + userRole + ")");
            return;
        }

        UserSession.getInstance().setCurrentUser(user, userRole);
        DashboardRouter.openDashboard(stage, userRole);
    }
    
    private void showError(Label errorMsg, String msg) {
        errorMsg.setText(msg);
        errorMsg.setVisible(true);
        errorMsg.setManaged(true);
    }

    private void showForgotPasswordDialog() {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Reset Password");
        dialog.setHeaderText("Enter your username to reset your password");
        dialog.setContentText("Username:");
        dialog.showAndWait().ifPresent(username -> {
            if (UserRepository.getInstance().getUserRole(username) != null) {
                String secQ = UserRepository.getInstance().getSecurityQuestion(username);
                if (secQ != null && !secQ.isBlank()) {
                    javafx.scene.control.TextInputDialog secDialog = new javafx.scene.control.TextInputDialog();
                    secDialog.setTitle("Security Question");
                    secDialog.setHeaderText("Please answer the security question to verify your identity.");
                    secDialog.setContentText(secQ + ":");
                    secDialog.showAndWait().ifPresent(answer -> {
                        if (UserRepository.getInstance().verifySecurityAnswer(username, answer)) {
                            javafx.scene.control.TextInputDialog passDialog = new javafx.scene.control.TextInputDialog();
                            passDialog.setTitle("New Password");
                            passDialog.setHeaderText("Enter your new password");
                            passDialog.setContentText("New Password:");
                            passDialog.showAndWait().ifPresent(newPass -> {
                                if (UserRepository.getInstance().updatePassword(username, newPass)) {
                                    FxComponents.showInfo("Success", "Password updated successfully.");
                                }
                            });
                        } else {
                            FxComponents.showError("Error", "Incorrect security answer.");
                        }
                    });
                } else {
                    FxComponents.showError("Error", "No security question set for this user.");
                }
            } else {
                FxComponents.showError("Error", "User not found.");
            }
        });
    }
}
