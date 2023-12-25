package com.is.mobops.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;
    private LocalDate date_payment;
    private double sum;
    @PrePersist
    public void prePersist() {
        date_payment = LocalDate.now();
    }

    public Payment() {
    }

    public Payment(User user, double sum){
        this.user = user;
        this.sum = sum;
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

    public LocalDate getDate_payment() {
        return date_payment;
    }

    public void setDate_order(LocalDate date_payment) {
        this.date_payment = date_payment;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }
}
