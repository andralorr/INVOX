package invox.model;

public class CompanyClient extends Client {

    private String companyName;
    private String cui;                  // CUI / CIF (cod fiscal)
    private String tradeRegisterNumber;  // nr. Registrul Comertului (J40/.../...)
    private String iban;
    private String bankName;

    public CompanyClient() {
        super();
    }

    public CompanyClient(int id, String email, String phone,
                         String address, String city, String county,
                         String companyName, String cui,
                         String tradeRegisterNumber, String iban,
                         String bankName) {
        super(id, email, phone, address, city, county);
        this.companyName = companyName;
        this.cui = cui;
        this.tradeRegisterNumber = tradeRegisterNumber;
        this.iban = iban;
        this.bankName = bankName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCui() {
        return cui;
    }

    public void setCui(String cui) {
        this.cui = cui;
    }

    public String getTradeRegisterNumber() {
        return tradeRegisterNumber;
    }

    public void setTradeRegisterNumber(String tradeRegisterNumber) {
        this.tradeRegisterNumber = tradeRegisterNumber;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @Override
    public String toString() {
        return "CompanyClient{" +
                super.toString() +
                ", companyName='" + companyName + '\'' +
                ", cui='" + cui + '\'' +
                ", tradeRegisterNumber='" + tradeRegisterNumber + '\'' +
                ", iban='" + iban + '\'' +
                ", bankName='" + bankName + '\'' +
                '}';
    }
}
