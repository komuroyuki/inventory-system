package com.inventory.Entity;

import java.time.LocalDateTime;

import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "products")
@Getter
@Setter
public class Product {
    @Id
    @Column(name = "product_id")
    @NotNull
    private Integer id;

    @Column(name = "product_name")
    @NotBlank
    private String name;

    @Column(name = "product_imageUrl")
    @URL
    private String image;

    @Column(name = "product_updatedAt")
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @ManyToOne()
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category categoryId;
}
