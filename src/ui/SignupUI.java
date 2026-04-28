package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

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
        
        visualSide.prefWidthProperty().bind(shell.widthProperty().multiply(0.4));
        formSide.prefWidthProperty().bind(shell.widthProperty().multiply(0.6));

        shell.getChildren().addAll(visualSide, formSide);
        Scene scene = new Scene(shell, 1280, 820);
        FxStyles.apply(scene);

        return scene;
    }

    private StackPane buildVisualSide() {
        StackPane pane = new StackPane();
        pane.getStyleClass().add("visual-panel");
        
        Circle glow = new Circle(200);
        glow.getStyleClass().add("eclipse-background");
        
        Circle ring = new Circle(170);
        ring.getStyleClass().add("eclipse-ring");
        
        VBox logoContent = new VBox(-3);
        logoContent.setAlignment(Pos.CENTER);
        LogoPanel logo = new LogoPanel(false, 48); // Increased from 40
        logo.setAlignment(Pos.CENTER);
        VBox.setMargin(logo, new Insets(0, 0, 15, 0));
        
        Label sub = new Label("Solve challenges together.");
        sub.getStyleClass().add("auth-sub");
        logoContent.getChildren().addAll(logo, sub);
        
        pane.getChildren().addAll(glow, ring, logoContent);
        pane.setClip(new javafx.scene.shape.Rectangle(1280, 1080));
        return pane;
    }

    private VBox buildFormSide() {
        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(40));
        
        VBox card = new VBox(2);
        card.getStyleClass().add("auth-card");
        card.setMaxWidth(520);
        
        Label welcome = new Label("Get started");
        welcome.getStyleClass().add("welcome-label");
        Label title = new Label("Create your account");
        title.getStyleClass().add("auth-title");
        Label sub = new Label("Join the largest open innovation hub.");
        sub.getStyleClass().add("auth-sub");
        VBox.setMargin(sub, new Insets(0, 0, 10, 0));

        Label roleLabel = new Label("Register as");
        roleLabel.getStyleClass().add("field-label");
        HBox roleBox = new HBox(10);
        ToggleGroup group = new ToggleGroup();
        String[] roles = {"Developer", "Company", "Evaluator"};
        for (String r : roles) {
            ToggleButton tb = new ToggleButton(r);
            tb.getStyleClass().add("role-chip");
            tb.setToggleGroup(group);
            if (r.equals(selectedRole)) tb.setSelected(true);
            roleBox.getChildren().add(tb);
        }
        group.selectedToggleProperty().addListener((obs, old, nv) -> {
            if (nv instanceof ToggleButton tb) {
                selectedRole = tb.getText();
                refreshDynamicLabels();
            }
        });

        VBox userGroup = createInputWithLabel("Username", "john_dev");
        TextField uField = extractTextField(userGroup);
        
        VBox emailGroup = createInputWithLabel("Email Address", "john@example.com");
        TextField eField = extractTextField(emailGroup);

        dynamicFieldLabel1 = new Label();
        dynamicFieldLabel1.getStyleClass().add("field-label");
        dynamicValue1Field = new TextField();
        dynamicValue1Field.getStyleClass().add("text-input");
        dynamicContainer1 = new VBox(0, dynamicFieldLabel1, wrapInput(dynamicValue1Field));

        dynamicFieldLabel2 = new Label();
        dynamicFieldLabel2.getStyleClass().add("field-label");
        dynamicValue2Field = new TextField();
        dynamicValue2Field.getStyleClass().add("text-input");
        dynamicContainer2 = new VBox(0, dynamicFieldLabel2, wrapInput(dynamicValue2Field));

        Label passLabel = new Label("Password");
        passLabel.getStyleClass().add("field-label");
        PasswordField pField = new PasswordField();
        pField.getStyleClass().add("password-input");
        pField.setPromptText("********");
        HBox pWrap = wrapInput(pField);

        refreshDynamicLabels();

        Label errorMsg = new Label();
        errorMsg.getStyleClass().add("error-text");
        errorMsg.setVisible(false);
        errorMsg.setManaged(false);

        Button signupBtn = new Button("Register Account");
        signupBtn.getStyleClass().add("primary-btn");
        signupBtn.setMaxWidth(Double.MAX_VALUE);
        signupBtn.setOnAction(e -> registerUser(uField, eField, pField, errorMsg));
        VBox.setMargin(signupBtn, new Insets(24, 0, 0, 0));

        Button back = new Button("Back to login");
        back.getStyleClass().add("link-btn");
        back.setAlignment(Pos.CENTER);
        back.setMaxWidth(Double.MAX_VALUE);
        back.setOnAction(e -> new LoginUI().show(stage));

        VBox content = new VBox(0, 
            welcome, title, sub,
            roleLabel, roleBox,
            userGroup, emailGroup, dynamicContainer1, dynamicContainer2, 
            passLabel, pWrap, errorMsg, 
            signupBtn, back
        );

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        card.getChildren().add(scroll);
        container.getChildren().add(card);
        return container;
    }

    private VBox createInputWithLabel(String labelText, String prompt) {
        Label l = new Label(labelText);
        l.getStyleClass().add("field-label");
        TextField f = new TextField();
        f.getStyleClass().add("text-input");
        f.setPromptText(prompt);
        return new VBox(0, l, wrapInput(f));
    }
    
    private HBox wrapInput(Node input) {
        HBox box = new HBox(input);
        box.getStyleClass().add("input-field-container");
        HBox.setHgrow(input, Priority.ALWAYS);
        return box;
    }

    private TextField extractTextField(VBox group) {
        HBox box = (HBox) group.getChildren().get(1);
        return (TextField) box.getChildren().get(0);
    }

    private void refreshDynamicLabels() {
        if (dynamicContainer2 == null) return; // Prevent NPE during init

        dynamicContainer2.setVisible(false);
        dynamicContainer2.setManaged(false);
        dynamicContainer1.setVisible(true);
        dynamicContainer1.setManaged(true);
        
        switch (selectedRole) {
            case "Developer" -> {
                dynamicFieldLabel1.setText("Core Skills");
                dynamicValue1Field.setPromptText("Java, Python, AI");
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

    private void registerUser(TextField u, TextField e, PasswordField p, Label err) {
        String username = trim(u.getText());
        String email = trim(e.getText());
        String password = trim(p.getText());

        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            err.setText("All fields required.");
            err.setVisible(true);
            err.setManaged(true);
            return;
        }

        boolean s;
        if ("Company".equals(selectedRole)) {
            s = UserRepository.getInstance().registerCompany(username, email, password, trim(dynamicValue1Field.getText()), trim(dynamicValue2Field.getText()));
        } else {
            s = UserRepository.getInstance().registerUser(username, email, password, selectedRole, trim(dynamicValue1Field.getText()));
        }

        if (!s) {
            err.setText("Username exists.");
            err.setVisible(true);
            err.setManaged(true);
            return;
        }

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText("Account created");
        a.showAndWait();
        new LoginUI().show(stage);
    }

    private String trim(String v) { return v == null ? "" : v.trim(); }
}
