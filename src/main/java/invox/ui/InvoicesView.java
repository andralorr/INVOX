package invox.ui;

import invox.exception.EntityNotFoundException;
import invox.exception.InsufficientStockException;
import invox.exception.InvoxException;
import invox.model.Client;
import invox.model.CompanyClient;
import invox.model.IndividualClient;
import invox.model.Invoice;
import invox.model.Product;
import invox.model.User;
import invox.service.ClientService;
import invox.service.InvoiceService;
import invox.service.ProductService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class InvoicesView extends BorderPane {

    private final InvoiceService invoiceService;
    private final ClientService clientService;
    private final ProductService productService;
    private final User currentUser;

    private final ObservableList<Invoice> invoices = FXCollections.observableArrayList();
    private final TableView<Invoice> table = new TableView<>(invoices);

    private final ComboBox<Client> clientCombo = new ComboBox<>();
    private final TextField seriesField = new TextField("INV");
    private final TextField numberField = new TextField();

    private final ComboBox<Product> productCombo = new ComboBox<>();
    private final TextField qtyField = new TextField();

    private final ObservableList<CartLine> cart = FXCollections.observableArrayList();
    private final TableView<CartLine> cartTable = new TableView<>(cart);

    public static class CartLine {
        final Product product;
        final int quantity;
        CartLine(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }

    public InvoicesView(InvoiceService invoiceService, ClientService clientService,
                        ProductService productService, User currentUser) {
        this.invoiceService = invoiceService;
        this.clientService = clientService;
        this.productService = productService;
        this.currentUser = currentUser;
        buildTable();
        setCenter(table);
        setRight(buildForm());
        setBottom(buildActions());
        reload();
    }

    private void buildTable() {
        addCol("Serie-Nr", inv -> inv.getSeries() + "-" + inv.getNumber());
        addCol("Client", inv -> clientName(inv.getClient()));
        addCol("Total", inv -> String.valueOf(inv.getTotalGross()));
        addCol("Status", inv -> inv.getStatus() != null ? inv.getStatus().name() : "");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void addCol(String title, java.util.function.Function<Invoice, String> getter) {
        TableColumn<Invoice, String> col = new TableColumn<>(title);
        col.setCellValueFactory(cd -> new SimpleStringProperty(getter.apply(cd.getValue())));
        table.getColumns().add(col);
    }

    private String clientName(Client c) {
        if (c instanceof CompanyClient company) return company.getCompanyName();
        if (c instanceof IndividualClient ind) return ind.getFirstName() + " " + ind.getLastName();
        return "";
    }

    private VBox buildForm() {
        clientCombo.setCellFactory(cb -> clientCell());
        clientCombo.setButtonCell(clientCell());
        productCombo.setCellFactory(cb -> productCell());
        productCombo.setButtonCell(productCell());

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(6);
        grid.setPadding(new Insets(12));
        grid.addRow(0, new Label("Client:"), clientCombo);
        grid.addRow(1, new Label("Serie:"), seriesField);
        grid.addRow(2, new Label("Numar:"), numberField);
        grid.addRow(3, new Label("Produs:"), productCombo);
        grid.addRow(4, new Label("Cantitate:"), qtyField);

        Button addLineBtn = new Button("Adauga linie");
        addLineBtn.setOnAction(e -> onAddLine());

        TableColumn<CartLine, String> pCol = new TableColumn<>("Produs");
        pCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().product.getName()));
        TableColumn<CartLine, String> qCol = new TableColumn<>("Cant.");
        qCol.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().quantity)));
        TableColumn<CartLine, String> tCol = new TableColumn<>("Total linie");
        tCol.setCellValueFactory(cd -> new SimpleStringProperty(
                String.valueOf(cd.getValue().product.getPrice() * cd.getValue().quantity)));
        cartTable.getColumns().add(pCol);
        cartTable.getColumns().add(qCol);
        cartTable.getColumns().add(tCol);
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        cartTable.setPrefHeight(160);

        Button removeLineBtn = new Button("Sterge linie selectata");
        removeLineBtn.setOnAction(e -> {
            CartLine sel = cartTable.getSelectionModel().getSelectedItem();
            if (sel == null) {
                Dialogs.error("Selecteaza o linie din cos.");
                return;
            }
            cart.remove(sel);
        });

        Button issueBtn = new Button("Emite factura");
        issueBtn.setOnAction(e -> onIssue());

        VBox box = new VBox(10, new Label("Factura noua"), grid, addLineBtn,
                new Label("Linii:"), cartTable, removeLineBtn, issueBtn);
        box.setPadding(new Insets(12));
        box.setPrefWidth(380);
        box.getStyleClass().add("card");
        return box;
    }

    private HBox buildActions() {
        Button detailsBtn = new Button("Detalii factura");
        detailsBtn.setOnAction(e -> onDetails());
        Button paidBtn = new Button("Marcheaza platita");
        paidBtn.setOnAction(e -> onStatusAction("paid"));
        Button cancelBtn = new Button("Anuleaza");
        cancelBtn.setOnAction(e -> onStatusAction("cancel"));
        Button deleteBtn = new Button("Sterge");
        deleteBtn.setOnAction(e -> onStatusAction("delete"));
        HBox box = new HBox(8, detailsBtn, paidBtn, cancelBtn, deleteBtn);
        box.setPadding(new Insets(12));
        return box;
    }

    private void onDetails() {
        Invoice selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Dialogs.error("Selecteaza o factura.");
            return;
        }
        new InvoiceDetailDialog(selected, currentUser).showAndWait();
    }

    private ListCell<Client> clientCell() {
        return new ListCell<>() {
            @Override protected void updateItem(Client c, boolean empty) {
                super.updateItem(c, empty);
                setText((empty || c == null) ? null : clientName(c));
            }
        };
    }

    private ListCell<Product> productCell() {
        return new ListCell<>() {
            @Override protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                setText((empty || p == null) ? null : p.getName() + " (stoc " + p.getStockQuantity() + ")");
            }
        };
    }

    private void onAddLine() {
        Product product = productCombo.getValue();
        if (product == null) {
            Dialogs.error("Alege un produs.");
            return;
        }
        try {
            int qty = Integer.parseInt(qtyField.getText().trim());
            if (qty <= 0) {
                Dialogs.error("Cantitatea trebuie sa fie pozitiva.");
                return;
            }
            cart.add(new CartLine(product, qty));
            qtyField.clear();
        } catch (NumberFormatException ex) {
            Dialogs.error("Cantitatea trebuie sa fie un numar.");
        }
    }

    private void onIssue() {
        Client client = clientCombo.getValue();
        if (client == null) {
            Dialogs.error("Alege un client.");
            return;
        }
        if (cart.isEmpty()) {
            Dialogs.error("Adauga cel putin o linie.");
            return;
        }
        int number;
        try {
            number = Integer.parseInt(numberField.getText().trim());
        } catch (NumberFormatException ex) {
            Dialogs.error("Numarul facturii trebuie sa fie un numar.");
            return;
        }
        Map<Product, Integer> lines = new LinkedHashMap<>();
        for (CartLine line : cart) {
            lines.merge(line.product, line.quantity, Integer::sum);
        }
        try {
            invoiceService.issueInvoice(currentUser.getId(), client, seriesField.getText().trim(),
                    number, LocalDate.now().plusDays(15), lines);
            cart.clear();
            numberField.clear();
            reload();
            Dialogs.info("Succes", "Factura a fost emisa.");
        } catch (InsufficientStockException | EntityNotFoundException ex) {
            Dialogs.error(ex.getMessage());
        }
    }

    private void onStatusAction(String action) {
        Invoice selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Dialogs.error("Selecteaza o factura.");
            return;
        }
        try {
            switch (action) {
                case "paid" -> invoiceService.markAsPaid(selected.getId());
                case "cancel" -> invoiceService.cancelInvoice(selected.getId());
                case "delete" -> invoiceService.deleteInvoice(selected.getId());
                default -> { }
            }
            reload();
        } catch (InvoxException ex) {
            Dialogs.error(ex.getMessage());
        }
    }

    public void reload() {
        invoices.setAll(invoiceService.listInvoicesByUser(currentUser.getId()));
        clientCombo.setItems(FXCollections.observableArrayList(clientService.listClients()));
        productCombo.setItems(FXCollections.observableArrayList(productService.listProducts()));
    }
}
