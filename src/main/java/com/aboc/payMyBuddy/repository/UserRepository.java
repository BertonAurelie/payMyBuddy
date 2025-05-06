package com.aboc.payMyBuddy.repository;

import com.aboc.payMyBuddy.model.User;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    @Procedure(procedureName = "create_user_if_email_not_existing")
    int createUser(String user_username, String user_email, String user_password);
}
