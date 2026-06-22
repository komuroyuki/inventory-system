package com.inventory.backend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.abort;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.StatusAssertions;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.Entity.Category;
import com.inventory.Entity.Product;
import com.inventory.Repository.CategoryRepository;
import com.inventory.Repository.ProductRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(authorities = "admin")
public class ProductDeletionTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void deleteProductShouldDeleteProduct() {
        Integer id = saveProduct();

        createStatusAssertions(id).isNoContent()
                .expectBody().isEmpty();
        assertThat(productRepository.existsById(id)).isFalse();
    }

    @Test
    @WithMockUser
    void deleteProductShouldReturnBadRequestWhenTheRequesterIsNotAnAdmin() {
        Integer id = saveProduct();

        createStatusAssertions(id).isForbidden();
        assertThat(productRepository.existsById(id)).isTrue();
    }

    @Test
    void deleteProductShouldReturnBadRequestWhenTheIdIsNotANumber() {
        client.delete().uri("/products/test")
                .exchange()
                .expectStatus().isBadRequest();
    }

    private Integer saveProduct() {
        Category category = categoryRepository.findById(1).orElseGet(() -> abort());

        Product product = new Product();
        product.setName("Test");
        product.setQuantity(10);
        product.setCategoryId(category);

        Product saved = productRepository.save(product);
        Integer id = saved.getId();

        return id;
    }

    private StatusAssertions createStatusAssertions(Integer id) {
        StatusAssertions assertions = client.delete().uri("/products/{id}", id)
                .exchange()
                .expectStatus();

        return assertions;
    }

}
