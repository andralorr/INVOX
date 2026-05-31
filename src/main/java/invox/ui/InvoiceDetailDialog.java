package invox.ui;

import invox.model.Client;
import invox.model.CompanyClient;
import invox.model.IndividualClient;
import invox.model.Invoice;
import invox.model.InvoiceItem;
import invox.model.User;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class InvoiceDetailDialog extends Stage {

    public InvoiceDetailDialog(Invoice invoice, User issuer) {
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Factura " + invoice.getSeries() + "-" + invoice.getNumber());

        Label title = new Label("FACTURA  " + invoice.getSeries() + "-" + invoice.getNumber());
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label meta = new Label(
                "Data emiterii: " + text(invoice.getIssueDate())
                        + "    Scadenta: " + text(invoice.getDueDate())
                        + "    Status: " + (invoice.getStatus() != null ? invoice.getStatus().name() : "-"));

        VBox clientBox = party("Client (cumparator)", clientLines(invoice.getClient()));
        VBox issuerBox = party("Furnizor (emitent)", issuerLines(issuer));
        issuerBox.setAlignment(Pos.TOP_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox parties = new HBox(clientBox, spacer, issuerBox);
        parties.setPadding(new Insets(8, 0, 8, 0));

        TableView<InvoiceItem> lines = new TableView<>(
                FXCollections.observableArrayList(invoice.getItems()));
        addCol(lines, "Produs", it -> it.getProduct() != null ? it.getProduct().getName() : "-");
        addCol(lines, "Cant.", it -> String.valueOf(it.getQuantity()));
        addCol(lines, "Pret unitar", it -> String.valueOf(it.getUnitPrice()));
        addCol(lines, "TVA %", it -> String.valueOf(it.getVatRate()));
        addCol(lines, "Valoare net", it -> String.valueOf(it.getNetAmount()));
        addCol(lines, "Valoare TVA", it -> String.valueOf(it.getVatAmount()));
        addCol(lines, "Total", it -> String.valueOf(it.getGrossAmount()));
        lines.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        lines.setPrefHeight(220);

        GridPane totals = new GridPane();
        totals.setHgap(10);
        totals.setVgap(4);
        totals.addRow(0, bold("Total fara TVA:"), new Label(String.valueOf(invoice.getTotalNet())));
        totals.addRow(1, bold("Total TVA:"), new Label(String.valueOf(invoice.getTotalVat())));
        totals.addRow(2, bold("TOTAL DE PLATA:"), new Label(String.valueOf(invoice.getTotalGross())));
        HBox totalsRow = new HBox(totals);
        totalsRow.setAlignment(Pos.CENTER_RIGHT);
        totalsRow.setPadding(new Insets(8, 0, 0, 0));

        Button closeBtn = new Button("Inchide");
        closeBtn.setOnAction(e -> close());
        HBox actions = new HBox(closeBtn);
        actions.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(8, title, meta, parties,
                new Label("Produse / servicii:"), lines, totalsRow, actions);
        root.setPadding(new Insets(16));
        root.setPrefWidth(720);

        Scene scene = new Scene(root);
        Theme.apply(scene);
        setScene(scene);
    }

    private VBox party(String heading, String body) {
        Label h = new Label(heading);
        h.setStyle("-fx-font-weight: bold;");
        Label b = new Label(body);
        VBox box = new VBox(4, h, b);
        box.setPadding(new Insets(8));
        box.getStyleClass().add("card");
        box.setMinWidth(280);
        return box;
    }

    private Label bold(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold;");
        return l;
    }

    private String issuerLines(User u) {
        if (u == null) {
            return "-";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(safe(u.getCompanyName())).append("\n");
        if (notBlank(u.getCui())) sb.append("CUI: ").append(u.getCui()).append("\n");
        if (notBlank(u.getTradeRegisterNumber())) sb.append("Reg. Com.: ").append(u.getTradeRegisterNumber()).append("\n");
        sb.append(addressLine(u.getAddress(), u.getCity(), u.getCounty()));
        if (notBlank(u.getIban())) sb.append("\nIBAN: ").append(u.getIban());
        if (notBlank(u.getBankName())) sb.append("\nBanca: ").append(u.getBankName());
        if (notBlank(u.getEmail())) sb.append("\n").append(u.getEmail());
        return sb.toString();
    }

    private String clientLines(Client c) {
        if (c == null) {
            return "-";
        }
        StringBuilder sb = new StringBuilder();
        if (c instanceof CompanyClient company) {
            sb.append(safe(company.getCompanyName())).append("\n");
            if (notBlank(company.getCui())) sb.append("CUI: ").append(company.getCui()).append("\n");
            if (notBlank(company.getTradeRegisterNumber())) sb.append("Reg. Com.: ").append(company.getTradeRegisterNumber()).append("\n");
        } else if (c instanceof IndividualClient ind) {
            sb.append(safe(ind.getFirstName())).append(" ").append(safe(ind.getLastName())).append("\n");
            if (notBlank(ind.getCnp())) sb.append("CNP: ").append(ind.getCnp()).append("\n");
        }
        sb.append(addressLine(c.getAddress(), c.getCity(), c.getCounty()));
        if (notBlank(c.getEmail())) sb.append("\n").append(c.getEmail());
        return sb.toString();
    }

    private String addressLine(String address, String city, String county) {
        StringBuilder sb = new StringBuilder();
        if (notBlank(address)) sb.append(address);
        if (notBlank(city)) sb.append(sb.length() > 0 ? ", " : "").append(city);
        if (notBlank(county)) sb.append(sb.length() > 0 ? ", " : "").append("jud. ").append(county);
        return sb.toString();
    }

    private boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    private String safe(String s) {
        return s != null ? s : "";
    }

    private void addCol(TableView<InvoiceItem> table, String title,
                        java.util.function.Function<InvoiceItem, String> getter) {
        TableColumn<InvoiceItem, String> col = new TableColumn<>(title);
        col.setCellValueFactory(cd -> new SimpleStringProperty(getter.apply(cd.getValue())));
        table.getColumns().add(col);
    }

    private String text(Object value) {
        return value != null ? value.toString() : "-";
    }
}
