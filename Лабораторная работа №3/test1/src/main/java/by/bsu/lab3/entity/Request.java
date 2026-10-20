package by.bsu.lab3.entity;

import java.time.LocalDate;

public class Request {
    private int id;
    private int customerId;
    // customerName, routeFrom/To, requiredBodyTypeName — из JOIN, в таблицу не пишутся
    private String customerName;
    private int routeId;
    private String routeFrom;
    private String routeTo;
    private int requiredBodyTypeId;
    private String requiredBodyTypeName;
    private int requiredLoadKg;
    private LocalDate departureDate;

    public Request() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getRouteFrom() {
        return routeFrom;
    }

    public void setRouteFrom(String routeFrom) {
        this.routeFrom = routeFrom;
    }

    public String getRouteTo() {
        return routeTo;
    }

    public void setRouteTo(String routeTo) {
        this.routeTo = routeTo;
    }

    public int getRequiredBodyTypeId() {
        return requiredBodyTypeId;
    }

    public void setRequiredBodyTypeId(int requiredBodyTypeId) {
        this.requiredBodyTypeId = requiredBodyTypeId;
    }

    public String getRequiredBodyTypeName() {
        return requiredBodyTypeName;
    }

    public void setRequiredBodyTypeName(String requiredBodyTypeName) {
        this.requiredBodyTypeName = requiredBodyTypeName;
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

    @Override
    public String toString() {
        return "Request{id=" + id + ", customerId=" + customerId + ", routeId=" + routeId
                + ", requiredBodyTypeId=" + requiredBodyTypeId + ", requiredLoadKg=" + requiredLoadKg
                + ", departureDate=" + departureDate + "}";
    }
}
