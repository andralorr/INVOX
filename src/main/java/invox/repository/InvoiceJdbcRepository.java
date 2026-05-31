package invox.repository;

import invox.database.Database;
import invox.database.GenericDao;
import invox.database.RowMapper;
import invox.exception.DataAccessException;
import invox.exception.EntityNotFoundException;
import invox.model.Invoice;
import invox.model.InvoiceItem;
import invox.model.InvoiceStatus;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class InvoiceJdbcRepository implements InvoiceRepository {

    private final GenericDao dao = GenericDao.getInstance();
    private final ClientJdbcRepository clientRepository = new ClientJdbcRepository();
    private final InvoiceItemJdbcRepository itemRepository = new InvoiceItemJdbcRepository();

    private static final String SELECT =
            "SELECT id, user_id, series, number, issue_date, due_date, client_id, " +
            "total_net, total_vat, total_gross, status FROM invoices";

    private final RowMapper<Invoice> mapper = rs -> {
        Invoice inv = new Invoice();
        int id = rs.getInt("id");
        inv.setId(id);
        inv.setUserId(rs.getInt("user_id"));
        inv.setSeries(rs.getString("series"));
        inv.setNumber(rs.getInt("number"));
        Date issue = rs.getDate("issue_date");
        Date due = rs.getDate("due_date");
        inv.setIssueDate(issue != null ? issue.toLocalDate() : null);
        inv.setDueDate(due != null ? due.toLocalDate() : null);
        inv.setTotalNet(rs.getDouble("total_net"));
        inv.setTotalVat(rs.getDouble("total_vat"));
        inv.setTotalGross(rs.getDouble("total_gross"));
        inv.setStatus(InvoiceStatus.valueOf(rs.getString("status")));
        inv.setClient(clientRepository.findById(rs.getInt("client_id")).orElse(null));
        inv.setItems(itemRepository.findByInvoice(id));
        return inv;
    };

    @Override
    public Invoice add(Invoice invoice) {
        Database db = Database.getInstance();
        try (Connection con = db.getConnection()) {
            con.setAutoCommit(false);
            try {
                int invoiceId;
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO invoices(user_id, series, number, issue_date, due_date, client_id, " +
                        "total_net, total_vat, total_gross, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        new String[]{"id"})) {
                    ps.setInt(1, invoice.getUserId());
                    ps.setString(2, invoice.getSeries());
                    ps.setInt(3, invoice.getNumber());
                    ps.setDate(4, invoice.getIssueDate() != null ? Date.valueOf(invoice.getIssueDate()) : null);
                    ps.setDate(5, invoice.getDueDate() != null ? Date.valueOf(invoice.getDueDate()) : null);
                    ps.setInt(6, invoice.getClient().getId());
                    ps.setDouble(7, invoice.getTotalNet());
                    ps.setDouble(8, invoice.getTotalVat());
                    ps.setDouble(9, invoice.getTotalGross());
                    ps.setString(10, invoice.getStatus().name());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        invoiceId = keys.getInt(1);
                    }
                }
                invoice.setId(invoiceId);

                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO invoice_items(invoice_id, product_id, quantity, unit_price, " +
                        "vat_rate, net_amount, vat_amount, gross_amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                    for (InvoiceItem item : invoice.getItems()) {
                        item.setInvoiceId(invoiceId);
                        ps.setInt(1, invoiceId);
                        ps.setInt(2, item.getProduct().getId());
                        ps.setInt(3, item.getQuantity());
                        ps.setDouble(4, item.getUnitPrice());
                        ps.setDouble(5, item.getVatRate());
                        ps.setDouble(6, item.getNetAmount());
                        ps.setDouble(7, item.getVatAmount());
                        ps.setDouble(8, item.getGrossAmount());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                con.commit();
                return invoice;
            } catch (SQLException e) {
                con.rollback();
                throw new DataAccessException("Eroare la salvarea facturii (rollback efectuat)", e);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Eroare de conexiune la salvarea facturii", e);
        }
    }

    @Override
    public Optional<Invoice> findById(int id) {
        return dao.queryOne(SELECT + " WHERE id = ?", mapper, id);
    }

    @Override
    public List<Invoice> findAll() {
        return dao.query(SELECT + " ORDER BY issue_date, series, number", mapper);
    }

    @Override
    public Invoice update(Invoice invoice) throws EntityNotFoundException {
        int affected = dao.update(
                "UPDATE invoices SET series = ?, number = ?, issue_date = ?, due_date = ?, " +
                "total_net = ?, total_vat = ?, total_gross = ?, status = ? WHERE id = ?",
                invoice.getSeries(), invoice.getNumber(),
                invoice.getIssueDate() != null ? Date.valueOf(invoice.getIssueDate()) : null,
                invoice.getDueDate() != null ? Date.valueOf(invoice.getDueDate()) : null,
                invoice.getTotalNet(), invoice.getTotalVat(), invoice.getTotalGross(),
                invoice.getStatus().name(), invoice.getId());
        if (affected == 0) {
            throw new EntityNotFoundException("Invoice", invoice.getId());
        }
        return invoice;
    }

    @Override
    public void deleteById(int id) throws EntityNotFoundException {
        int affected = dao.update("DELETE FROM invoices WHERE id = ?", id);
        if (affected == 0) {
            throw new EntityNotFoundException("Invoice", id);
        }
    }

    @Override
    public List<Invoice> findByStatus(InvoiceStatus status) {
        return dao.query(SELECT + " WHERE status = ? ORDER BY issue_date, series, number",
                mapper, status.name());
    }

    @Override
    public List<Invoice> findByUser(int userId) {
        return dao.query(SELECT + " WHERE user_id = ? ORDER BY issue_date, series, number",
                mapper, userId);
    }
}
