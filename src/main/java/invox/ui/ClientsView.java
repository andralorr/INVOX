package invox.ui;

import invox.model.Client;
import invox.model.CompanyClient;
import invox.model.IndividualClient;
import invox.service.ClientService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ClientsView extends BorderPane {

    private final ClientService clientService;

    private final ObservableList<Client> clients = FXCollections.observableArrayList();
    private final FilteredList<Client> filtered = new FilteredList<>(clients, c -> true);
    private final TableView<Client> table = new TableView<>(filtered);
    private final TextField searchField = new TextField();

    private final ComboBox<String> typeCombo = new ComboBox<>(
            FXCollections.observableArrayList("Firma", "Persoana fizica"));

    // comune
    private final TextField email = new TextField();
    private final TextField phone = new TextField();
    private final TextField address = new TextField();
    private final TextField city = new TextField();
    private final TextField county = new TextField();
    // firma
    private final TextField companyName = new TextField();
    private final TextField cui = new TextField();
    private final TextField regCom = new TextField();
    private final TextField iban = new TextField();
    private final TextField bank = new TextField();
    // persoana
    private final TextField firstName = new TextField();
    private final TextField lastName = new TextField();
    private final TextField cnp = new TextField();

    public ClientsView(ClientService clientService) {
        this.clientService = clientService;
        buildTable();
        searchField.setPromptText("Cauta dupa nume, email sau id...");
        searchField.textProperty().addListener((obs, oldV, newV) -> applyFilter(newV));
        VBox center = new VBox(8, searchField, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        center.setPadding(new Insets(12));
        setCenter(center);
        setRight(buildForm());
        reload();
    }

    private void applyFilter(String query) {
        String s = query == null ? "" : query.trim().toLowerCase();
        filtered.setPredicate(c -> {
            if (s.isEmpty()) {
                return true;
            }
            String name;
            if (c instanceof CompanyClient company) {
                name = company.getCompanyName();
            } else if (c instanceof IndividualClient ind) {
                name = ind.getFirstName() + " " + ind.getLastName();
            } else {
                name = "";
            }
            return (name != null && name.toLowerCase().contains(s))
                    || (c.getEmail() != null && c.getEmail().toLowerCase().contains(s))
                    || String.valueOf(c.getId()).contains(s);
        });
    }

    private void buildTable() {
        addCol("Tip", c -> c instanceof CompanyClient ? "Firma" : "Persoana");
        addCol("Nume / Denumire", this::displayName);
        addCol("Email", Client::getEmail);
        addCol("Localitate", Client::getCity);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private String displayName(Client c) {
        if (c instanceof CompanyClient company) {
            return company.getCompanyName();
        }
        if (c instanceof IndividualClient ind) {
            return ind.getFirstName() + " " + ind.getLastName();
        }
        return "";
    }

    private void addCol(String title, java.util.function.Function<Client, String> getter) {
        TableColumn<Client, String> col = new TableColumn<>(title);
        col.setCellValueFactory(cd -> new SimpleStringProperty(getter.apply(cd.getValue())));
        table.getColumns().add(col);
    }

    private VBox buildForm() {
        typeCombo.getSelectionModel().selectFirst();

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(6);
        grid.setPadding(new Insets(12));
        int r = 0;
        grid.addRow(r++, new Label("Tip:"), typeCombo);
        grid.addRow(r++, new Label("Email:"), email);
        grid.addRow(r++, new Label("Telefon:"), phone);
        grid.addRow(r++, new Label("Adresa:"), address);
        grid.addRow(r++, new Label("Localitate:"), city);
        grid.addRow(r++, new Label("Judet:"), county);
        grid.addRow(r++, new Label("Denumire (firma):"), companyName);
        grid.addRow(r++, new Label("CUI:"), cui);
        grid.addRow(r++, new Label("Reg. Com.:"), regCom);
        grid.addRow(r++, new Label("IBAN:"), iban);
        grid.addRow(r++, new Label("Banca:"), bank);
        grid.addRow(r++, new Label("Prenume (PF):"), firstName);
        grid.addRow(r++, new Label("Nume (PF):"), lastName);
        grid.addRow(r++, new Label("CNP:"), cnp);

        Button addBtn = new Button("Adauga client");
        addBtn.setOnAction(e -> onAdd());
        Button detailBtn = new Button("Detalii / Editeaza selectat");
        detailBtn.setOnAction(e -> onDetails());
        Button deleteBtn = new Button("Sterge selectat");
        deleteBtn.setOnAction(e -> onDelete());

        VBox box = new VBox(10, new Label("Client nou"), grid,
                new HBox(8, addBtn, detailBtn), deleteBtn);
        box.setPadding(new Insets(12));
        box.setPrefWidth(440);
        box.getStyleClass().add("card");
        return box;
    }

    private void onAdd() {
        if (email.getText().trim().isEmpty()) {
            Dialogs.error("Emailul este obligatoriu.");
            return;
        }
        try {
            if ("Firma".equals(typeCombo.getValue())) {
                if (companyName.getText().trim().isEmpty() || cui.getText().trim().isEmpty()) {
                    Dialogs.error("Pentru firma, denumirea si CUI sunt obligatorii.");
                    return;
                }
                clientService.addCompanyClient(email.getText().trim(), phone.getText().trim(),
                        address.getText().trim(), city.getText().trim(), county.getText().trim(),
                        companyName.getText().trim(), cui.getText().trim(), regCom.getText().trim(),
                        iban.getText().trim(), bank.getText().trim());
            } else {
                if (lastName.getText().trim().isEmpty()) {
                    Dialogs.error("Numele persoanei este obligatoriu.");
                    return;
                }
                clientService.addIndividualClient(email.getText().trim(), phone.getText().trim(),
                        address.getText().trim(), city.getText().trim(), county.getText().trim(),
                        firstName.getText().trim(), lastName.getText().trim(), cnp.getText().trim());
            }
            clearForm();
            reload();
        } catch (Exception ex) {
            Dialogs.error(ex.getMessage());
        }
    }

    private void onDetails() {
        Client selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Dialogs.error("Selecteaza un client din lista.");
            return;
        }
        new ClientDetailDialog(selected, clientService).showAndWait();
        reload();
    }

    private void onDelete() {
        Client selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Dialogs.error("Selecteaza un client din lista.");
            return;
        }
        try {
            clientService.deleteClient(selected.getId());
            reload();
        } catch (Exception ex) {
            Dialogs.error("Nu am putut sterge clientul: " + ex.getMessage());
        }
    }

    private void clearForm() {
        for (TextField f : new TextField[]{email, phone, address, city, county,
                companyName, cui, regCom, iban, bank, firstName, lastName, cnp}) {
            f.clear();
        }
    }

    public void reload() {
        clients.setAll(clientService.listClients());
    }
}