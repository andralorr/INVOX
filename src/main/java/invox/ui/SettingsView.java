package invox.ui;

import invox.model.User;
import invox.service.AuthService;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class SettingsView extends BorderPane {

    private final AuthService authService;
    private final User currentUser;

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
    private final PasswordField newPassword = new PasswordField();

    public SettingsView(User currentUser, AuthService authService) {
        this.currentUser = currentUser;
        this.authService = authService;
        setCenter(buildForm());
        loadFromUser();
    }

    private VBox buildForm() {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(6);
        grid.setPadding(new Insets(12));
        int r = 0;
        grid.addRow(r++, new Label("Utilizator:"), new Label(currentUser.getUsername()));
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
        grid.addRow(r++, new Label("Parola noua:"), newPassword);

        Button saveBtn = new Button("Salveaza modificarile");
        saveBtn.setOnAction(e -> onSave());

        VBox box = new VBox(12, new Label("Setarile contului tau"), grid, saveBtn);
        box.setPadding(new Insets(16));
        box.setMaxWidth(460);
        box.getStyleClass().add("card");
        return box;
    }

    private void loadFromUser() {
        companyName.setText(currentUser.getCompanyName());
        cui.setText(currentUser.getCui());
        regCom.setText(currentUser.getTradeRegisterNumber());
        iban.setText(currentUser.getIban());
        bank.setText(currentUser.getBankName());
        email.setText(currentUser.getEmail());
        phone.setText(currentUser.getPhone());
        address.setText(currentUser.getAddress());
        city.setText(currentUser.getCity());
        county.setText(currentUser.getCounty());
    }

    private void onSave() {
        if (companyName.getText().trim().isEmpty()) {
            Dialogs.error("Denumirea firmei este obligatorie.");
            return;
        }
        currentUser.setCompanyName(companyName.getText().trim());
        currentUser.setCui(cui.getText().trim());
        currentUser.setTradeRegisterNumber(regCom.getText().trim());
        currentUser.setIban(iban.getText().trim());
        currentUser.setBankName(bank.getText().trim());
        currentUser.setEmail(email.getText().trim());
        currentUser.setPhone(phone.getText().trim());
        currentUser.setAddress(address.getText().trim());
        currentUser.setCity(city.getText().trim());
        currentUser.setCounty(county.getText().trim());
        try {
            authService.updateAccount(currentUser);
            if (!newPassword.getText().isEmpty()) {
                authService.changePassword(currentUser, newPassword.getText());
                newPassword.clear();
            }
            Dialogs.info("Salvat", "Datele contului au fost actualizate.");
        } catch (Exception ex) {
            Dialogs.error(ex.getMessage());
        }
    }

    public void reload() {
        loadFromUser();
    }
}
