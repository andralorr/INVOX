package invox.model;

public class User implements Identifiable {

    private int id;
    private String username;
    private String passwordHash;
    private String companyName;
    private String cui;
    private String tradeRegisterNumber;
    private String iban;
    private String bankName;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String county;

    public User() {}

    public User(int id, String username, String passwordHash, String companyName,
                String cui, String tradeRegisterNumber, String iban, String bankName,
                String email, String phone, String address, String city, String county) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.companyName = companyName;
        this.cui = cui;
        this.tradeRegisterNumber = tradeRegisterNumber;
        this.iban = iban;
        this.bankName = bankName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.county = county;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", companyName='" + companyName + '\'' +
                ", cui='" + cui + '\'' +
                '}';
    }
}
