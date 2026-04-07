package com.tetris.tetrisburger_backend.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Supplier {

    private Integer id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private LocalDateTime registrationDate;

    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Integer createdBy;
    private Integer updatedBy;
    private Integer deletedBy;
    // ─────────────────────────────────────────────────────────────────────────

    private Supplier() {}

    public static Supplier ofNew(String name, String phone, String email,
                                 String address, Integer createdBy) {
        Supplier s = new Supplier();
        s.name             = name;
        s.phone            = phone;
        s.email            = email;
        s.address          = address;
        s.registrationDate = LocalDateTime.now();
        s.createdBy        = createdBy;
        return s;
    }

    public static Supplier reconstitute(
            Integer id, String name, String phone, String email,
            String address, LocalDateTime registrationDate,
            LocalDateTime updatedAt, LocalDateTime deletedAt,
            Integer createdBy, Integer updatedBy, Integer deletedBy
    ) {
        Supplier s = new Supplier();
        s.id               = id;
        s.name             = name;
        s.phone            = phone;
        s.email            = email;
        s.address          = address;
        s.registrationDate = registrationDate;
        s.updatedAt        = updatedAt;
        s.deletedAt        = deletedAt;
        s.createdBy        = createdBy;
        s.updatedBy        = updatedBy;
        s.deletedBy        = deletedBy;
        return s;
    }

    public void update(String name, String phone, String email,
                       String address, Integer updatedBy) {
        this.name      = name;
        this.phone     = phone;
        this.email     = email;
        this.address   = address;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    public void softDelete(Integer deletedBy) {
        if (this.deletedAt != null)
            throw new IllegalStateException("El proveedor ya fue eliminado");
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = deletedBy;
    }

    public boolean isDeleted() { return this.deletedAt != null; }

    // ── Getters ──────────────────────────────────────────────────────────────
    public Integer getId()                 { return id; }
    public String getName()                { return name; }
    public String getPhone()               { return phone; }
    public String getEmail()               { return email; }
    public String getAddress()             { return address; }
    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public LocalDateTime getUpdatedAt()    { return updatedAt; }
    public LocalDateTime getDeletedAt()    { return deletedAt; }
    public Integer getCreatedBy()          { return createdBy; }
    public Integer getUpdatedBy()          { return updatedBy; }
    public Integer getDeletedBy()          { return deletedBy; }
}