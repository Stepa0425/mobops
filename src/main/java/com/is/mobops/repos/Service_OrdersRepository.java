package com.is.mobops.repos;


import com.is.mobops.models.Service_Orders;
import com.is.mobops.models.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Service_OrdersRepository extends CrudRepository<Service_Orders,Long> {
    @Query("select s from Service_Orders as s")
    List<Service_Orders> getAllServiceOrders();
    @Query("select s from Service_Orders as s where id = :id")
    Service_Orders getOrderById(@Param("id") Long id);

    @Modifying
    @Query("delete from Service_Orders so where so.service.id = :id and so.user.id = :user_id")
    void delete(@Param("id") Long service_id, @Param("user_id") Long user_id);

    List<Service_Orders> getService_OrdersByUser(User user);
}
