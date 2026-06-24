package com.inventory.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.inventory.service.FilterAndSearchService;

class ProductSearchControllerTests {

    private MockMvc mockMvc;

    @Mock
    private FilterAndSearchService filterAndSearchService;

    @InjectMocks
    private ProductSearchController filterAndSearchController;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders
                .standaloneSetup(filterAndSearchController)
                .build();
    }

    @Test
    void shouldReturnOk() throws Exception {

        when(filterAndSearchService.filterAndSearch(
                eq(1),
                eq("apple")))
                .thenReturn(List.of());

        mockMvc.perform(
                get("/products/search-filter")
                        .param("category_id", "1")
                        .param("keyword", "apple"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestWhenKeywordTooLong() throws Exception {

        String keyword = "a".repeat(51);

        mockMvc.perform(
                get("/products/search-filter")
                        .param("keyword", keyword))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenKeywordContainsInvalidCharacters() throws Exception {

        mockMvc.perform(
                get("/products/search-filter")
                        .param("keyword", "@@@"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldTrimFullWidthSpaces() throws Exception {

        when(filterAndSearchService.filterAndSearch(
                any(),
                eq("apple")))
                .thenReturn(List.of());

        mockMvc.perform(
                get("/products/search-filter")
                        .param("keyword", "　apple　"))
                .andExpect(status().isOk());
    }

}
