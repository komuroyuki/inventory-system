package com.inventory.Controller;

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
import org.springframework.test.web.reactive.server.WebTestClient;

import com.inventory.DTO.LoginRequest;
import com.inventory.DTO.LoginResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private WebTestClient client;

    @Value("${jwt.secret}")
    private String secretString;

    @Test
    void loginShouldReturnUserJwtWhenUserRequests() {
        testLogin("user@example.com", "user");
    }

    @Test
    void loginShouldReturnAdminJwtWhenAdminRequests() {
        testLogin("admin@example.com", "admin");
    }

    private void testLogin(String email, String role) {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword("password");

        client.post().uri("")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginResponse.class)
                .consumeWith(result -> assertLoginResponse(result, email, role));
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
