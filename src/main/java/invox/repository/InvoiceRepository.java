package invox.repository;

import invox.model.Invoice;
import invox.model.InvoiceStatus;

import java.util.List;

public interface InvoiceRepository extends Repository<Invoice> {

    List<Invoice> findByStatus(InvoiceStatus status);

    List<Invoice> findByUser(int userId);
}
