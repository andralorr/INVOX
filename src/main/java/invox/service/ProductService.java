package invox.service;

import invox.exception.EntityNotFoundException;
import invox.exception.InsufficientStockException;
import invox.model.Category;
import invox.model.Product;
import invox.repository.ProductRepository;

import java.util.List;

public class ProductService {

    private final ProductRepository productRepository;
    private final int ownerUserId;
    private final AuditService audit = AuditService.getInstance();

    public ProductService(ProductRepository productRepository, int ownerUserId) {
        this.productRepository = productRepository;
        this.ownerUserId = ownerUserId;
    }

    public Product addProduct(String code, String name, String unit, double price,
                              double vatRate, int stockQuantity, Category category) {
        Product product = new Product(0, code, name, unit, price, vatRate, stockQuantity, category);
        product.setUserId(ownerUserId);
        Product saved = productRepository.add(product);
        audit.log("ADD_PRODUCT");
        return saved;
    }

    public Product updateProduct(int id, String code, String name, String unit,
                                 double price, double vatRate, int stockQuantity,
                                 Category category) throws EntityNotFoundException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product", id));
        product.setCode(code);
        product.setName(name);
        product.setUnit(unit);
        product.setPrice(price);
        product.setVatRate(vatRate);
        product.setStockQuantity(stockQuantity);
        product.setCategory(category);
        productRepository.update(product);
        audit.log("UPDATE_PRODUCT");
        return product;
    }

    public void deleteProduct(int id) throws EntityNotFoundException {
        productRepository.deleteById(id);
        audit.log("DELETE_PRODUCT");
    }

    public Product getProduct(int id) throws EntityNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product", id));
    }

    public List<Product> listProducts() {
        return productRepository.findByUser(ownerUserId);
    }

    public Product increaseStock(int productId, int quantity) throws EntityNotFoundException {
        Product product = getProduct(productId);
        product.setStockQuantity(product.getStockQuantity() + quantity);
        productRepository.update(product);
        audit.log("INCREASE_STOCK");
        return product;
    }

    public Product decreaseStock(int productId, int quantity)
            throws EntityNotFoundException, InsufficientStockException {
        Product product = getProduct(productId);
        if (quantity > product.getStockQuantity()) {
            throw new InsufficientStockException(
                    product.getName(), quantity, product.getStockQuantity());
        }
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.update(product);
        audit.log("DECREASE_STOCK");
        return product;
    }
}