package com.is.mobops.repos;

import com.is.mobops.models.Tariff_Orders;
import com.is.mobops.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Tariff_OrdersRepository extends CrudRepository<Tariff_Orders, Long> {
    @Query("select t from Tariff_Orders as t")
    List<Tariff_Orders> getAllTariffsOrders();
    @Query("select t from Tariff_Orders as t where id = :id")
    Tariff_Orders getOrderById(@Param("id") Long id);

    Tariff_Orders getTariff_OrdersByUser(User user);
    @Query("SELECT t.tariff.id, COUNT(t) FROM Tariff_Orders t GROUP BY t.tariff")
    List<Object[]> countUsersByTariff();
}
