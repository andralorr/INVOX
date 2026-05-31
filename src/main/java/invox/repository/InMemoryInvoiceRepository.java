package invox.repository;

import invox.exception.EntityNotFoundException;
import invox.model.Invoice;
import invox.model.InvoiceStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

public class InMemoryInvoiceRepository implements InvoiceRepository {

    private final TreeSet<Invoice> invoices = new TreeSet<>();
    private int nextId = 1;

    @Override
    public Invoice add(Invoice invoice) {
        invoice.setId(nextId++);
        invoices.add(invoice);
        return invoice;
    }

    @Override
    public Optional<Invoice> findById(int id) {
        return invoices.stream()
                .filter(inv -> inv.getId() == id)
                .findFirst();
    }

    @Override
    public List<Invoice> findAll() {
        return new ArrayList<>(invoices);
    }

    @Override
    public Invoice update(Invoice invoice) throws EntityNotFoundException {
        Invoice existing = findById(invoice.getId())
                .orElseThrow(() -> new EntityNotFoundException("Invoice", invoice.getId()));
        invoices.remove(existing);
        invoices.add(invoice);
        return invoice;
    }

    @Override
    public void deleteById(int id) throws EntityNotFoundException {
        Invoice existing = findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice", id));
        invoices.remove(existing);
    }

    public List<Invoice> findByStatus(InvoiceStatus status) {
        List<Invoice> result = new ArrayList<>();
        for (Invoice inv : invoices) {
            if (inv.getStatus() == status) {
                result.add(inv);
            }
        }
        return result;
    }

    @Override
    public List<Invoice> findByUser(int userId) {
        List<Invoice> result = new ArrayList<>();
        for (Invoice inv : invoices) {
            if (inv.getUserId() == userId) {
                result.add(inv);
            }
        }
        return result;
    }
}
