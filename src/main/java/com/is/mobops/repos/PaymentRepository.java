package com.is.mobops.repos;

import com.is.mobops.models.Payment;
import com.is.mobops.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
    @Query("select p from Payment as p")
    List<Payment> getAllPayments();
    @Query("select p from Payment as p where p.id = :id")
    Payment getPaymentById(@Param("id") Long id);

    List<Payment> getPaymentByUser(User user);

}
