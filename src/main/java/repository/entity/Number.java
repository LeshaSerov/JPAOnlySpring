package repository.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Setter
@Getter
public class Number {
    @Id
    private Long numberId;
    private String name;

    @OneToOne(optional = false)
    private Car car;
}
