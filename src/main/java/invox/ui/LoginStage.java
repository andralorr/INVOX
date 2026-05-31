package invox.ui;

import invox.exception.AuthenticationException;
import invox.model.User;
import invox.service.AuthService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginStage extends Stage {

    private final AuthService authService;
    private User loggedUser;

    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();

    public LoginStage(AuthService authService) {
        this.authService = authService;
        setTitle("INVOX - Autentificare");

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.addRow(0, new Label("Utilizator:"), usernameField);
        grid.addRow(1, new Label("Parola:"), passwordField);

        Button loginBtn = new Button("Autentificare");
        loginBtn.setOnAction(e -> onLogin());
        Button registerBtn = new Button("Creeaza cont");
        registerBtn.setOnAction(e -> new RegisterDialog(authService).showAndWait());

        VBox root = new VBox(14,
                new Label("Bine ai venit in INVOX"),
                grid,
                new HBox(8, loginBtn, registerBtn));
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(24));
        root.getStyleClass().add("card");

        Scene scene = new Scene(root, 380, 240);
        Theme.apply(scene);
        setScene(scene);
    }

    private void onLogin() {
        try {
            loggedUser = authService.login(usernameField.getText().trim(),
                    passwordField.getText());
            close();
        } catch (AuthenticationException ex) {
            Dialogs.error(ex.getMessage());
        }
    }

    public User getLoggedUser() {
        return loggedUser;
    }
}
