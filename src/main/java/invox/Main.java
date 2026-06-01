package invox;

import invox.exception.DuplicateEntityException;
import invox.exception.EntityNotFoundException;
import invox.exception.InvalidInvoiceStateException;
import invox.exception.InsufficientStockException;
import invox.model.Category;
import invox.model.CompanyClient;
import invox.model.IndividualClient;
import invox.model.Invoice;
import invox.model.InvoiceStatus;
import invox.model.Product;
import invox.repository.InMemoryCategoryRepository;
import invox.repository.InMemoryClientRepository;
import invox.repository.InMemoryInvoiceRepository;
import invox.repository.InMemoryProductRepository;
import invox.repository.ProductRepository;
import invox.service.CategoryService;
import invox.service.ClientService;
import invox.service.InvoiceService;
import invox.service.ProductService;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws EntityNotFoundException, DuplicateEntityException, InvalidInvoiceStateException {

        CategoryService categoryService = new CategoryService(new InMemoryCategoryRepository(), 1);
        ProductRepository productRepository = new InMemoryProductRepository();
        ProductService productService = new ProductService(productRepository, 1);
        ClientService clientService = new ClientService(new InMemoryClientRepository(), 1);
        InvoiceService invoiceService =
                new InvoiceService(new InMemoryInvoiceRepository(), productService);

        section("Adaugare categorii");
        Category electronice = categoryService.addCategory("Electronice", "Produse IT & gadgets");
        Category servicii = categoryService.addCategory("Servicii", "Servicii prestate");
        categoryService.listCategories().forEach(c -> System.out.println("  " + c));

        section("Adaugare produse");
        Product laptop = productService.addProduct("PRD-001", "Laptop Dell", "buc", 3500, 19, 10, electronice);
        Product mouse = productService.addProduct("PRD-002", "Mouse wireless", "buc", 90, 19, 50, electronice);
        Product consultanta = productService.addProduct("SRV-001", "Consultanta IT", "ora", 200, 19, 100, servicii);
        productService.listProducts().forEach(p -> System.out.println("  " + p));

        section("Adaugare clienti");
        CompanyClient firma = clientService.addCompanyClient(
                "contact@acme.ro", "0712345678", "Str. Industriilor 10", "Bucuresti", "Bucuresti",
                "ACME SRL", "RO12345678", "J40/123/2020", "RO49INGB0000999912345678", "ING Bank");
        IndividualClient persoana = clientService.addIndividualClient(
                "ion.popescu@gmail.com", "0723456789", "Bd. Unirii 5", "Cluj-Napoca", "Cluj",
                "Ion", "Popescu", "1900101123456");
        System.out.println("  Firma:    " + firma);
        System.out.println("  Persoana: " + persoana);

        section("Emitere factura");
        System.out.println("  Stoc laptop INAINTE: " + laptop.getStockQuantity());
        Map<Product, Integer> linii = new LinkedHashMap<>();
        linii.put(laptop, 2);
        linii.put(mouse, 3);
        linii.put(consultanta, 5);
        try {
            Invoice factura = invoiceService.issueInvoice(
                    1, firma, "INV", 1001, LocalDate.now().plusDays(15), linii);
            System.out.println("  Factura emisa: " + factura);
            System.out.println("  Stoc laptop DUPA:    " + productService.getProduct(laptop.getId()).getStockQuantity());

            section("Marcare factura ca platita");
            invoiceService.markAsPaid(factura.getId());
            System.out.println("  Facturi PLATITE: " + invoiceService.listInvoicesByStatus(InvoiceStatus.PAID));
        } catch (InsufficientStockException e) {
            System.out.println("  Eroare la emitere: " + e.getMessage());
        }

        section("Scenariu stoc insuficient (asteptam exceptie)");
        Map<Product, Integer> preaMult = new LinkedHashMap<>();
        preaMult.put(laptop, 999);
        try {
            invoiceService.issueInvoice(1, firma, "INV", 1002, LocalDate.now(), preaMult);
            System.out.println("  (nu ar trebui sa ajungem aici)");
        } catch (InsufficientStockException e) {
            System.out.println("  Prins: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
        }
        System.out.println("  Stoc laptop neschimbat: " + productService.getProduct(laptop.getId()).getStockQuantity());

        section("Scenariu entitate inexistenta (asteptam exceptie)");
        try {
            productService.getProduct(999);
        } catch (EntityNotFoundException e) {
            System.out.println("  Prins: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
        }

        section("Gata");
        System.out.println("  Toate actiunile au fost scrise in audit.csv");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
