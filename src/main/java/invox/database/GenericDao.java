package invox.database;

import invox.exception.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GenericDao {

    private static GenericDao instance;
    private final Database database = Database.getInstance();

    private GenericDao() {
    }

    public static synchronized GenericDao getInstance() {
        if (instance == null) {
            instance = new GenericDao();
        }
        return instance;
    }

    public <T> List<T> query(String sql, RowMapper<T> mapper, Object... params) {
        List<T> result = new ArrayList<>();
        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapper.map(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Eroare la interogare: " + sql, e);
        }
        return result;
    }

    public <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, Object... params) {
        List<T> list = query(sql, mapper, params);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public int update(String sql, Object... params) {
        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, params);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Eroare la executie: " + sql, e);
        }
    }

    public int insert(String sql, Object... params) {
        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"})) {
            bind(ps, params);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new DataAccessException("Eroare la insert: " + sql, e);
        }
    }

    private void bind(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }
}
