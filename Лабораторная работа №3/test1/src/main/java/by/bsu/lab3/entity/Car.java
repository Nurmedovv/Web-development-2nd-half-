package by.bsu.lab3.entity;

public class Car {
    public static final String STATUS_SERVICEABLE = "SERVICEABLE";
    public static final String STATUS_REPAIR = "REPAIR";

    private int id;
    private String plateNumber;
    private String model;
    private int bodyTypeId;
    private String bodyTypeName;
    private int loadKg;
    private String status;

    public Car() {
    }

    public Car(int id, String plateNumber, String model, int bodyTypeId, String bodyTypeName,
               int loadKg, String status) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.model = model;
        this.bodyTypeId = bodyTypeId;
        this.bodyTypeName = bodyTypeName;
        this.loadKg = loadKg;
        this.status = status;
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

    public int getBodyTypeId() {
        return bodyTypeId;
    }

    public void setBodyTypeId(int bodyTypeId) {
        this.bodyTypeId = bodyTypeId;
    }

    public String getBodyTypeName() {
        return bodyTypeName;
    }

    public void setBodyTypeName(String bodyTypeName) {
        this.bodyTypeName = bodyTypeName;
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

    @Override
    public String toString() {
        return "Car{id=" + id + ", plateNumber='" + plateNumber + "', model='" + model + "', bodyTypeId=" + bodyTypeId
                + ", loadKg=" + loadKg + ", status='" + status + "'}";
    }
}
