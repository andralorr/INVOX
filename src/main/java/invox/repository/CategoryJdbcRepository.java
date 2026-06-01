package invox.repository;

import invox.database.GenericDao;
import invox.database.RowMapper;
import invox.exception.EntityNotFoundException;
import invox.model.Category;

import java.util.List;
import java.util.Optional;

public class CategoryJdbcRepository implements CategoryRepository {

    private final GenericDao dao = GenericDao.getInstance();

    private static final RowMapper<Category> MAPPER = rs -> {
        Category c = new Category(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"));
        c.setUserId(rs.getInt("user_id"));
        return c;
    };

    @Override
    public Category add(Category category) {
        int id = dao.insert(
                "INSERT INTO categories(user_id, name, description) VALUES (?, ?, ?)",
                category.getUserId(), category.getName(), category.getDescription());
        category.setId(id);
        return category;
    }

    @Override
    public Optional<Category> findById(int id) {
        return dao.queryOne(
                "SELECT id, user_id, name, description FROM categories WHERE id = ?", MAPPER, id);
    }

    @Override
    public List<Category> findAll() {
        return dao.query(
                "SELECT id, user_id, name, description FROM categories ORDER BY id", MAPPER);
    }

    @Override
    public Category update(Category category) throws EntityNotFoundException {
        int affected = dao.update(
                "UPDATE categories SET name = ?, description = ? WHERE id = ?",
                category.getName(), category.getDescription(), category.getId());
        if (affected == 0) {
            throw new EntityNotFoundException("Category", category.getId());
        }
        return category;
    }

    @Override
    public void deleteById(int id) throws EntityNotFoundException {
        int affected = dao.update("DELETE FROM categories WHERE id = ?", id);
        if (affected == 0) {
            throw new EntityNotFoundException("Category", id);
        }
    }

    @Override
    public List<Category> findByUser(int userId) {
        return dao.query(
                "SELECT id, user_id, name, description FROM categories WHERE user_id = ? ORDER BY id",
                MAPPER, userId);
    }
}
