package com.tetris.tetrisburger_backend.domain.model;

import java.time.LocalDate;

public class Supplier {
    private Integer id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private LocalDate registrationDate;

    private Supplier(Integer id, String name, String phone, String email, String address, LocalDate registrationDate) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.registrationDate = registrationDate;
    }

    public static Supplier ofNew(String name, String phone, String email, String address, LocalDate registrationDate) {
        return new Supplier(null, name, phone, email, address, registrationDate);
    }

    public static Supplier of(Integer id, String name, String phone, String email, String address, LocalDate registrationDate) {
        return new Supplier(id, name, phone, email, address, registrationDate);
    }

    public void update(String name, String phone, String email, String address, LocalDate registrationDate) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.registrationDate = registrationDate;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }
}
