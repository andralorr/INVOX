package invox.service;

import invox.exception.EntityNotFoundException;
import invox.exception.InvalidInvoiceStateException;
import invox.exception.InsufficientStockException;
import invox.model.Client;
import invox.model.Invoice;
import invox.model.InvoiceItem;
import invox.model.InvoiceStatus;
import invox.model.Product;
import invox.patterns.InvoiceBuilder;
import invox.repository.InvoiceRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ProductService productService;
    private final AuditService audit = AuditService.getInstance();

    public InvoiceService(InvoiceRepository invoiceRepository, ProductService productService) {
        this.invoiceRepository = invoiceRepository;
        this.productService = productService;
    }

    public Invoice issueInvoice(int issuerUserId, Client client, String series, int number,
                                LocalDate dueDate, Map<Product, Integer> lines)
            throws InsufficientStockException, EntityNotFoundException {

        for (Map.Entry<Product, Integer> line : lines.entrySet()) {
            Product product = line.getKey();
            int quantity = line.getValue();
            if (quantity > product.getStockQuantity()) {
                throw new InsufficientStockException(
                        product.getName(), quantity, product.getStockQuantity());
            }
        }

        InvoiceBuilder builder = new InvoiceBuilder()
                .series(series)
                .number(number)
                .issueDate(LocalDate.now())
                .dueDate(dueDate)
                .client(client)
                .status(InvoiceStatus.ISSUED);
        for (Map.Entry<Product, Integer> line : lines.entrySet()) {
            builder.addItem(line.getKey(), line.getValue());
        }
        Invoice invoice = builder.build();
        invoice.setUserId(issuerUserId);

        invoiceRepository.add(invoice);

        for (Map.Entry<Product, Integer> line : lines.entrySet()) {
            productService.decreaseStock(line.getKey().getId(), line.getValue());
        }

        audit.log("ISSUE_INVOICE");
        return invoice;
    }

    public Invoice markAsPaid(int invoiceId)
            throws EntityNotFoundException, InvalidInvoiceStateException {
        Invoice invoice = getInvoice(invoiceId);
        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new InvalidInvoiceStateException(
                    "Factura este anulata si nu poate fi marcata ca platita.");
        }
        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.update(invoice);
        audit.log("MARK_INVOICE_PAID");
        return invoice;
    }

    public Invoice cancelInvoice(int invoiceId) throws EntityNotFoundException {
        Invoice invoice = getInvoice(invoiceId);
        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            return invoice;
        }
        for (InvoiceItem item : invoice.getItems()) {
            productService.increaseStock(item.getProduct().getId(), item.getQuantity());
        }
        invoice.setStatus(InvoiceStatus.CANCELLED);
        invoiceRepository.update(invoice);
        audit.log("CANCEL_INVOICE");
        return invoice;
    }

    public void deleteInvoice(int invoiceId) throws EntityNotFoundException {
        invoiceRepository.deleteById(invoiceId);
        audit.log("DELETE_INVOICE");
    }

    public Invoice getInvoice(int invoiceId) throws EntityNotFoundException {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Invoice", invoiceId));
    }

    public List<Invoice> listInvoicesByUser(int userId) {
        return invoiceRepository.findByUser(userId);
    }

    public List<Invoice> listInvoicesByStatus(InvoiceStatus status) {
        return invoiceRepository.findByStatus(status);
    }
}
