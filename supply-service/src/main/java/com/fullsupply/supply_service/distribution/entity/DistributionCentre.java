package com.fullsupply.supply_service.distribution.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "distribution_centres")
public class DistributionCentre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "centre_id")
    private Long centreId;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "location", length = 150)
    private String location;

    @Column(name = "capacity", precision = 10, scale = 2)
    private BigDecimal capacity;

    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    public DistributionCentre() {}

    public Long getCentreId() { return centreId; }
    public void setCentreId(Long centreId) { this.centreId = centreId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public BigDecimal getCapacity() { return capacity; }
    public void setCapacity(BigDecimal capacity) { this.capacity = capacity; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
}