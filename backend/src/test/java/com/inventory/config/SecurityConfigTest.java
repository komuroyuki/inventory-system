package com.inventory.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.Repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class SecurityConfigTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  UserRepository userRepository;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Test
  void shouldRequireAuthentication_forProducts() throws Exception {
    mockMvc.perform(get("/products"))
        .andExpect(status().isUnauthorized());
  }

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

    mockMvc.perform(get("/products")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk());
  }

  @Test
  void shouldReturn401_whenNoToken() throws Exception {
    mockMvc.perform(get("/products"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldReturn401_whenTokenIsInvalid() throws Exception {
    mockMvc.perform(get("/products")
            .header("Authorization", "Bearer invalid-token"))
        .andExpect(status().isUnauthorized());
  }
}