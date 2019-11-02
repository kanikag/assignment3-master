package com.lnu.foundation.repository;

import com.lnu.foundation.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;
import java.util.Optional;

/**
 * Created by kangul on 10/10/2019.
 */
@RepositoryRestResource
//@CrossOrigin(origins = {"http://localhost:4200"})
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByRole_Name(@Param("role") String role);
    @RestResource
    Optional<User> findByUsername(@Param("username") String email);
}
