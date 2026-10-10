package com.foodsupply.management.procurement.entity;
import jakarta.persistence.*;
@Entity @Table(name="suppliers") public class Supplier {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=150) private String name; @Column(length=100) private String contactPerson;
 @Column(length=20) private String phone; @Column(length=100) private String email;
 public Long getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;}
 public String getContactPerson(){return contactPerson;} public void setContactPerson(String v){contactPerson=v;}
 public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;}
}
