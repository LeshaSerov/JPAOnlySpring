package repository;

import org.springframework.data.repository.ListCrudRepository;

public interface NumberRepository extends ListCrudRepository<Number, Long> {
}
