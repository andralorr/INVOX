package invox.repository;

import invox.model.Client;
import invox.model.CompanyClient;
import invox.model.IndividualClient;

import java.util.List;

public interface ClientRepository extends Repository<Client> {

    List<CompanyClient> findCompanies();

    List<IndividualClient> findIndividuals();

    List<Client> findByUser(int userId);
}
