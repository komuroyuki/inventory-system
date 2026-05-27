package com.inventory.backend;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.client.RestTestClient;

import com.inventory.DTO.ProductRequest;
import com.inventory.Entity.Product;
import com.inventory.Repository.ProductRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class HttpRequestTests {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void replaceProductShouldReplaceRecord() {
        ProductRequest productRequest = new ProductRequest("Test", 10, "", 2);

        restTestClient.put()
                .uri("/products/1")
                .body(productRequest)
                .exchange()
                .expectStatus().isOk();

        Product product = productRepository.findById(1).orElseThrow();

        assertThat(product.getName()).isEqualTo(productRequest.name());
        assertThat(product.getQuantity()).isEqualTo(productRequest.quantity());
        assertThat(product.getImage()).isEqualTo(productRequest.image());
        assertThat(product.getCategoryId().getId()).isEqualTo(productRequest.categoryId());
    }
}
