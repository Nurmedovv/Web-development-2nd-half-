package by.bsu.lab5.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "trip")
@NamedQueries({
        @NamedQuery(name = "Trip.findAll",
                query = "SELECT t FROM Trip t ORDER BY t.id"),
        @NamedQuery(name = "Trip.findById",
                query = "SELECT t FROM Trip t WHERE t.id = :id"),
        @NamedQuery(name = "Trip.findByDriver",
                query = "SELECT t FROM Trip t WHERE t.driver.id = :driverId ORDER BY t.id")
})
public class Trip {
    public static final String STATUS_PLANNED = "PLANNED";
    public static final String STATUS_COMPLETED = "COMPLETED";

    @Id
    @Column(name = "trip_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @Column(name = "status", nullable = false, length = 10)
    private String status;

    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;

    @Column(name = "completion_date")
    private LocalDate completionDate;

    @Column(name = "car_condition_after", length = 12)
    private String carConditionAfter;

    public Trip() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public String getCarConditionAfter() {
        return carConditionAfter;
    }

    public void setCarConditionAfter(String carConditionAfter) {
        this.carConditionAfter = carConditionAfter;
    }

    @Override
    public String toString() {
        return "Trip{id=" + id + ", request=" + request + ", driver=" + driver + ", car=" + car
                + ", status='" + status + "', departureDate=" + departureDate
                + ", completionDate=" + completionDate + ", carConditionAfter='" + carConditionAfter + "'}";
    }
}
