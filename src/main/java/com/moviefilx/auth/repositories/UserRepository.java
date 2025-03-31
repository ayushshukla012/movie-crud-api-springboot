package com.moviefilx.auth.repositories;

import com.moviefilx.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // We can directly create the method passing the field we want to select from database.
    // No need to write the definition JPA directly fetch data from table.
    // Below method searches the data from "users" database table based on the provided "username".
    Optional<User> findByEmail(String username);

}
