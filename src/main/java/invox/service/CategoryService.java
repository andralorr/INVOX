package invox.service;

import invox.exception.EntityNotFoundException;
import invox.model.Category;
import invox.repository.Repository;

import java.util.List;

public class CategoryService {

    private final Repository<Category> categoryRepository;
    private final AuditService audit = AuditService.getInstance();

    public CategoryService(Repository<Category> categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category addCategory(String name, String description) {
        Category category = new Category(0, name, description);
        Category saved = categoryRepository.add(category);
        audit.log("ADD_CATEGORY");
        return saved;
    }

    public Category updateCategory(int id, String name, String description)
            throws EntityNotFoundException {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category", id));
        category.setName(name);
        category.setDescription(description);
        categoryRepository.update(category);
        audit.log("UPDATE_CATEGORY");
        return category;
    }

    public void deleteCategory(int id) throws EntityNotFoundException {
        categoryRepository.deleteById(id);
        audit.log("DELETE_CATEGORY");
    }

    public Category getCategory(int id) throws EntityNotFoundException {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category", id));
    }

    public List<Category> listCategories() {
        return categoryRepository.findAll();
    }
}
