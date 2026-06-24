package com.inventory.backend;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.inventory.dto.ProductRequest;
import com.inventory.dto.ProductResponse;
import com.inventory.entity.Product;
import com.inventory.repository.ProductRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser(authorities = "admin")
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class HttpRequestTestsMock {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductRepository productRepository;

    private ProductRequest createProductRequest(
            String name,
            Integer quantity,
            String image,
            Integer categoryId) {

        return new ProductRequest(name, quantity, image, categoryId);
    }

    @Test
    @DisplayName("商品更新成功")
    void shouldReplaceProduct() {

        ProductRequest request = new ProductRequest("Test", 10, "", 2);

        webTestClient.put()
                .uri("/products/1")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk();

        Product updatedProduct = productRepository.findById(1).orElseThrow();

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

        ProductRequest request = new ProductRequest("Error", 100, "error", 3);

        webTestClient.put()
                .uri("/products/1000")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    @DisplayName("存在しないカテゴリIDなら400")
    void shouldReturnBadRequestWhenCategoryIdDoesNotExist() {

        ProductRequest request = new ProductRequest("Error", 100, "error", 100);

        webTestClient.put()
                .uri("/products/1")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @DisplayName("商品名が空文字なら400")
    void shouldReturnBadRequestWhenNameIsBlank() {

        ProductRequest request = new ProductRequest("", 100, "error", 3);

        webTestClient.put()
                .uri("/products/1")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @DisplayName("在庫数が負数なら400")
    void shouldReturnBadRequestWhenQuantityIsNegative() {

        ProductRequest request = new ProductRequest("Error", -1, "error", 3);

        webTestClient.put()
                .uri("/products/1")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @DisplayName("カテゴリIDがnullなら400")
    void shouldReturnBadRequestWhenCategoryIdIsNull() {

        ProductRequest request = new ProductRequest("Error", 100, "error", null);

        webTestClient.put()
                .uri("/products/1")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @DisplayName("商品作成成功")
    void postProductShouldPostProduct() {

        ProductRequest request = createProductRequest("Post", 10, "", 1);

        ProductResponse response = webTestClient.post()
                .uri("/products")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(ProductResponse.class)
                .returnResult()
                .getResponseBody();

        Product product = productRepository.findById(response.id()).orElseThrow();

        assertThat(product.getName()).isEqualTo(request.name());
        assertThat(product.getQuantity()).isEqualTo(request.quantity());
        assertThat(product.getImage()).isEqualTo(request.image());
        assertThat(product.getCategoryId().getId()).isEqualTo(request.categoryId());
    }

    @Test
    @DisplayName("存在しないカテゴリIDならPOSTは400")
    void postProductShouldReturnBadRequestIfCategoryIdDoesNotExist() {

        long count = productRepository.count();

        ProductRequest request = createProductRequest("Error", 100, "error", 100);

        webTestClient.post()
                .uri("/products")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest();

        assertThat(productRepository.count())
                .isEqualTo(count);
    }

    @Test
    @DisplayName("商品名が空ならPOSTは400")
    void postProductShouldReturnBadRequestIfNameIsBlank() {

        long count = productRepository.count();

        ProductRequest request = createProductRequest("", 100, "error", 3);

        webTestClient.post()
                .uri("/products")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest();

        assertThat(productRepository.count())
                .isEqualTo(count);
    }

    @Test
    @DisplayName("在庫数が負数ならPOSTは400")
    void postProductShouldReturnBadRequestIfQuantityIsNegative() {

        long count = productRepository.count();

        ProductRequest request = createProductRequest("Error", -1, "error", 3);

        webTestClient.post()
                .uri("/products")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest();

        assertThat(productRepository.count())
                .isEqualTo(count);
    }

    @Test
    @DisplayName("カテゴリIDがnullならPOSTは400")
    void postProductShouldReturnBadRequestIfCategoryIdIsNull() {

        long count = productRepository.count();

        ProductRequest request = createProductRequest("Error", 100, "error", null);

        webTestClient.post()
                .uri("/products")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest();

        assertThat(productRepository.count())
                .isEqualTo(count);
    }
}
