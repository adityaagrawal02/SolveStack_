package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/*
 FULL DB CONNECTED SIGNUP UI
 Uses JDBC UserRepository methods
*/

public class SignupUI {

    private String selectedRole = "Developer";
    private Stage stage;

    private Label dynamicFieldLabel1;
    private Label dynamicFieldLabel2;

    private TextField dynamicValue1Field;
    private TextField dynamicValue2Field;

    private VBox dynamicContainer1;
    private VBox dynamicContainer2;

    public void show(Stage stage) {
        this.stage = stage;
        FxSolveStackApp.setPrimaryStage(stage);
        stage.setTitle("SolveStack - Create Account");
        stage.setScene(buildScene());
        stage.show();
    }

    public void setVisible(boolean visible) {

        if (!visible) {
            dispose();
            return;
        }

        Runnable task = () -> {
            Stage target = stage != null ? stage : new Stage();
            show(target);
        };

        if (Platform.isFxApplicationThread()) {
            task.run();
        } else {
            Platform.runLater(task);
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

        StackPane left = buildVisualSide();
        VBox right = buildFormSide();

        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);

        left.prefWidthProperty().bind(shell.widthProperty().multiply(0.40));
        right.prefWidthProperty().bind(shell.widthProperty().multiply(0.60));

        shell.getChildren().addAll(left, right);

        Scene scene = new Scene(shell, 1280, 820);
        FxStyles.apply(scene);

        return scene;
    }

    private StackPane buildVisualSide() {

        StackPane pane = new StackPane();
        pane.getStyleClass().add("visual-panel");

        Circle glow = new Circle(210);
        glow.getStyleClass().add("eclipse-background");

        Circle ring = new Circle(170);
        ring.getStyleClass().add("eclipse-ring");

        VBox logoBox = new VBox(-2);
        logoBox.setAlignment(Pos.CENTER);

        LogoPanel logo = new LogoPanel(false, 48);

        Label sub = new Label("Create your future.");
        sub.getStyleClass().add("auth-sub");

        logoBox.getChildren().addAll(logo, sub);

        pane.getChildren().addAll(glow, ring, logoBox);

        return pane;
    }

    private VBox buildFormSide() {

        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        VBox card = new VBox(2);
        card.getStyleClass().add("auth-card");
        card.setMaxWidth(520);

        Label top = new Label("Get started");
        top.getStyleClass().add("welcome-label");

        Label title = new Label("Create your account");
        title.getStyleClass().add("auth-title");

        Label sub = new Label("Connected to MySQL Database");
        sub.getStyleClass().add("auth-sub");

        Label roleLabel = new Label("Register as");
        roleLabel.getStyleClass().add("field-label");

        HBox roleBox = new HBox(10);

        ToggleGroup group = new ToggleGroup();

        String[] roles = {"Developer", "Company", "Evaluator"};

        for (String r : roles) {

            ToggleButton tb = new ToggleButton(r);
            tb.getStyleClass().add("role-chip");
            tb.setToggleGroup(group);

            if (r.equals(selectedRole)) {
                tb.setSelected(true);
            }

            roleBox.getChildren().add(tb);
        }

        group.selectedToggleProperty().addListener((obs, old, val) -> {

            if (val instanceof ToggleButton btn) {
                selectedRole = btn.getText();
                refreshDynamicLabels();
            }
        });

        VBox userGroup = createInputWithLabel("Username", "Enter username");
        TextField usernameField = extractTextField(userGroup);

        VBox emailGroup = createInputWithLabel("Email", "Enter email");
        TextField emailField = extractTextField(emailGroup);

        dynamicFieldLabel1 = new Label();
        dynamicFieldLabel1.getStyleClass().add("field-label");

        dynamicValue1Field = new TextField();
        dynamicValue1Field.getStyleClass().add("text-input");

        dynamicContainer1 =
                new VBox(0, dynamicFieldLabel1, wrapInput(dynamicValue1Field));

        dynamicFieldLabel2 = new Label();
        dynamicFieldLabel2.getStyleClass().add("field-label");

        dynamicValue2Field = new TextField();
        dynamicValue2Field.getStyleClass().add("text-input");

        dynamicContainer2 =
                new VBox(0, dynamicFieldLabel2, wrapInput(dynamicValue2Field));

        Label passLabel = new Label("Password");
        passLabel.getStyleClass().add("field-label");

        PasswordField passwordField = new PasswordField();
        passwordField.getStyleClass().add("password-input");
        passwordField.setPromptText("Enter password");

        HBox passWrap = wrapInput(passwordField);

        VBox secQGroup =
                createInputWithLabel(
                        "Security Question",
                        "What is your pet's name?"
                );

        TextField secQField = extractTextField(secQGroup);

        VBox secAGroup =
                createInputWithLabel(
                        "Security Answer",
                        "Fluffy"
                );

        TextField secAField = extractTextField(secAGroup);

        refreshDynamicLabels();

        Label error = new Label();
        error.getStyleClass().add("error-text");
        error.setVisible(false);
        error.setManaged(false);

        Button register = new Button("Register Account");
        register.getStyleClass().add("primary-btn");
        register.setMaxWidth(Double.MAX_VALUE);

        register.setOnAction(e ->
                registerUser(
                        usernameField,
                        emailField,
                        passwordField,
                        secQField,
                        secAField,
                        error
                )
        );

        Button back = new Button("Back to login");
        back.getStyleClass().add("link-btn");
        back.setMaxWidth(Double.MAX_VALUE);
        back.setOnAction(e -> new LoginUI().show(stage));

        VBox content = new VBox(
                0,
                top, title, sub,
                roleLabel, roleBox,
                userGroup,
                emailGroup,
                dynamicContainer1,
                dynamicContainer2,
                passLabel, passWrap,
                secQGroup,
                secAGroup,
                error,
                register,
                back
        );

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        card.getChildren().add(scroll);
        root.getChildren().add(card);

        return root;
    }

    private VBox createInputWithLabel(String label, String prompt) {

        Label l = new Label(label);
        l.getStyleClass().add("field-label");

        TextField field = new TextField();
        field.getStyleClass().add("text-input");
        field.setPromptText(prompt);

        return new VBox(0, l, wrapInput(field));
    }

    private HBox wrapInput(Node node) {

        HBox box = new HBox(node);
        box.getStyleClass().add("input-field-container");

        HBox.setHgrow(node, Priority.ALWAYS);

        return box;
    }

    private TextField extractTextField(VBox group) {

        HBox box = (HBox) group.getChildren().get(1);

        return (TextField) box.getChildren().get(0);
    }

    private void refreshDynamicLabels() {

        if (dynamicContainer2 == null) return;

        dynamicContainer2.setVisible(false);
        dynamicContainer2.setManaged(false);

        switch (selectedRole) {

            case "Developer" -> {
                dynamicFieldLabel1.setText("Core Skills");
                dynamicValue1Field.setPromptText("Java, Python");
            }

            case "Company" -> {
                dynamicFieldLabel1.setText("Company Name");
                dynamicValue1Field.setPromptText("Acme Corp");

                dynamicFieldLabel2.setText("Industry");
                dynamicValue2Field.setPromptText("Technology");

                dynamicContainer2.setVisible(true);
                dynamicContainer2.setManaged(true);
            }

            case "Evaluator" -> {
                dynamicFieldLabel1.setText("Expertise");
                dynamicValue1Field.setPromptText("Cybersecurity");
            }
        }
    }

    private void registerUser(TextField u,
                              TextField e,
                              PasswordField p,
                              TextField secQ,
                              TextField secA,
                              Label err) {

        String username = trim(u.getText());
        String email = trim(e.getText());
        String password = trim(p.getText());
        String q = trim(secQ.getText());
        String a = trim(secA.getText());

        if (username.isBlank() ||
                email.isBlank() ||
                password.isBlank()) {

            showError(err, "All required fields must be filled.");
            return;
        }

        boolean success = false;

        try {

            if ("Company".equals(selectedRole)) {

                success =
                        UserRepository.getInstance()
                                .registerCompany(
                                        username,
                                        email,
                                        password,
                                        trim(dynamicValue1Field.getText()),
                                        trim(dynamicValue2Field.getText()),
                                        q,
                                        a
                                );

            } else {

                success =
                        UserRepository.getInstance()
                                .registerUser(
                                        username,
                                        email,
                                        password,
                                        selectedRole,
                                        trim(dynamicValue1Field.getText()),
                                        q,
                                        a
                                );
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            showError(err, "Database error occurred.");
            return;
        }

        if (!success) {
            showError(err, "Registration failed. Username may already exist.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Account Created Successfully");
        alert.setContentText("You may now sign in.");
        alert.showAndWait();

        new LoginUI().show(stage);
    }

    private void showError(Label err, String msg) {
        err.setText(msg);
        err.setVisible(true);
        err.setManaged(true);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}