package com.inventory.backend;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.inventory.DTO.ProductRequest;
import com.inventory.Entity.Product;
import com.inventory.Repository.ProductRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class HttpRequestTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("商品更新成功")
    void shouldReplaceProduct() {

        ProductRequest request =
                new ProductRequest("Test", 10, "", 2);

        webTestClient.put()
                .uri("/products/1")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();

        Product updatedProduct =
                productRepository.findById(1).orElseThrow();

        assertThat(updatedProduct.getName())
                .isEqualTo(request.name());

        assertThat(updatedProduct.getQuantity())
                .isEqualTo(request.quantity());

        assertThat(updatedProduct.getImage())
                .isEqualTo(request.image());

        assertThat(updatedProduct.getCategoryId().getId())
                .isEqualTo(request.categoryId());
    }

    @Test
    @DisplayName("存在しない商品IDなら404")
    void shouldReturnNotFoundWhenProductIdDoesNotExist() {

        ProductRequest request =
                new ProductRequest("Error", 100, "error", 3);

        webTestClient.put()
                .uri("/products/1000")
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();

        Product product =
                productRepository.findById(1000).orElse(null);

        assertThat(product).isNull();
    }

    @Test
    @DisplayName("存在しないカテゴリIDなら400")
    void shouldReturnBadRequestWhenCategoryIdDoesNotExist() {

        ProductRequest request =
                new ProductRequest("Error", 100, "error", 100);

        webTestClient.put()
                .uri("/products/1")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();

        Product product =
                productRepository.findById(1).orElseThrow();

        assertThat(product.getName())
                .isNotEqualTo(request.name());

        assertThat(product.getQuantity())
                .isNotEqualTo(request.quantity());

        assertThat(product.getImage())
                .isNotEqualTo(request.image());

        assertThat(product.getCategoryId().getId())
                .isNotEqualTo(request.categoryId());
    }

    @Test
    @DisplayName("商品名が空文字なら400")
    void shouldReturnBadRequestWhenNameIsBlank() {

        ProductRequest request =
                new ProductRequest("", 100, "error", 3);

        webTestClient.put()
                .uri("/products/1")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();

        Product product =
                productRepository.findById(1).orElseThrow();

        assertThat(product.getName())
                .isNotEqualTo(request.name());
    }

    @Test
    @DisplayName("在庫数が負数なら400")
    void shouldReturnBadRequestWhenQuantityIsNegative() {

        ProductRequest request =
                new ProductRequest("Error", -1, "error", 3);

        webTestClient.put()
                .uri("/products/1")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();

        Product product =
                productRepository.findById(1).orElseThrow();

        assertThat(product.getQuantity())
                .isNotEqualTo(request.quantity());
    }

    @Test
    @DisplayName("カテゴリIDがnullなら400")
    void shouldReturnBadRequestWhenCategoryIdIsNull() {

        ProductRequest request =
                new ProductRequest("Error", 100, "error", null);

        webTestClient.put()
                .uri("/products/1")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();

        Product product =
                productRepository.findById(1).orElseThrow();

        assertThat(product.getCategoryId().getId())
                .isNotEqualTo(request.categoryId());
    }
}