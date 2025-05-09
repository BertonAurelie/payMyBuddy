package com.aboc.payMyBuddy.repository;

import com.aboc.payMyBuddy.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    @Query(value = "SELECT COUNT(*) FROM user WHERE email = :mail", nativeQuery = true)
    int findUserByEmail(@Param("mail") String mail);

    @Query(value = "SELECT COUNT(*) FROM user WHERE username = :username", nativeQuery = true)
    int findUserByUserName(@Param("username") String username);

    @Procedure(procedureName = "create_user_if_email_not_existing")
    int createUser(String user_username, String user_email, String user_password);

    public User findUserByUsername(String username);
}
