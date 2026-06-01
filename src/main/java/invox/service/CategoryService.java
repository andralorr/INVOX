package invox.service;

import invox.exception.EntityNotFoundException;
import invox.model.Category;
import invox.repository.CategoryRepository;

import java.util.List;

public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final int ownerUserId;
    private final AuditService audit = AuditService.getInstance();

    public CategoryService(CategoryRepository categoryRepository, int ownerUserId) {
        this.categoryRepository = categoryRepository;
        this.ownerUserId = ownerUserId;
    }

    public Category addCategory(String name, String description) {
        Category category = new Category(0, name, description);
        category.setUserId(ownerUserId);
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
        return categoryRepository.findByUser(ownerUserId);
    }
}
