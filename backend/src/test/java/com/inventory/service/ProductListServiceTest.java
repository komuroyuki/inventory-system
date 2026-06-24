package com.inventory.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.inventory.entity.Product;
import com.inventory.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class ProductListServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductListService productListService;

    private List<Product> productList;

    @BeforeEach
    void setUp() {
        productList = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1);
        product1.setName("Product 1");
        productList.add(product1);

        Product product2 = new Product();
        product2.setId(2);
        product2.setName("Product 2");
        productList.add(product2);
    }

    @Test
    @DisplayName("すべての製品リストを取得する")
    void getProductList_returnsAllProducts() {
        // productRepository.findAll()がproductListを返すようにモックを設定
        when(productRepository.findAll()).thenReturn(productList);

        // productListService.getProductList()を呼び出し、結果を取得
        List<Product> result = productListService.getProductList();

        // 結果がnullでないこと、サイズが2であることをアサート
        assertNotNull(result);
        assertEquals(2, result.size());
        // 返されたリストの要素が期待通りであることをアサート
        assertEquals(productList.get(0).getId(), result.get(0).getId());
        assertEquals(productList.get(1).getId(), result.get(1).getId());
        // productRepository.findAll()が一度だけ呼ばれたことを検証
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("製品リストが空の場合、空のリストを返す")
    void getProductList_emptyList_returnsEmptyList() {
        // productRepository.findAll()が空のArrayListを返すようにモックを設定
        when(productRepository.findAll()).thenReturn(new ArrayList<>());

        // productListService.getProductList()を呼び出し、結果を取得
        List<Product> result = productListService.getProductList();

        // 結果が空であることをアサート
        assertTrue(result.isEmpty());
        // productRepository.findAll()が一度だけ呼ばれたことを検証
        verify(productRepository, times(1)).findAll();
    }
}
