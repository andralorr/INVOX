package invox.ui;

import invox.exception.EntityNotFoundException;
import invox.model.Client;
import invox.model.CompanyClient;
import invox.model.IndividualClient;
import invox.service.ClientService;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ClientDetailDialog extends Stage {

    public ClientDetailDialog(Client client, ClientService clientService) {
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Detalii client #" + client.getId());

        boolean isCompany = client instanceof CompanyClient;

        TextField email = new TextField(client.getEmail());
        TextField phone = new TextField(client.getPhone());
        TextField address = new TextField(client.getAddress());
        TextField city = new TextField(client.getCity());
        TextField county = new TextField(client.getCounty());

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(6);
        grid.setPadding(new Insets(12));
        int r = 0;
        grid.addRow(r++, new Label("Tip:"), new Label(isCompany ? "Firma" : "Persoana fizica"));
        grid.addRow(r++, new Label("Email:"), email);
        grid.addRow(r++, new Label("Telefon:"), phone);
        grid.addRow(r++, new Label("Adresa:"), address);
        grid.addRow(r++, new Label("Localitate:"), city);
        grid.addRow(r++, new Label("Judet:"), county);

        TextField companyName = new TextField();
        TextField cui = new TextField();
        TextField regCom = new TextField();
        TextField iban = new TextField();
        TextField bank = new TextField();
        TextField firstName = new TextField();
        TextField lastName = new TextField();
        TextField cnp = new TextField();

        if (client instanceof CompanyClient c) {
            companyName.setText(c.getCompanyName());
            cui.setText(c.getCui());
            regCom.setText(c.getTradeRegisterNumber());
            iban.setText(c.getIban());
            bank.setText(c.getBankName());
            grid.addRow(r++, new Label("Denumire:"), companyName);
            grid.addRow(r++, new Label("CUI:"), cui);
            grid.addRow(r++, new Label("Reg. Com.:"), regCom);
            grid.addRow(r++, new Label("IBAN:"), iban);
            grid.addRow(r++, new Label("Banca:"), bank);
        } else if (client instanceof IndividualClient i) {
            firstName.setText(i.getFirstName());
            lastName.setText(i.getLastName());
            cnp.setText(i.getCnp());
            grid.addRow(r++, new Label("Prenume:"), firstName);
            grid.addRow(r++, new Label("Nume:"), lastName);
            grid.addRow(r++, new Label("CNP:"), cnp);
        }

        Button saveBtn = new Button("Salveaza");
        saveBtn.setOnAction(e -> {
            client.setEmail(email.getText().trim());
            client.setPhone(phone.getText().trim());
            client.setAddress(address.getText().trim());
            client.setCity(city.getText().trim());
            client.setCounty(county.getText().trim());
            if (client instanceof CompanyClient c) {
                c.setCompanyName(companyName.getText().trim());
                c.setCui(cui.getText().trim());
                c.setTradeRegisterNumber(regCom.getText().trim());
                c.setIban(iban.getText().trim());
                c.setBankName(bank.getText().trim());
            } else if (client instanceof IndividualClient i) {
                i.setFirstName(firstName.getText().trim());
                i.setLastName(lastName.getText().trim());
                i.setCnp(cnp.getText().trim());
            }
            try {
                clientService.updateClient(client);
                close();
            } catch (EntityNotFoundException ex) {
                Dialogs.error(ex.getMessage());
            }
        });
        Button cancelBtn = new Button("Inchide");
        cancelBtn.setOnAction(e -> close());

        VBox root = new VBox(10, grid, new HBox(8, saveBtn, cancelBtn));
        root.setPadding(new Insets(12));
        setScene(new Scene(root));
        Theme.apply(getScene());
    }
}
