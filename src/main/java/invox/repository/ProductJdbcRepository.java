package invox.repository;

import invox.database.GenericDao;
import invox.database.RowMapper;
import invox.exception.EntityNotFoundException;
import invox.model.Category;
import invox.model.Product;

import java.util.List;
import java.util.Optional;

public class ProductJdbcRepository implements Repository<Product> {

    private final GenericDao dao = GenericDao.getInstance();

    private static final String SELECT =
            "SELECT p.id, p.code, p.name, p.unit, p.price, p.vat_rate, p.stock_quantity, " +
            "       p.category_id, c.name AS category_name, c.description AS category_description " +
            "FROM products p LEFT JOIN categories c ON c.id = p.category_id";

    private static final RowMapper<Product> MAPPER = rs -> {
        Category category = null;
        int categoryId = rs.getInt("category_id");
        if (!rs.wasNull()) {
            category = new Category(categoryId,
                    rs.getString("category_name"),
                    rs.getString("category_description"));
        }
        return new Product(
                rs.getInt("id"),
                rs.getString("code"),
                rs.getString("name"),
                rs.getString("unit"),
                rs.getDouble("price"),
                rs.getDouble("vat_rate"),
                rs.getInt("stock_quantity"),
                category);
    };

    @Override
    public Product add(Product product) {
        Integer categoryId = (product.getCategory() != null)
                ? product.getCategory().getId() : null;
        int id = dao.insert(
                "INSERT INTO products(code, name, unit, price, vat_rate, stock_quantity, category_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)",
                product.getCode(), product.getName(), product.getUnit(), product.getPrice(),
                product.getVatRate(), product.getStockQuantity(), categoryId);
        product.setId(id);
        return product;
    }

    @Override
    public Optional<Product> findById(int id) {
        return dao.queryOne(SELECT + " WHERE p.id = ?", MAPPER, id);
    }

    @Override
    public List<Product> findAll() {
        return dao.query(SELECT + " ORDER BY p.id", MAPPER);
    }

    @Override
    public Product update(Product product) throws EntityNotFoundException {
        Integer categoryId = (product.getCategory() != null)
                ? product.getCategory().getId() : null;
        int affected = dao.update(
                "UPDATE products SET code = ?, name = ?, unit = ?, price = ?, vat_rate = ?, " +
                "stock_quantity = ?, category_id = ? WHERE id = ?",
                product.getCode(), product.getName(), product.getUnit(), product.getPrice(),
                product.getVatRate(), product.getStockQuantity(), categoryId, product.getId());
        if (affected == 0) {
            throw new EntityNotFoundException("Product", product.getId());
        }
        return product;
    }

    @Override
    public void deleteById(int id) throws EntityNotFoundException {
        int affected = dao.update("DELETE FROM products WHERE id = ?", id);
        if (affected == 0) {
            throw new EntityNotFoundException("Product", id);
        }
    }

    public Optional<Product> findByCode(String code) {
        return dao.queryOne(SELECT + " WHERE p.code = ?", MAPPER, code);
    }
}
