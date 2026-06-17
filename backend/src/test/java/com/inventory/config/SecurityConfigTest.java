package com.inventory.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.transaction.Transactional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class SecurityConfigTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  JdbcTemplate jdbcTemplate;

  @Autowired
  PasswordEncoder passwordEncoder;

@BeforeEach
void setup() {
    jdbcTemplate.update("""
        INSERT INTO users(id, email, name, password, is_admin)
        VALUES (?, ?, ?, ?, ?)
    """,
        99999L,
        "test@example.com",
        "test",
        passwordEncoder.encode("Password1"),
        false
    );
}

// APIにセキュリティが効いているか
@Test
void shouldRequireAuthentication_forProducts() throws Exception {

    mockMvc.perform(get("/products"))
        .andExpect(status().isUnauthorized());
}

  // 成功
@Test
void login_shouldReturn200_whenCredentialsAreValid() throws Exception {

    mockMvc.perform(post("/products/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
              {
                "email": "test@example.com",
                "password": "Password1"
              }
            """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.access_token").exists());
}

  // 失敗
@Test
void login_shouldReturn401_whenCredentialsAreInvalid() throws Exception {

    mockMvc.perform(post("/products/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {
            "email": "test@example.com",
            "password": "wrong"
          }
        """))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error_code").value("AUTH_FAILED"));
}

// JWTで認証成功
@Test
void shouldAccessProtectedApi_whenTokenIsValid() throws Exception {

    String response = mockMvc.perform(post("/products/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
              {
                "email": "test@example.com",
                "password": "Password1"
              }
            """))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    String token = com.jayway.jsonpath.JsonPath.read(response, "$.access_token");

    mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/products")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk());
}


// トークンなし
@Test
void shouldReturn401_whenNoToken() throws Exception {

    mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/products"))
        .andExpect(status().isUnauthorized());
}

// 不正トークン
@Test
void shouldReturn401_whenTokenIsInvalid() throws Exception {

    mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/products")
            .header("Authorization", "Bearer invalid-token"))
        .andExpect(status().isUnauthorized());
}
}