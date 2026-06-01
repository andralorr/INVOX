package invox.repository;

import invox.exception.EntityNotFoundException;
import invox.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryProductRepository implements ProductRepository {

    private final List<Product> products = new ArrayList<>();
    private int nextId = 1;

    @Override
    public Product add(Product product) {
        product.setId(nextId++);
        products.add(product);
        return product;
    }

    @Override
    public Optional<Product> findById(int id) {
        return products.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products);
    }

    @Override
    public Product update(Product product) throws EntityNotFoundException {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == product.getId()) {
                products.set(i, product);
                return product;
            }
        }
        throw new EntityNotFoundException("Product", product.getId());
    }

    @Override
    public void deleteById(int id) throws EntityNotFoundException {
        boolean removed = products.removeIf(p -> p.getId() == id);
        if (!removed) {
            throw new EntityNotFoundException("Product", id);
        }
    }

    @Override
    public List<Product> findByUser(int userId) {
        List<Product> result = new ArrayList<>();
        for (Product p : products) {
            if (p.getUserId() == userId) {
                result.add(p);
            }
        }
        return result;
    }

    public Optional<Product> findByCode(String code) {
        return products.stream()
                .filter(p -> p.getCode() != null && p.getCode().equals(code))
                .findFirst();
    }
}
