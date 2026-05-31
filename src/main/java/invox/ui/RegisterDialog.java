package invox.ui;

import invox.exception.DuplicateEntityException;
import invox.service.AuthService;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RegisterDialog extends Stage {

    private final AuthService authService;

    private final TextField username = new TextField();
    private final PasswordField password = new PasswordField();
    private final TextField companyName = new TextField();
    private final TextField cui = new TextField();
    private final TextField regCom = new TextField();
    private final TextField iban = new TextField();
    private final TextField bank = new TextField();
    private final TextField email = new TextField();
    private final TextField phone = new TextField();
    private final TextField address = new TextField();
    private final TextField city = new TextField();
    private final TextField county = new TextField();

    public RegisterDialog(AuthService authService) {
        this.authService = authService;
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Creeaza cont firma");

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(6);
        grid.setPadding(new Insets(12));
        int r = 0;
        grid.addRow(r++, new Label("Utilizator:"), username);
        grid.addRow(r++, new Label("Parola:"), password);
        grid.addRow(r++, new Label("Denumire firma:"), companyName);
        grid.addRow(r++, new Label("CUI:"), cui);
        grid.addRow(r++, new Label("Reg. Com.:"), regCom);
        grid.addRow(r++, new Label("IBAN:"), iban);
        grid.addRow(r++, new Label("Banca:"), bank);
        grid.addRow(r++, new Label("Email:"), email);
        grid.addRow(r++, new Label("Telefon:"), phone);
        grid.addRow(r++, new Label("Adresa:"), address);
        grid.addRow(r++, new Label("Localitate:"), city);
        grid.addRow(r++, new Label("Judet:"), county);

        Button registerBtn = new Button("Inregistreaza");
        registerBtn.setOnAction(e -> onRegister());
        Button cancelBtn = new Button("Anuleaza");
        cancelBtn.setOnAction(e -> close());

        VBox root = new VBox(10, grid, new HBox(8, registerBtn, cancelBtn));
        root.setPadding(new Insets(12));
        Scene scene = new Scene(root);
        Theme.apply(scene);
        setScene(scene);
    }

    private void onRegister() {
        if (username.getText().trim().isEmpty() || password.getText().isEmpty()
                || companyName.getText().trim().isEmpty() || cui.getText().trim().isEmpty()) {
            Dialogs.error("Utilizator, parola, denumire firma si CUI sunt obligatorii.");
            return;
        }
        try {
            authService.register(username.getText().trim(), password.getText(),
                    companyName.getText().trim(), cui.getText().trim(), regCom.getText().trim(),
                    iban.getText().trim(), bank.getText().trim(), email.getText().trim(),
                    phone.getText().trim(), address.getText().trim(), city.getText().trim(),
                    county.getText().trim());
            Dialogs.info("Succes", "Cont creat. Te poti autentifica acum.");
            close();
        } catch (DuplicateEntityException ex) {
            Dialogs.error(ex.getMessage());
        }
    }
}
