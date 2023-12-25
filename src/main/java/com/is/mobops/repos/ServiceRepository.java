package com.is.mobops.repos;


import com.is.mobops.models.Service;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceRepository extends CrudRepository<Service, Long>{
    @Query("select s from Service as s")
    List<Service> getAllServices();

    @Query("SELECT s FROM Service as s WHERE s.title LIKE %:query%")
    List<Service> search(@Param("query") String query);
    @Query("select s from Service as s where s.price >= :minPrice and s.price <= :maxPrice")
    List<Service> filtration(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);
    @Query("select s from Service as s where id = :id")
    Service getServiceById(@Param("id") Long id);
    @Modifying(clearAutomatically = true)
    @Query("insert into Service (title, price, description) VALUES (:title, :price, :description)")
    void addService(@Param("title") String title, @Param("price") double price,
                    @Param("description") String description);
    @Modifying(clearAutomatically = true)
    @Query("delete from Service WHERE id = :id")
    void deleteServiceById(@Param("id") long id);

    @Query("select s from Service as s where title = :title")
    Service getServiceByTitle(@Param("title") String title);
}
