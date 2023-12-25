package com.is.mobops.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Tariff_Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "tariff")
    private Tariff tariff;
    private LocalDate date_order;
    @PrePersist
    public void prePersist() {
        date_order = LocalDate.now();
    }
    public Tariff_Orders() {
    }
    public Tariff_Orders(User user, Tariff tariff) {
        this.user = user;
        this.tariff = tariff;
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

    public Tariff getTariff() {
        return tariff;
    }

    public void setTariff(Tariff tariff) {
        this.tariff = tariff;
    }

    public LocalDate getDate_order() {
        return date_order;
    }

    public void setDate_order(LocalDate date_order) {
        this.date_order = date_order;
    }
}
