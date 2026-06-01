package invox.repository;

import invox.model.Category;

import java.util.List;

public interface CategoryRepository extends Repository<Category> {

    List<Category> findByUser(int userId);
}
