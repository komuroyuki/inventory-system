package com.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.inventory.dto.ProductRequest;
import com.inventory.entity.Category;
import com.inventory.entity.Product;
import com.inventory.repository.CategoryRepository;
import com.inventory.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductUpdateServiceTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductUpdateService service;

    private Product product;
    private Category category;
    private ProductRequest productRequest;

    private int categoryId;

    @BeforeEach
    void setUp() {

        category = new Category();
        category.setId(1);
        category.setName("Test Category");

        product = new Product();
        product.setId(1);
        product.setName("Old Product Name");
        product.setQuantity(10);
        product.setImage("old_image.jpg");
        product.setCategoryId(category);

        productRequest = new ProductRequest(
                "New Product Name",
                20,
                "new_image.jpg",
                1);

        categoryId = productRequest.categoryId();
    }

    @Test
    @DisplayName("カテゴリIDが存在しない場合は BAD_REQUEST を返す")
    void replaceProduct_categoryNotFound_returnsBadRequest() {

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = service.replaceProduct(productRequest, 1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("カテゴリIDが存在しません。\n", response.getBody());

        verify(categoryRepository)
                .findById(categoryId);

        verifyNoInteractions(productRepository);
    }

    @Test
    @DisplayName("商品IDが存在しない場合は NOT_FOUND を返す")
    void replaceProduct_productNotFound_returnsNotFound() {

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(productRepository.findById(1))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = service.replaceProduct(productRequest, 1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("IDが存在しません。\n", response.getBody());

        verify(categoryRepository)
                .findById(categoryId);

        verify(productRepository)
                .findById(1);
    }

    @Test
    @DisplayName("商品情報を正常に更新できる")
    void replaceProduct_success_returnsUpdatedProduct() {

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(productRepository.findById(1))
                .thenReturn(Optional.of(product));

        when(productRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = service.replaceProduct(productRequest, 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Product updatedProduct = assertInstanceOf(Product.class, response.getBody());

        assertEquals("New Product Name", updatedProduct.getName());
        assertEquals(20, updatedProduct.getQuantity());
        assertEquals("new_image.jpg", updatedProduct.getImage());
        assertEquals(
                category.getId(),
                updatedProduct.getCategoryId().getId());

        verify(categoryRepository)
                .findById(categoryId);

        verify(productRepository)
                .findById(1);

        verify(productRepository)
                .save(any(Product.class));
    }

}
