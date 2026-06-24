package com.inventory.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.inventory.service.ProductDetailService;

class ProductDetailControllerTests {

    private MockMvc mockMvc;

    @Mock
    private ProductDetailService service;

    @InjectMocks
    private ProductDetailController controller;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void shouldReturnOkWhenProductExists() throws Exception {

        when(service.getDetails(1))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {

        when(service.getDetails(999))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/products/999"))
                .andExpect(status().isNotFound());
    }

}
