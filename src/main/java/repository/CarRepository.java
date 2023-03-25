package repository;


import org.springframework.data.repository.ListCrudRepository;
import repository.entity.Car;

public interface CarRepository extends ListCrudRepository<Car, Long> {
}
