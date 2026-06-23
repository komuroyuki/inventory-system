package com.inventory.backend;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.inventory.DTO.ProductRequest;
import com.inventory.DTO.ProductResponse;
import com.inventory.Entity.Product;
import com.inventory.Repository.ProductRepository;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class HttpRequestTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductRepository productRepository;

    private WebTestClient client; // ★固定クライアント

    private ProductRequest createProductRequest(
            String name,
            Integer quantity,
            String image,
            Integer categoryId) {

        return new ProductRequest(name, quantity, image, categoryId);
    }

    

    @Autowired
private JdbcTemplate jdbcTemplate;

@Autowired
private PasswordEncoder passwordEncoder;

@BeforeEach
void setupAuth() {

    jdbcTemplate.update("DELETE FROM users");

    jdbcTemplate.update("""
        INSERT INTO users(id, email, name, password, is_admin)
        VALUES (?, ?, ?, ?, ?)
    """,
        99999L,
        "test@example.com",
        "test",
        passwordEncoder.encode("Password1"),
        true
    );

    String response = webTestClient.post()
            .uri("/products/login")
            .header("Content-Type", "application/json")
            .bodyValue("""
                {
                    "email":"test@example.com",
                    "password":"Password1"
                }
            """)
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    String token = JsonPath.read(response, "$.access_token");

    this.client = webTestClient.mutate()
            .defaultHeader("Authorization", "Bearer " + token)
            .build();
}

    @Test
    @DisplayName("商品更新成功")
    void shouldReplaceProduct() {

        ProductRequest request =
                new ProductRequest("Test", 10, "", 2);

        client.put()
                .uri("/products/1")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk();

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

        client.put()
                .uri("/products/1000")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    @DisplayName("存在しないカテゴリIDなら400")
    void shouldReturnBadRequestWhenCategoryIdDoesNotExist() {

        ProductRequest request =
                new ProductRequest("Error", 100, "error", 100);

        client.put()
                .uri("/products/1")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @DisplayName("商品名が空文字なら400")
    void shouldReturnBadRequestWhenNameIsBlank() {

        ProductRequest request =
                new ProductRequest("", 100, "error", 3);

        client.put()
                .uri("/products/1")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @DisplayName("在庫数が負数なら400")
    void shouldReturnBadRequestWhenQuantityIsNegative() {

        ProductRequest request =
                new ProductRequest("Error", -1, "error", 3);

        client.put()
                .uri("/products/1")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @DisplayName("カテゴリIDがnullなら400")
    void shouldReturnBadRequestWhenCategoryIdIsNull() {

        ProductRequest request =
                new ProductRequest("Error", 100, "error", null);

        client.put()
                .uri("/products/1")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
@DisplayName("商品作成成功")
void postProductShouldPostProduct() {

    ProductRequest request =
            createProductRequest("Post", 10, "", 1);

    ProductResponse response = client.post()
            .uri("/products")
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ProductResponse.class)
            .returnResult()
            .getResponseBody();

    Product product =
            productRepository.findById(response.id()).orElseThrow();

    assertThat(product.getName()).isEqualTo(request.name());
    assertThat(product.getQuantity()).isEqualTo(request.quantity());
    assertThat(product.getImage()).isEqualTo(request.image());
    assertThat(product.getCategoryId().getId())
            .isEqualTo(request.categoryId());
}

    @Test
    @DisplayName("存在しないカテゴリIDならPOSTは400")
    void postProductShouldReturnBadRequestIfCategoryIdDoesNotExist() {

        long count = productRepository.count();

        ProductRequest request =
                createProductRequest("Error", 100, "error", 100);

        client.post()
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

        ProductRequest request =
                createProductRequest("", 100, "error", 3);

        client.post()
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

        ProductRequest request =
                createProductRequest("Error", -1, "error", 3);

        client.post()
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

        ProductRequest request =
                createProductRequest("Error", 100, "error", null);

        client.post()
                .uri("/products")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest();

        assertThat(productRepository.count())
                .isEqualTo(count);
    }
}