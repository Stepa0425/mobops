package com.is.mobops.repos;



import com.is.mobops.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

    @Query("select case when count(u) > 0 then true else false end from User as u where email = :email")
    boolean existsByEmail(@Param("email") String email);

    @Query("select id from User as u where email = :email")
    Long getUsersIdByEmail(@Param("email") String email);
    @Query("select password from User as u where email= :email")
    String getPasswordUserByEmail(@Param("email") String email);
    @Modifying(clearAutomatically = true)
    @Query("insert into User (name, surname, phone, email, password, address) VALUES (:name, :surname, :phone, :email, :password, :address)")
    void addUser(@Param("name") String name, @Param("surname") String surname,
                 @Param("phone") String phone, @Param("email") String email, @Param("password") String password, @Param("address") String address);

    @Query("select is_admin from User as u where id = :id")
    boolean isAdminById(@Param("id") Long id);

    @Query("select u from User as u")
    List<User> getAllUsers();

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update User set password = :password where id = :id")
    void setPasswordById(@Param("id") Long id, @Param("password") String password);
    @Modifying(clearAutomatically = true)
    @Query("update User set is_admin = true where email = :email")
    void setTrueAdminByEmail(@Param("email") String email);

    @Query("select u from User as u where id = :id")
    User getUserById(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("update User set name = :name, surname = :surname, phone = :phone, email = :email, address = :address where id = :id")
    void setDataById(@Param("id") Long id, @Param("name") String name, @Param("surname") String surname, @Param("phone") String phone, @Param("email") String email, @Param("address") String address);

    @Query("select u from User as u where phone = :phone")
    User getUserByPhone(@Param("phone") String phone);

}
