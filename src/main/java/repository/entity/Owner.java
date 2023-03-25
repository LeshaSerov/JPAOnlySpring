package repository.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Set;

@Entity
@Setter
@Getter
public class Owner {
    @Id
    private Long ownerId;

    @ManyToMany
    private Set<Car> cars;

}
