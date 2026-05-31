package invox.repository;

import invox.exception.EntityNotFoundException;
import invox.model.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryCategoryRepository implements Repository<Category> {

    private final Map<Integer, Category> categories = new HashMap<>();
    private int nextId = 1;

    @Override
    public Category add(Category category) {
        category.setId(nextId++);
        categories.put(category.getId(), category);
        return category;
    }

    @Override
    public Optional<Category> findById(int id) {
        return Optional.ofNullable(categories.get(id));
    }

    @Override
    public List<Category> findAll() {
        return new ArrayList<>(categories.values());
    }

    @Override
    public Category update(Category category) throws EntityNotFoundException {
        if (!categories.containsKey(category.getId())) {
            throw new EntityNotFoundException("Category", category.getId());
        }
        categories.put(category.getId(), category);
        return category;
    }

    @Override
    public void deleteById(int id) throws EntityNotFoundException {
        if (categories.remove(id) == null) {
            throw new EntityNotFoundException("Category", id);
        }
    }
}
