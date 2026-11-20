package by.bsu.lab5.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "driver")
@NamedQueries({
        @NamedQuery(name = "Driver.findAll",
                query = "SELECT d FROM Driver d ORDER BY d.id"),
        @NamedQuery(name = "Driver.findById",
                query = "SELECT d FROM Driver d WHERE d.id = :id")
})
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "driver_id")
    private int id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "license_category", nullable = false, length = 10)
    private String licenseCategory;

    @Column(name = "phone", length = 20)
    private String phone;

    @OneToMany(mappedBy = "driver")
    private List<Trip> trips;

    public Driver() {
    }

    public Driver(int id, String fullName, String licenseCategory, String phone) {
        this.id = id;
        this.fullName = fullName;
        this.licenseCategory = licenseCategory;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLicenseCategory() {
        return licenseCategory;
    }

    public void setLicenseCategory(String licenseCategory) {
        this.licenseCategory = licenseCategory;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    @Override
    public String toString() {
        return "Driver{id=" + id + ", fullName='" + fullName + "', licenseCategory='" + licenseCategory
                + "', phone='" + phone + "'}";
    }
}
