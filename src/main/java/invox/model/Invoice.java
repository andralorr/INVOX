package invox.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Invoice implements Identifiable, Comparable<Invoice> {

    private static final Comparator<Invoice> NATURAL_ORDER =
            Comparator.comparing(Invoice::getIssueDate,
                            Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(Invoice::getSeries,
                            Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparingInt(Invoice::getNumber);

    private int id;
    private int userId;               // emitentul (userul logat care a emis factura)
    private String series;            // seria facturii (ex: "INV")
    private int number;               // numarul facturii (ex: 1024)
    private LocalDate issueDate;      // data emiterii
    private LocalDate dueDate;        // data scadenta
    private Client client;            // clientul facturat
    private List<InvoiceItem> items;
    private double totalNet;          // total fara TVA
    private double totalVat;          // total TVA
    private double totalGross;        // total cu TVA
    private InvoiceStatus status;

    public Invoice() {
        this.items = new ArrayList<>();
    }

    public Invoice(int id, String series, int number,
                   LocalDate issueDate, LocalDate dueDate, Client client,
                   List<InvoiceItem> items, double totalNet, double totalVat,
                   double totalGross, InvoiceStatus status) {
        this.id = id;
        this.series = series;
        this.number = number;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.client = client;
        this.items = (items != null) ? items : new ArrayList<>();
        this.totalNet = totalNet;
        this.totalVat = totalVat;
        this.totalGross = totalGross;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
    }

    public double getTotalNet() {
        return totalNet;
    }

    public void setTotalNet(double totalNet) {
        this.totalNet = totalNet;
    }

    public double getTotalVat() {
        return totalVat;
    }

    public void setTotalVat(double totalVat) {
        this.totalVat = totalVat;
    }

    public double getTotalGross() {
        return totalGross;
    }

    public void setTotalGross(double totalGross) {
        this.totalGross = totalGross;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    @Override
    public int compareTo(Invoice other) {
        return NATURAL_ORDER.compare(this, other);
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", series='" + series + '\'' +
                ", number=" + number +
                ", issueDate=" + issueDate +
                ", dueDate=" + dueDate +
                ", client=" + (client != null ? client.getId() : "null") +
                ", items=" + (items != null ? items.size() : 0) + " linii" +
                ", totalNet=" + totalNet +
                ", totalVat=" + totalVat +
                ", totalGross=" + totalGross +
                ", status=" + status +
                '}';
    }
}
