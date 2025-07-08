package com.aboc.payMyBuddy.repository;

import com.aboc.payMyBuddy.model.UserDb;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends CrudRepository<UserDb, Integer> {
    @Procedure(procedureName = "create_user_if_email_not_existing")
    int createUser(String user_username, String user_email, String user_password);

    @Procedure(procedureName = "update_user")
    int updateUser(int user_id, String user_username, String user_email, String user_password, double user_solde);

    UserDb findUserByEmail(String email);

    UserDb findUserById(int id);

    UserDb findUserByUsername(String username);
}
