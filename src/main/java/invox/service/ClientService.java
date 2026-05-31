package invox.service;

import invox.exception.DuplicateEntityException;
import invox.exception.EntityNotFoundException;
import invox.model.Client;
import invox.model.CompanyClient;
import invox.model.IndividualClient;
import invox.patterns.ClientFactory;
import invox.repository.ClientRepository;

import java.util.List;

public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientFactory clientFactory = new ClientFactory();
    private final AuditService audit = AuditService.getInstance();

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    private void ensureUniqueEmail(String email) throws DuplicateEntityException {
        if (email == null || email.isBlank()) {
            return;
        }
        for (Client existing : clientRepository.findAll()) {
            if (email.equalsIgnoreCase(existing.getEmail())) {
                throw new DuplicateEntityException(
                        "Exista deja un client cu emailul " + email);
            }
        }
    }

    public CompanyClient addCompanyClient(String email, String phone, String address,
                                          String city, String county, String companyName,
                                          String cui, String tradeRegisterNumber,
                                          String iban, String bankName)
            throws DuplicateEntityException {
        ensureUniqueEmail(email);
        CompanyClient client = clientFactory.createCompany(email, phone, address, city,
                county, companyName, cui, tradeRegisterNumber, iban, bankName);
        clientRepository.add(client);
        audit.log("ADD_COMPANY_CLIENT");
        return client;
    }

    public IndividualClient addIndividualClient(String email, String phone, String address,
                                                String city, String county, String firstName,
                                                String lastName, String cnp)
            throws DuplicateEntityException {
        ensureUniqueEmail(email);
        IndividualClient client = clientFactory.createIndividual(email, phone, address,
                city, county, firstName, lastName, cnp);
        clientRepository.add(client);
        audit.log("ADD_INDIVIDUAL_CLIENT");
        return client;
    }

    public Client updateClient(Client client) throws EntityNotFoundException {
        Client updated = clientRepository.update(client);
        audit.log("UPDATE_CLIENT");
        return updated;
    }

    public void deleteClient(int id) throws EntityNotFoundException {
        clientRepository.deleteById(id);
        audit.log("DELETE_CLIENT");
    }

    public Client getClient(int id) throws EntityNotFoundException {
        return clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client", id));
    }

    public List<Client> listClients() {
        return clientRepository.findAll();
    }

}
