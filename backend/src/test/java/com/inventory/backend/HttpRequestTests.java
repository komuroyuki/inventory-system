package com.inventory.backend;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.inventory.DTO.ProductRequest;
import com.inventory.Entity.Product;
import com.inventory.Repository.ProductRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class HttpRequestTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void replaceProductShouldReplaceRecord() {
        ProductRequest productRequest = new ProductRequest("Test", 10, "", 2);

        webTestClient.put()
                .uri("/products/1")
                .bodyValue(productRequest)
                .exchange()
                .expectStatus().isOk();

        Product product = productRepository.findById(1).orElseThrow();

        assertThat(product.getName()).isEqualTo(productRequest.name());
        assertThat(product.getQuantity()).isEqualTo(productRequest.quantity());
        assertThat(product.getImage()).isEqualTo(productRequest.image());
        assertThat(product.getCategoryId().getId()).isEqualTo(productRequest.categoryId());
    }

    @Test
    void replaceProductShouldReturnErrorIfIdNotExist() {
        ProductRequest productRequest = new ProductRequest("Error", 100, "error", 3);

        webTestClient.put()
                .uri("/products/1000")
                .bodyValue(productRequest)
                .exchange()
                .expectStatus().isNotFound();

        Product product = productRepository.findById(1000).orElse(null);

        assertThat(product).isEqualTo(null);
    }

    @Test
    void replaceProductShouldReturnErrorIfCategoryIdNotExist() {
        ProductRequest productRequest = new ProductRequest("Error", 100, "error", 100);

        webTestClient.put()
                .uri("/products/1")
                .bodyValue(productRequest)
                .exchange()
                .expectStatus().isBadRequest();

        Product product = productRepository.findById(1).orElseThrow();

        assertThat(product.getName()).isNotEqualTo(productRequest.name());
        assertThat(product.getQuantity()).isNotEqualTo(productRequest.quantity());
        assertThat(product.getImage()).isNotEqualTo(productRequest.image());
        assertThat(product.getCategoryId().getId()).isNotEqualTo(productRequest.categoryId());
    }

    @Test
    void replaceProductShouldReturnErrorIfNameIsBlank() {
        ProductRequest productRequest = new ProductRequest("", 100, "error", 3);

        webTestClient.put()
                .uri("/products/1")
                .bodyValue(productRequest)
                .exchange()
                .expectStatus().isBadRequest();

        Product product = productRepository.findById(1).orElseThrow();

        assertThat(product.getName()).isNotEqualTo(productRequest.name());
        assertThat(product.getQuantity()).isNotEqualTo(productRequest.quantity());
        assertThat(product.getImage()).isNotEqualTo(productRequest.image());
        assertThat(product.getCategoryId().getId()).isNotEqualTo(productRequest.categoryId());
    }

    @Test
    void replaceProductShouldReturnErrorIfQuantityIsNegative() {
        ProductRequest productRequest = new ProductRequest("Error", -1, "error", 3);

        webTestClient.put()
                .uri("/products/1")
                .bodyValue(productRequest)
                .exchange()
                .expectStatus().isBadRequest();

        Product product = productRepository.findById(1).orElseThrow();

        assertThat(product.getName()).isNotEqualTo(productRequest.name());
        assertThat(product.getQuantity()).isNotEqualTo(productRequest.quantity());
        assertThat(product.getImage()).isNotEqualTo(productRequest.image());
        assertThat(product.getCategoryId().getId()).isNotEqualTo(productRequest.categoryId());
    }

    @Test
    void replaceProductShouldReturnErrorIfCategoryIdIsNull() {
        ProductRequest productRequest = new ProductRequest("Error", 100, "error", null);

        webTestClient.put()
                .uri("/products/1")
                .bodyValue(productRequest)
                .exchange()
                .expectStatus().isBadRequest();

        Product product = productRepository.findById(1).orElseThrow();

        assertThat(product.getName()).isNotEqualTo(productRequest.name());
        assertThat(product.getQuantity()).isNotEqualTo(productRequest.quantity());
        assertThat(product.getImage()).isNotEqualTo(productRequest.image());
        assertThat(product.getCategoryId().getId()).isNotEqualTo(productRequest.categoryId());
    }
}
