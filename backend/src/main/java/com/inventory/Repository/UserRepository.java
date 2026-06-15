package com.inventory.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory.Entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}
