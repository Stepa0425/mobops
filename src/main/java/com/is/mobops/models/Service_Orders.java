package com.is.mobops.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Service_Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "service")
    private Service service;
    private LocalDate date_order;
    @PrePersist
    public void prePersist() {
        date_order = LocalDate.now();
    }
    public Service_Orders() {
    }
    public Service_Orders(User user, Service service) {
        this.user = user;
        this.service = service;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public LocalDate getDate_order() {
        return date_order;
    }

    public void setDate_order(LocalDate date_order) {
        this.date_order = date_order;
    }
}
