package by.bsu.lab3.entity;

public class Route {
    private int id;
    private String cityFrom;
    private String cityTo;

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

    @Override
    public String toString() {
        return "Route{id=" + id + ", cityFrom='" + cityFrom + "', cityTo='" + cityTo + "'}";
    }
}
