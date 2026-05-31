package invox.repository;

import invox.exception.EntityNotFoundException;
import invox.model.Client;
import invox.model.CompanyClient;
import invox.model.IndividualClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryClientRepository implements ClientRepository {

    private final Map<Integer, Client> clients = new HashMap<>();
    private int nextId = 1;

    @Override
    public Client add(Client client) {
        client.setId(nextId++);
        clients.put(client.getId(), client);
        return client;
    }

    @Override
    public Optional<Client> findById(int id) {
        return Optional.ofNullable(clients.get(id));
    }

    @Override
    public List<Client> findAll() {
        return new ArrayList<>(clients.values());
    }

    @Override
    public Client update(Client client) throws EntityNotFoundException {
        if (!clients.containsKey(client.getId())) {
            throw new EntityNotFoundException("Client", client.getId());
        }
        clients.put(client.getId(), client);
        return client;
    }

    @Override
    public void deleteById(int id) throws EntityNotFoundException {
        if (clients.remove(id) == null) {
            throw new EntityNotFoundException("Client", id);
        }
    }

    public List<CompanyClient> findCompanies() {
        List<CompanyClient> result = new ArrayList<>();
        for (Client c : clients.values()) {
            if (c instanceof CompanyClient company) {
                result.add(company);
            }
        }
        return result;
    }

    public List<IndividualClient> findIndividuals() {
        List<IndividualClient> result = new ArrayList<>();
        for (Client c : clients.values()) {
            if (c instanceof IndividualClient individual) {
                result.add(individual);
            }
        }
        return result;
    }
}
