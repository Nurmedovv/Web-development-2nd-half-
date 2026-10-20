package by.bsu.lab3.entity;

import java.time.LocalDate;

public class Trip {
    public static final String STATUS_PLANNED = "PLANNED";
    public static final String STATUS_COMPLETED = "COMPLETED";

    private int id;
    private int requestId;
    private Integer driverId;
    private Integer carId;
    private String status;
    private LocalDate departureDate;
    private LocalDate completionDate;
    private String carConditionAfter;

    // driverName, carPlate, route* — из JOIN, в trip не пишутся
    private String driverName;
    private String carPlate;
    private String routeFrom;
    private String routeTo;

    public Trip() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
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

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getCarPlate() {
        return carPlate;
    }

    public void setCarPlate(String carPlate) {
        this.carPlate = carPlate;
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

    @Override
    public String toString() {
        return "Trip{id=" + id + ", requestId=" + requestId + ", driverId=" + driverId + ", carId=" + carId
                + ", status='" + status + "', departureDate=" + departureDate + ", completionDate=" + completionDate
                + ", carConditionAfter='" + carConditionAfter + "'}";
    }
}
