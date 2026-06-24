package com.inventory.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
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

import com.inventory.dto.ProductDetailsResponse;
import com.inventory.entity.Category;
import com.inventory.entity.Product;
import com.inventory.repository.DetailsRepository;
import com.inventory.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class DetailsServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private DetailsRepository detailsRepository;

    @InjectMocks
    private DetailsService detailsService;

    private Product product;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1);
        category.setName("Test Category");

        product = new Product();
        product.setId(101);
        product.setName("Test Product");
        product.setQuantity(10);
        product.setImage("test_image.jpg");
        product.setCategoryId(category);
        product.setLastModifiedDate(LocalDateTime.of(2023, 10, 26, 10, 0, 0));
    }

    @Test
    @DisplayName("商品が存在しない場合、NOT_FOUNDを返す")
    void getDetails_productNotFound_returnsNotFound() {
        // productRepository.findById()がOptional.empty()を返すようにモックを設定
        when(productRepository.findById(anyInt())).thenReturn(Optional.empty());

        // detailsService.getDetails()を呼び出し、結果を取得
        ResponseEntity<?> response = detailsService.getDetails(101);

        // ステータスコードがHttpStatus.NOT_FOUNDであることをアサート
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        // レスポンスボディが期待通りのエラーメッセージであることをアサート
        assertEquals("IDが存在しません。", response.getBody());
        // productRepository.findById()が一度だけ指定したIDで呼ばれたことを検証
        verify(productRepository, times(1)).findById(101);
        // detailsRepositoryが一切呼び出されなかったことを検証
        verifyNoInteractions(detailsRepository);
    }

    @Test
    @DisplayName("商品が存在し、次の商品も存在する場合、商品詳細と次の商品IDを返す")
    void getDetails_productAndNextProductExist_returnsDetailsAndNextProductId() {
        // 次の商品を表すProductエンティティを作成
        Product nextProduct = new Product();
        nextProduct.setId(102);
        nextProduct.setName("Next Product");

        // productRepository.findById()がOptional.of(product)を返すようにモックを設定
        when(productRepository.findById(anyInt())).thenReturn(Optional.of(product));
        // detailsRepository.findFirstByIdGreaterThanOrderByIdAsc()がnextProductを返すようにモックを設定
        when(detailsRepository.findFirstByIdGreaterThanOrderByIdAsc(anyInt())).thenReturn(nextProduct);

        // detailsService.getDetails()を呼び出し、結果を取得
        ResponseEntity<?> response = detailsService.getDetails(101);

        // ステータスコードがHttpStatus.OKであることをアサート
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // レスポンスボディがProductDetailsResponse型であることをアサート
        assertTrue(response.getBody() instanceof ProductDetailsResponse);

        // レスポンスボディをProductDetailsResponseにキャスト
        ProductDetailsResponse detailsResponse = (ProductDetailsResponse) response.getBody();

        // 各フィールドが期待通りに設定されているかをアサート
        assertEquals(product.getId(), detailsResponse.getProductId());
        assertEquals(product.getName(), detailsResponse.getProductName());
        assertEquals(product.getQuantity(), detailsResponse.getProductQuantity());
        assertEquals(product.getImage(), detailsResponse.getProductImageUrl());
        assertEquals(product.getCategoryId().getId(), detailsResponse.getCategoryId());
        assertEquals(product.getLastModifiedDate().toString(), detailsResponse.getProductUpdatedAt());
        assertEquals(nextProduct.getId(), detailsResponse.getNextProductId());

        // productRepository.findById()が一度だけ指定したIDで呼ばれたことを検証
        verify(productRepository, times(1)).findById(101);
        // detailsRepository.findFirstByIdGreaterThanOrderByIdAsc()が一度だけ指定したIDで呼ばれたことを検証
        verify(detailsRepository, times(1)).findFirstByIdGreaterThanOrderByIdAsc(101);
    }

    @Test
    @DisplayName("商品が存在し、次の商品が存在しない場合、商品詳細とnullの次の商品IDを返す")
    void getDetails_productExistsNextProductNull_returnsDetailsAndNullNextProductId() {
        // productRepository.findById()がOptional.of(product)を返すようにモックを設定
        when(productRepository.findById(anyInt())).thenReturn(Optional.of(product));
        // detailsRepository.findFirstByIdGreaterThanOrderByIdAsc()がnullを返すようにモックを設定
        when(detailsRepository.findFirstByIdGreaterThanOrderByIdAsc(anyInt())).thenReturn(null);

        // detailsService.getDetails()を呼び出し、結果を取得
        ResponseEntity<?> response = detailsService.getDetails(101);

        // ステータスコードがHttpStatus.OKであることをアサート
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // レスポンスボディがProductDetailsResponse型であることをアサート
        assertTrue(response.getBody() instanceof ProductDetailsResponse);

        // レスポンスボディをProductDetailsResponseにキャスト
        ProductDetailsResponse detailsResponse = (ProductDetailsResponse) response.getBody();

        // 次の商品IDがnullであることをアサート
        assertNull(detailsResponse.getNextProductId());

        // productRepository.findById()が一度だけ指定したIDで呼ばれたことを検証
        verify(productRepository, times(1)).findById(101);
        // detailsRepository.findFirstByIdGreaterThanOrderByIdAsc()が一度だけ指定したIDで呼ばれたことを検証
        verify(detailsRepository, times(1)).findFirstByIdGreaterThanOrderByIdAsc(101);
    }

    @Test
    @DisplayName("商品の更新日時がnullの場合、空文字列を返す")
    void getDetails_productUpdatedAtIsNull_returnsEmptyString() {
        // 商品の更新日時をnullに設定
        product.setLastModifiedDate(null);
        // productRepository.findById()がOptional.of(product)を返すようにモックを設定
        when(productRepository.findById(anyInt())).thenReturn(Optional.of(product));
        // detailsRepository.findFirstByIdGreaterThanOrderByIdAsc()がnullを返すようにモックを設定
        when(detailsRepository.findFirstByIdGreaterThanOrderByIdAsc(anyInt())).thenReturn(null);

        // detailsService.getDetails()を呼び出し、結果を取得
        ResponseEntity<?> response = detailsService.getDetails(101);

        // ステータスコードがHttpStatus.OKであることをアサート
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // レスポンスボディをProductDetailsResponseにキャスト
        ProductDetailsResponse detailsResponse = (ProductDetailsResponse) response.getBody();
        // 更新日時が空文字列であることをアサート
        assertEquals("", detailsResponse.getProductUpdatedAt());
    }

}
