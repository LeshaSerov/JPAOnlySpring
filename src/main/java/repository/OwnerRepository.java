package repository;

import org.springframework.data.repository.ListCrudRepository;
import repository.entity.Owner;

public interface OwnerRepository extends ListCrudRepository<Owner, Long> {
}
