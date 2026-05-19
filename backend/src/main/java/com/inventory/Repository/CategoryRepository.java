package com.inventory.Repository;

import org.springframework.data.repository.CrudRepository;

import com.inventory.Entity.Category;

public interface CategoryRepository extends CrudRepository<Category, String> {

}
