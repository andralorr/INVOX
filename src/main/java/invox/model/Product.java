package invox.model;

public class Product implements Identifiable {

    private int id;
    private String code;          // cod produs / SKU (ex: PRD-001)
    private String name;
    private String unit;          // unitate de masura: buc, kg, l, ora
    private double price;         // pret unitar fara TVA
    private double vatRate;       // cota TVA aplicata: 19, 9, 5, 0
    private int stockQuantity;
    private Category category;
    private int userId;

    public Product() {}

    public Product(int id, String code, String name, String unit,
                   double price, double vatRate, int stockQuantity,
                   Category category) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.unit = unit;
        this.price = price;
        this.vatRate = vatRate;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getVatRate() {
        return vatRate;
    }

    public void setVatRate(double vatRate) {
        this.vatRate = vatRate;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", price=" + price +
                ", vatRate=" + vatRate +
                ", stockQuantity=" + stockQuantity +
                ", category=" + (category != null ? category.getName() : "null") +
                '}';
    }
}
