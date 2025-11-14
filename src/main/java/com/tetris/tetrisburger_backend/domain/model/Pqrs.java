package com.tetris.tetrisburger_backend.domain.model;

import java.time.LocalDateTime;

public class Pqrs {
    private Integer idPqrs;
    private PqrsType type;
    private PqrsStatus status;
    private PqrsPriority priority;
    private String subject;
    private String description;
    private String response;
    private Integer idUser;
    private Integer assignedTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Integer createdBy;
    private Integer updatedBy;
    private Integer deletedBy;

    public Pqrs (){}

    public Pqrs(Integer idPqrs, PqrsType type, PqrsStatus status, PqrsPriority priority, String subject, String description, String response, Integer idUser, Integer assignedTo, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt, Integer createdBy, Integer updatedBy, Integer deletedBy) {
        this.idPqrs = idPqrs;
        this.type = type;
        this.status = status;
        this.priority = priority;
        this.subject = subject;
        this.description = description;
        this.response = response;
        this.idUser = idUser;
        this.assignedTo = assignedTo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.deletedBy = deletedBy;
    }

    public Integer getIdPqrs() {
        return idPqrs;
    }

    public void setIdPqrs(Integer idPqrs) {
        this.idPqrs = idPqrs;
    }

    public PqrsType getType() {
        return type;
    }

    public void setType(PqrsType type) {
        this.type = type;
    }

    public PqrsStatus getStatus() {
        return status;
    }

    public void setStatus(PqrsStatus status) {
        this.status = status;
    }

    public PqrsPriority getPriority() {
        return priority;
    }

    public void setPriority(PqrsPriority priority) {
        this.priority = priority;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public Integer getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Integer assignedTo) {
        this.assignedTo = assignedTo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Integer getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(Integer deletedBy) {
        this.deletedBy = deletedBy;
    }
}
