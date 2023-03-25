package repository;

import org.springframework.data.repository.ListCrudRepository;
import repository.entity.ClassCar;

public interface ClassCarRepository extends ListCrudRepository<ClassCar, Long> {
}
