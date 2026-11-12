package by.bsu.lab4.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "route")
@NamedQueries({
        @NamedQuery(name = "Route.findAll",
                query = "SELECT r FROM Route r ORDER BY r.id"),
        @NamedQuery(name = "Route.findById",
                query = "SELECT r FROM Route r WHERE r.id = :id")
})
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private int id;

    @Column(name = "city_from", nullable = false, length = 100)
    private String cityFrom;

    @Column(name = "city_to", nullable = false, length = 100)
    private String cityTo;

    // обратная сторона связи: один маршрут → много заявок
    @OneToMany(mappedBy = "route")
    private List<Request> requests;

    public Route() {
    }

    public Route(int id, String cityFrom, String cityTo) {
        this.id = id;
        this.cityFrom = cityFrom;
        this.cityTo = cityTo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityFrom() {
        return cityFrom;
    }

    public void setCityFrom(String cityFrom) {
        this.cityFrom = cityFrom;
    }

    public String getCityTo() {
        return cityTo;
    }

    public void setCityTo(String cityTo) {
        this.cityTo = cityTo;
    }

    public List<Request> getRequests() {
        return requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }

    @Override
    public String toString() {
        return "Route{id=" + id + ", cityFrom='" + cityFrom + "', cityTo='" + cityTo + "'}";
    }
}
