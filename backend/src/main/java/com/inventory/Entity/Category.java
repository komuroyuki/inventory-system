package com.inventory.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {
    @Id
    @Column(name = "category_id")
    @NotBlank
    private String id;

    @Column(name = "category_name")
    @NotBlank
    private String name;
}
