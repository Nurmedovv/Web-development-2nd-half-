package by.bsu.lab3.entity;

public class Driver {
    private int id;
    private String fullName;
    private String licenseCategory;
    private String phone;

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

    @Override
    public String toString() {
        return "Driver{id=" + id + ", fullName='" + fullName + "', licenseCategory='" + licenseCategory
                + "', phone='" + phone + "'}";
    }
}
