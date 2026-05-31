package invox.repository;

import invox.exception.EntityNotFoundException;
import invox.model.Identifiable;

import java.util.List;
import java.util.Optional;

public interface Repository<T extends Identifiable> {

    T add(T entity);

    Optional<T> findById(int id);

    List<T> findAll();

    T update(T entity) throws EntityNotFoundException;

    void deleteById(int id) throws EntityNotFoundException;
}
