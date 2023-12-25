package com.is.mobops.repos;

import com.is.mobops.models.Tariff;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TariffRepository extends CrudRepository<Tariff, Long> {
    @Query("select t from Tariff as t")
    List<Tariff> getAllTariffs();

    @Query("select t from Tariff as t where id = :id")
    Tariff getTariffById(@Param("id") Long id);

    @Query("select case when count(t) > 0 then true else false end from Tariff as t where id = :id")
    boolean existsById(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("delete from Tariff WHERE id = :id")
    void deleteTariffById(@Param("id") long id);

    @Query("select t from Tariff as t where t.title LIKE %:query%")
    List<Tariff> search(@Param("query") String query);

    @Query("select t from Tariff as t where t.price >= :minPrice and t.price <= :maxPrice and t.sms >= :minSMS and t.sms <= :maxSMS and t.internet >= :minInternet and t.internet <= :maxInternet and t.minutes >= :minMinutes and t.minutes <= :maxMinutes")
    List<Tariff> filtration(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice, @Param("minSMS") double minSMS, @Param("maxSMS") double maxSMS, @Param("minInternet") double minInternet, @Param("maxInternet") double maxInternet, @Param("minMinutes") double minMinutes, @Param("maxMinutes") double maxMinutes);

    @Query("select t from Tariff as t where title = :title")
    Tariff getTariffByTitle(@Param("title") String title);


}
