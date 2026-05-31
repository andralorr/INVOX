package invox.model;

public class InvoiceItem implements Identifiable {

    private int id;
    private int invoiceId;
    private Product product;
    private int quantity;
    private double unitPrice;
    private double vatRate;
    private double netAmount;    // valoare fara TVA (quantity * unitPrice)
    private double vatAmount;    // valoarea TVA
    private double grossAmount;  // valoare cu TVA (net + TVA)

    public InvoiceItem() {}

    public InvoiceItem(int id, Product product, int quantity,
                       double unitPrice, double vatRate,
                       double netAmount, double vatAmount, double grossAmount) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.vatRate = vatRate;
        this.netAmount = netAmount;
        this.vatAmount = vatAmount;
        this.grossAmount = grossAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getVatRate() {
        return vatRate;
    }

    public void setVatRate(double vatRate) {
        this.vatRate = vatRate;
    }

    public double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(double netAmount) {
        this.netAmount = netAmount;
    }

    public double getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(double vatAmount) {
        this.vatAmount = vatAmount;
    }

    public double getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(double grossAmount) {
        this.grossAmount = grossAmount;
    }

    @Override
    public String toString() {
        return "InvoiceItem{" +
                "id=" + id +
                ", product=" + (product != null ? product.getName() : "null") +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", vatRate=" + vatRate +
                ", netAmount=" + netAmount +
                ", vatAmount=" + vatAmount +
                ", grossAmount=" + grossAmount +
                '}';
    }
}
