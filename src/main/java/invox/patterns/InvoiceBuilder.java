package invox.patterns;

import invox.model.Client;
import invox.model.Invoice;
import invox.model.InvoiceItem;
import invox.model.InvoiceStatus;
import invox.model.Product;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InvoiceBuilder {

    private String series;
    private int number;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private Client client;
    private InvoiceStatus status = InvoiceStatus.DRAFT;
    private final List<InvoiceItem> items = new ArrayList<>();

    public InvoiceBuilder series(String series) {
        this.series = series;
        return this;
    }

    public InvoiceBuilder number(int number) {
        this.number = number;
        return this;
    }

    public InvoiceBuilder issueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
        return this;
    }

    public InvoiceBuilder dueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public InvoiceBuilder client(Client client) {
        this.client = client;
        return this;
    }

    public InvoiceBuilder status(InvoiceStatus status) {
        this.status = status;
        return this;
    }

    public InvoiceBuilder addItem(Product product, int quantity) {
        double unitPrice = product.getPrice();
        double vatRate = product.getVatRate();
        double net = unitPrice * quantity;
        double vat = net * vatRate / 100.0;
        double gross = net + vat;

        InvoiceItem item = new InvoiceItem(0, product, quantity,
                unitPrice, vatRate, net, vat, gross);
        items.add(item);
        return this;
    }

    public Invoice build() {
        double totalNet = 0.0;
        double totalVat = 0.0;
        double totalGross = 0.0;
        for (InvoiceItem item : items) {
            totalNet += item.getNetAmount();
            totalVat += item.getVatAmount();
            totalGross += item.getGrossAmount();
        }

        return new Invoice(0, series, number, issueDate, dueDate, client,
                new ArrayList<>(items), totalNet, totalVat, totalGross, status);
    }
}
