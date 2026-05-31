package invox.repository;

import invox.database.GenericDao;
import invox.database.RowMapper;
import invox.exception.EntityNotFoundException;
import invox.model.User;

import java.util.List;
import java.util.Optional;

public class UserJdbcRepository implements UserRepository {

    private final GenericDao dao = GenericDao.getInstance();

    private static final String SELECT =
            "SELECT id, username, password_hash, company_name, cui, trade_register_number, " +
            "iban, bank_name, email, phone, address, city, county FROM users";

    private static final RowMapper<User> MAPPER = rs -> new User(
            rs.getInt("id"), rs.getString("username"), rs.getString("password_hash"),
            rs.getString("company_name"), rs.getString("cui"), rs.getString("trade_register_number"),
            rs.getString("iban"), rs.getString("bank_name"), rs.getString("email"),
            rs.getString("phone"), rs.getString("address"), rs.getString("city"), rs.getString("county"));

    @Override
    public User add(User user) {
        int id = dao.insert(
                "INSERT INTO users(username, password_hash, company_name, cui, trade_register_number, " +
                "iban, bank_name, email, phone, address, city, county) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                user.getUsername(), user.getPasswordHash(), user.getCompanyName(), user.getCui(),
                user.getTradeRegisterNumber(), user.getIban(), user.getBankName(), user.getEmail(),
                user.getPhone(), user.getAddress(), user.getCity(), user.getCounty());
        user.setId(id);
        return user;
    }

    @Override
    public Optional<User> findById(int id) {
        return dao.queryOne(SELECT + " WHERE id = ?", MAPPER, id);
    }

    @Override
    public List<User> findAll() {
        return dao.query(SELECT + " ORDER BY id", MAPPER);
    }

    @Override
    public User update(User user) throws EntityNotFoundException {
        int affected = dao.update(
                "UPDATE users SET username = ?, password_hash = ?, company_name = ?, cui = ?, " +
                "trade_register_number = ?, iban = ?, bank_name = ?, email = ?, phone = ?, " +
                "address = ?, city = ?, county = ? WHERE id = ?",
                user.getUsername(), user.getPasswordHash(), user.getCompanyName(), user.getCui(),
                user.getTradeRegisterNumber(), user.getIban(), user.getBankName(), user.getEmail(),
                user.getPhone(), user.getAddress(), user.getCity(), user.getCounty(), user.getId());
        if (affected == 0) {
            throw new EntityNotFoundException("User", user.getId());
        }
        return user;
    }

    @Override
    public void deleteById(int id) throws EntityNotFoundException {
        int affected = dao.update("DELETE FROM users WHERE id = ?", id);
        if (affected == 0) {
            throw new EntityNotFoundException("User", id);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return dao.queryOne(SELECT + " WHERE username = ?", MAPPER, username);
    }
}
