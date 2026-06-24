package com.inventory.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory.Entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
