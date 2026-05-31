package invox.patterns;

import invox.model.CompanyClient;
import invox.model.IndividualClient;

public class ClientFactory {

    public enum ClientType {
        COMPANY,      // persoana juridica
        INDIVIDUAL    // persoana fizica
    }

    public CompanyClient createCompany(String email, String phone, String address,
                                       String city, String county, String companyName,
                                       String cui, String tradeRegisterNumber,
                                       String iban, String bankName) {
        return new CompanyClient(0, email, phone, address, city, county,
                companyName, cui, tradeRegisterNumber, iban, bankName);
    }

    public IndividualClient createIndividual(String email, String phone, String address,
                                             String city, String county,
                                             String firstName, String lastName, String cnp) {
        return new IndividualClient(0, email, phone, address, city, county,
                firstName, lastName, cnp);
    }
}
