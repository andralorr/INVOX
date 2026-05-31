package invox.model;

public abstract class Client implements Identifiable {

    private int id;
    private String email;
    private String phone;
    private String address;   // strada, numar
    private String city;      // localitate
    private String county;    // judet

    protected Client() {}

    protected Client(int id, String email, String phone,
                     String address, String city, String county) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.county = county;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return "id=" + id +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", county='" + county + '\'';
    }
}
