package by.bsu.lab5.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "request")
@NamedQueries({
        @NamedQuery(name = "Request.findAll",
                query = "SELECT r FROM Request r ORDER BY r.id"),
        @NamedQuery(name = "Request.findById",
                query = "SELECT r FROM Request r WHERE r.id = :id")
})
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne
    @JoinColumn(name = "required_body_type_id", nullable = false)
    private BodyType requiredBodyType;

    @Column(name = "required_load_kg", nullable = false)
    private int requiredLoadKg;

    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;

    @OneToMany(mappedBy = "request")
    private List<Trip> trips;

    public Request() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public BodyType getRequiredBodyType() {
        return requiredBodyType;
    }

    public void setRequiredBodyType(BodyType requiredBodyType) {
        this.requiredBodyType = requiredBodyType;
    }

    public int getRequiredLoadKg() {
        return requiredLoadKg;
    }

    public void setRequiredLoadKg(int requiredLoadKg) {
        this.requiredLoadKg = requiredLoadKg;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    @Override
    public String toString() {
        return "Request{id=" + id + ", customer=" + customer + ", route=" + route
                + ", requiredBodyType=" + requiredBodyType + ", requiredLoadKg=" + requiredLoadKg
                + ", departureDate=" + departureDate + "}";
    }
}
