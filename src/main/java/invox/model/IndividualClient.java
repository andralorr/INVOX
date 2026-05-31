package invox.model;

public class IndividualClient extends Client {

    private String firstName;
    private String lastName;
    private String cnp;

    public IndividualClient() {
        super();
    }

    public IndividualClient(int id, String email, String phone,
                            String address, String city, String county,
                            String firstName, String lastName, String cnp) {
        super(id, email, phone, address, city, county);
        this.firstName = firstName;
        this.lastName = lastName;
        this.cnp = cnp;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCnp() {
        return cnp;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    @Override
    public String toString() {
        return "IndividualClient{" +
                super.toString() +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", cnp='" + cnp + '\'' +
                '}';
    }
}
