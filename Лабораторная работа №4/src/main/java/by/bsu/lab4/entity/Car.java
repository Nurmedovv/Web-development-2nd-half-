package by.bsu.lab4.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "car")
@NamedQueries({
        @NamedQuery(name = "Car.findAll",
                query = "SELECT c FROM Car c ORDER BY c.id"),
        @NamedQuery(name = "Car.findById",
                query = "SELECT c FROM Car c WHERE c.id = :id"),
        @NamedQuery(name = "Car.findInRepair",
                query = "SELECT c FROM Car c WHERE c.status = 'REPAIR' ORDER BY c.id")
})
public class Car {
    public static final String STATUS_SERVICEABLE = "SERVICEABLE";
    public static final String STATUS_REPAIR = "REPAIR";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private int id;

    @Column(name = "plate_number", nullable = false, unique = true, length = 20)
    private String plateNumber;

    @Column(name = "model", nullable = false, length = 50)
    private String model;

    @ManyToOne
    @JoinColumn(name = "body_type_id", nullable = false)
    private BodyType bodyType;

    @Column(name = "load_kg", nullable = false)
    private int loadKg;

    @Column(name = "status", nullable = false, length = 12)
    private String status;

    // обратная сторона связи: один автомобиль → много рейсов
    @OneToMany(mappedBy = "car")
    private List<Trip> trips;

    public Car() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public int getLoadKg() {
        return loadKg;
    }

    public void setLoadKg(int loadKg) {
        this.loadKg = loadKg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isServiceable() {
        return STATUS_SERVICEABLE.equals(status);
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    @Override
    public String toString() {
        return "Car{id=" + id + ", plateNumber='" + plateNumber + "', model='" + model
                + "', bodyType=" + bodyType + ", loadKg=" + loadKg + ", status='" + status + "'}";
    }
}
