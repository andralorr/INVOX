package invox.repository;

import invox.database.GenericDao;
import invox.database.RowMapper;
import invox.exception.EntityNotFoundException;
import invox.model.InvoiceItem;
import invox.model.Product;

import java.util.List;
import java.util.Optional;

public class InvoiceItemJdbcRepository implements Repository<InvoiceItem> {

    private final GenericDao dao = GenericDao.getInstance();

    private static final String SELECT =
            "SELECT it.id, it.invoice_id, it.product_id, it.quantity, it.unit_price, " +
            "       it.vat_rate, it.net_amount, it.vat_amount, it.gross_amount, " +
            "       p.code AS p_code, p.name AS p_name " +
            "FROM invoice_items it LEFT JOIN products p ON p.id = it.product_id";

    private static final RowMapper<InvoiceItem> MAPPER = rs -> {
        Product product = new Product();
        product.setId(rs.getInt("product_id"));
        product.setCode(rs.getString("p_code"));
        product.setName(rs.getString("p_name"));

        InvoiceItem item = new InvoiceItem();
        item.setId(rs.getInt("id"));
        item.setInvoiceId(rs.getInt("invoice_id"));
        item.setProduct(product);
        item.setQuantity(rs.getInt("quantity"));
        item.setUnitPrice(rs.getDouble("unit_price"));
        item.setVatRate(rs.getDouble("vat_rate"));
        item.setNetAmount(rs.getDouble("net_amount"));
        item.setVatAmount(rs.getDouble("vat_amount"));
        item.setGrossAmount(rs.getDouble("gross_amount"));
        return item;
    };

    @Override
    public InvoiceItem add(InvoiceItem item) {
        int id = dao.insert(
                "INSERT INTO invoice_items(invoice_id, product_id, quantity, unit_price, " +
                "vat_rate, net_amount, vat_amount, gross_amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                item.getInvoiceId(), item.getProduct().getId(), item.getQuantity(),
                item.getUnitPrice(), item.getVatRate(), item.getNetAmount(),
                item.getVatAmount(), item.getGrossAmount());
        item.setId(id);
        return item;
    }

    @Override
    public Optional<InvoiceItem> findById(int id) {
        return dao.queryOne(SELECT + " WHERE it.id = ?", MAPPER, id);
    }

    @Override
    public List<InvoiceItem> findAll() {
        return dao.query(SELECT + " ORDER BY it.id", MAPPER);
    }

    @Override
    public InvoiceItem update(InvoiceItem item) throws EntityNotFoundException {
        int affected = dao.update(
                "UPDATE invoice_items SET quantity = ?, unit_price = ?, vat_rate = ?, " +
                "net_amount = ?, vat_amount = ?, gross_amount = ? WHERE id = ?",
                item.getQuantity(), item.getUnitPrice(), item.getVatRate(),
                item.getNetAmount(), item.getVatAmount(), item.getGrossAmount(), item.getId());
        if (affected == 0) {
            throw new EntityNotFoundException("InvoiceItem", item.getId());
        }
        return item;
    }

    @Override
    public void deleteById(int id) throws EntityNotFoundException {
        int affected = dao.update("DELETE FROM invoice_items WHERE id = ?", id);
        if (affected == 0) {
            throw new EntityNotFoundException("InvoiceItem", id);
        }
    }

    public List<InvoiceItem> findByInvoice(int invoiceId) {
        return dao.query(SELECT + " WHERE it.invoice_id = ? ORDER BY it.id", MAPPER, invoiceId);
    }
}
