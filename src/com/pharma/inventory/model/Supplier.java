package com.pharma.inventory.model;
public class Supplier {
    private int supplierId, rating;
    private String supplierName, contactPerson, email, phone, address, city, country, paymentTerms;
    public Supplier() {}
    public Supplier(String supplierName, String contactPerson, String email) {
        this.supplierName = supplierName;
        this.contactPerson = contactPerson;
        this.email = email;
        this.rating = 5;
    }
    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    /** So this drops straight into a JComboBox. */
    @Override
    public String toString() { return supplierName; }
}
