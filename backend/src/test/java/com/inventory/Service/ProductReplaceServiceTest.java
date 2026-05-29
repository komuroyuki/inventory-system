package com.inventory.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.inventory.DTO.ProductRequest;
import com.inventory.Entity.Category;
import com.inventory.Entity.Product;
import com.inventory.Repository.CategoryRepository;
import com.inventory.Repository.ProductRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductReplaceServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductReplaceService productReplaceService;

    private Product product;
    private Category category;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1);
        product.setName("Old Product Name");
        product.setQuantity(10);
        product.setImage("old_image.jpg");

        category = new Category();
        category.setId(1);
        category.setName("Test Category");

        productRequest = new ProductRequest("New Product Name", 20, "new_image.jpg", 1);

        // productエンティティにCategoryを設定
        product.setCategoryId(category);
    }

    @Test
    @DisplayName("カテゴリIDが存在しない場合、BAD_REQUESTを返す")
    void replaceProduct_categoryNotFound_returnsBadRequest() {
        // categoryRepository.findById()が空のOptionalを返すようにモックを設定
        when(categoryRepository.findById(anyInt())).thenReturn(Optional.empty());

        // productReplaceService.replaceProduct()を呼び出し、結果を取得
        ResponseEntity<?> response = productReplaceService.replaceProduct(productRequest, 1);

        // ステータスコードがHttpStatus.BAD_REQUESTであることをアサート
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        // レスポンスボディが期待通りのエラーメッセージであることをアサート
        assertEquals("カテゴリIDが存在しません。\n", response.getBody());
        // categoryRepository.findById()が一度だけ呼ばれたことを検証
        verify(categoryRepository, times(1)).findById(productRequest.categoryId());
        // productRepositoryが一切呼び出されなかったことを検証
        verifyNoInteractions(productRepository);
    }

    @Test
    @DisplayName("IDが存在しない場合、NOT_FOUNDを返す")
    void replaceProduct_productNotFound_returnsNotFound() {
        // categoryRepository.findById()がcategoryを返すようにモックを設定
        when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(category));
        // productRepository.findById()が空のOptionalを返すようにモックを設定
        when(productRepository.findById(anyInt())).thenReturn(Optional.empty());

        // productReplaceService.replaceProduct()を呼び出し、結果を取得
        ResponseEntity<?> response = productReplaceService.replaceProduct(productRequest, 1);

        // ステータスコードがHttpStatus.NOT_FOUNDであることをアサート
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        // レスポンスボディが期待通りのエラーメッセージであることをアサート
        assertEquals("IDが存在しません。\n", response.getBody());
        // categoryRepository.findById()が一度だけ呼ばれたことを検証
        verify(categoryRepository, times(1)).findById(productRequest.categoryId());
        // productRepository.findById()が一度だけ呼ばれたことを検証
        verify(productRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("製品情報が正常に更新された場合、OKと更新後の製品を返す")
    void replaceProduct_success_returnsOkAndUpdatedProduct() {
        // categoryRepository.findById()がcategoryを返すようにモックを設定
        when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(category));
        // productRepository.findById()がproductを返すようにモックを設定
        when(productRepository.findById(anyInt())).thenReturn(Optional.of(product));
        // productRepository.save()が引数として渡されたProductを返すように設定
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            savedProduct.setId(1); // IDを設定して返す
            return savedProduct;
        });

        // productReplaceService.replaceProduct()を呼び出し、結果を取得
        ResponseEntity<?> response = productReplaceService.replaceProduct(productRequest, 1);

        // ステータスコードがHttpStatus.OKであることをアサート
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // レスポンスボディがProduct型であることをアサート
        assertTrue(response.getBody() instanceof Product);

        // レスポンスボディをProductにキャスト
        Product updatedProduct = (Product) response.getBody();
        // 更新された製品の各フィールドが期待通りであることをアサート
        assertEquals("New Product Name", updatedProduct.getName());
        assertEquals(20, updatedProduct.getQuantity());
        assertEquals("new_image.jpg", updatedProduct.getImage());
        assertEquals(category.getId(), updatedProduct.getCategoryId().getId());

        // categoryRepository.findById()が一度だけ呼ばれたことを検証
        verify(categoryRepository, times(1)).findById(productRequest.categoryId());
        // productRepository.findById()が一度だけ呼ばれたことを検証
        verify(productRepository, times(1)).findById(1);
        // productRepository.save()が一度だけ呼ばれたことを検証
        verify(productRepository, times(1)).save(any(Product.class));
    }
}
