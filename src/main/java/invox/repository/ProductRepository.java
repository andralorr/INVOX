package invox.repository;

import invox.model.Product;

import java.util.List;

public interface ProductRepository extends Repository<Product> {

    List<Product> findByUser(int userId);
}
