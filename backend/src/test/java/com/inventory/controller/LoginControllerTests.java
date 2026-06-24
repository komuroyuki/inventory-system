package com.inventory.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.reactive.server.WebTestClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.StatusAssertions;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.inventory.dto.LoginRequest;
import com.inventory.dto.LoginResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTests {

    @Autowired
    private WebTestClient client;

    @Value("${jwt.secret}")
    private String secretString;

    @Test
    void loginShouldReturnUserJwtWhenUserRequests() {
        LoginRequest request = createRequest("user@example.com", "password");
        assertLogin(request, "user");
    }

    @Test
    void loginShouldReturnAdminJwtWhenAdminRequests() {
        LoginRequest request = createRequest("admin@example.com", "password");
        assertLogin(request, "admin");
    }

    @Test
    void loginShouldReturnUnauthorizedWhenTheEmailIsIncorrect() {
        LoginRequest request = createRequest("incorrect@example.com", "password");
        createStatusAssertions(request).isUnauthorized();
    }

    @Test
    void loginShouldReturnUnauthorizedWhenThePasswordIsIncorrect() {
        LoginRequest request = createRequest("user@example.com", "incorrect");
        createStatusAssertions(request).isUnauthorized();
    }

    @Test
    void loginShouldReturnBadRequestWhenTheEmailIsBlank() {
        LoginRequest request = createRequest("", "password");
        createStatusAssertions(request).isBadRequest();
    }

    @Test
    void loginShouldReturnBadRequestWhenThePasswordIsBlank() {
        LoginRequest request = createRequest("user@example.com", "");
        createStatusAssertions(request).isBadRequest();
    }

    @Test
    void loginShouldReturnBadRequestWhenBodyIsMissing() {
        client.post().uri("")
                .exchange()
                .expectStatus().isBadRequest();
    }

    private LoginRequest createRequest(String email, String password) {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        return request;
    }

    private StatusAssertions createStatusAssertions(LoginRequest request) {
        return client.post().uri("")
                .bodyValue(request)
                .exchange()
                .expectStatus();
    }

    private void assertLogin(LoginRequest request, String role) {
        createStatusAssertions(request).isOk()
                .expectBody(LoginResponse.class)
                .consumeWith(result -> assertLoginResponse(result, request.getEmail(), role));
    }

    private void assertLoginResponse(EntityExchangeResult<LoginResponse> result, String email, String role) {
        LoginResponse body = result.getResponseBody();

        SecretKey key = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(body.getAccess_token())
                .getBody();

        assertThat(claims.getSubject()).isEqualTo(email);
        assertThat(claims.get("role", String.class)).isEqualTo(role);
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class ClientConfig {

        @Bean
        WebTestClientBuilderCustomizer clientCustomizer() {
            return builder -> builder
                    .baseUrl("/products/login")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        }
    }

}
