package com.aboc.payMyBuddy.repository;

import com.aboc.payMyBuddy.model.UserDb;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;


@Repository
public interface UserRepository extends CrudRepository<UserDb, Integer> {

    @Query(value = "SELECT COUNT(*) FROM user WHERE email = :mail", nativeQuery = true)
    int findUserDbByEmail(@Param("mail") String mail);

    @Query(value = "SELECT COUNT(*) FROM user WHERE username = :username", nativeQuery = true)
    int findUserByUserName(@Param("username") String username);

    @Procedure(procedureName = "create_user_if_email_not_existing")
    int createUser(String user_username, String user_email, String user_password);

    @Procedure(procedureName = "update_user")
    int updateUser(int user_id, String user_username, String user_email, String user_password, BigDecimal user_solde);

    public UserDb findUserByEmail(String email);
}
