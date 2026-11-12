package by.bsu.lab4.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "body_type")
@NamedQueries({
        @NamedQuery(name = "BodyType.findAll",
                query = "SELECT b FROM BodyType b ORDER BY b.id"),
        @NamedQuery(name = "BodyType.findById",
                query = "SELECT b FROM BodyType b WHERE b.id = :id")
})
public class BodyType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "body_type_id")
    private int id;

    @Column(name = "name", nullable = false, unique = true, length = 30)
    private String name;

    // обратная сторона связи: один тип кузова → много автомобилей
    @OneToMany(mappedBy = "bodyType")
    private List<Car> cars;

    public BodyType() {
    }

    public BodyType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    @Override
    public String toString() {
        return "BodyType{id=" + id + ", name='" + name + "'}";
    }
}
