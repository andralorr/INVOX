package invox.repository;

import invox.database.GenericDao;
import invox.database.RowMapper;
import invox.exception.EntityNotFoundException;
import invox.model.Client;
import invox.model.CompanyClient;
import invox.model.IndividualClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientJdbcRepository implements ClientRepository {

    private final GenericDao dao = GenericDao.getInstance();

    private static final String SELECT =
            "SELECT id, user_id, client_type, email, phone, address, city, county, " +
                    "company_name, cui, trade_register_number, iban, bank_name, " +
                    "first_name, last_name, cnp FROM clients";

    private static final RowMapper<Client> MAPPER = rs -> {
        String type = rs.getString("client_type");
        Client client;
        if ("COMPANY".equals(type)) {
            client = new CompanyClient(
                    rs.getInt("id"), rs.getString("email"), rs.getString("phone"),
                    rs.getString("address"), rs.getString("city"), rs.getString("county"),
                    rs.getString("company_name"), rs.getString("cui"),
                    rs.getString("trade_register_number"), rs.getString("iban"),
                    rs.getString("bank_name"));
        } else {
            client = new IndividualClient(
                    rs.getInt("id"), rs.getString("email"), rs.getString("phone"),
                    rs.getString("address"), rs.getString("city"), rs.getString("county"),
                    rs.getString("first_name"), rs.getString("last_name"), rs.getString("cnp"));
        }
        client.setUserId(rs.getInt("user_id"));
        return client;
    };

    @Override
    public Client add(Client client) {
        int id;
        if (client instanceof CompanyClient c) {
            id = dao.insert(
                    "INSERT INTO clients(user_id, client_type, email, phone, address, city, county, " +
                            "company_name, cui, trade_register_number, iban, bank_name) " +
                            "VALUES (?, 'COMPANY', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    c.getUserId(), c.getEmail(), c.getPhone(), c.getAddress(), c.getCity(), c.getCounty(),
                    c.getCompanyName(), c.getCui(), c.getTradeRegisterNumber(),
                    c.getIban(), c.getBankName());
        } else if (client instanceof IndividualClient i) {
            id = dao.insert(
                    "INSERT INTO clients(user_id, client_type, email, phone, address, city, county, " +
                            "first_name, last_name, cnp) " +
                            "VALUES (?, 'INDIVIDUAL', ?, ?, ?, ?, ?, ?, ?, ?)",
                    i.getUserId(), i.getEmail(), i.getPhone(), i.getAddress(), i.getCity(), i.getCounty(),
                    i.getFirstName(), i.getLastName(), i.getCnp());
        } else {
            throw new IllegalArgumentException("Tip de client necunoscut: " + client);
        }
        client.setId(id);
        return client;
    }

    @Override
    public Optional<Client> findById(int id) {
        return dao.queryOne(SELECT + " WHERE id = ?", MAPPER, id);
    }

    @Override
    public List<Client> findAll() {
        return dao.query(SELECT + " ORDER BY id", MAPPER);
    }

    @Override
    public Client update(Client client) throws EntityNotFoundException {
        int affected;
        if (client instanceof CompanyClient c) {
            affected = dao.update(
                    "UPDATE clients SET email = ?, phone = ?, address = ?, city = ?, county = ?, " +
                            "company_name = ?, cui = ?, trade_register_number = ?, iban = ?, bank_name = ? " +
                            "WHERE id = ?",
                    c.getEmail(), c.getPhone(), c.getAddress(), c.getCity(), c.getCounty(),
                    c.getCompanyName(), c.getCui(), c.getTradeRegisterNumber(),
                    c.getIban(), c.getBankName(), c.getId());
        } else if (client instanceof IndividualClient i) {
            affected = dao.update(
                    "UPDATE clients SET email = ?, phone = ?, address = ?, city = ?, county = ?, " +
                            "first_name = ?, last_name = ?, cnp = ? WHERE id = ?",
                    i.getEmail(), i.getPhone(), i.getAddress(), i.getCity(), i.getCounty(),
                    i.getFirstName(), i.getLastName(), i.getCnp(), i.getId());
        } else {
            throw new IllegalArgumentException("Tip de client necunoscut: " + client);
        }
        if (affected == 0) {
            throw new EntityNotFoundException("Client", client.getId());
        }
        return client;
    }

    @Override
    public void deleteById(int id) throws EntityNotFoundException {
        int affected = dao.update("DELETE FROM clients WHERE id = ?", id);
        if (affected == 0) {
            throw new EntityNotFoundException("Client", id);
        }
    }

    @Override
    public List<Client> findByUser(int userId) {
        return dao.query(SELECT + " WHERE user_id = ? ORDER BY id", MAPPER, userId);
    }

    @Override
    public List<CompanyClient> findCompanies() {
        List<CompanyClient> result = new ArrayList<>();
        for (Client c : dao.query(SELECT + " WHERE client_type = 'COMPANY' ORDER BY id", MAPPER)) {
            result.add((CompanyClient) c);
        }
        return result;
    }

    @Override
    public List<IndividualClient> findIndividuals() {
        List<IndividualClient> result = new ArrayList<>();
        for (Client c : dao.query(SELECT + " WHERE client_type = 'INDIVIDUAL' ORDER BY id", MAPPER)) {
            result.add((IndividualClient) c);
        }
        return result;
    }
}
