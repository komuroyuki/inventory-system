package com.inventory.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.inventory.entity.Category;
import com.inventory.entity.Product;
import com.inventory.repository.ProductSearchRepository;
import com.inventory.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductSearchServiceTests {

    @Mock
    private ProductRepository repository;

    @Mock
    private ProductSearchRepository searchRepository;

    @InjectMocks
    private ProductSearchService filterAndSearchService;

    private Product product1;
    private Product product2;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1);
        category.setName("Test Category");

        product1 = new Product();
        product1.setId(101);
        product1.setName("Product A");
        product1.setCategoryId(category);

        product2 = new Product();
        product2.setId(102);
        product2.setName("Product B");
        product2.setCategoryId(category);
    }

    @Test
    @DisplayName("キーワードが空でカテゴリーIDも指定なしの場合、全商品を取得する")
    void filterAndSearch_emptyKeywordAndNullCategoryId_returnsAllProducts() {
        // repository.findAll()がproduct1とproduct2のリストを返すようにモックを設定
        when(repository.findAll()).thenReturn(Arrays.asList(product1, product2));

        // filterAndSearchService.filterAndSearch()を呼び出し、結果を取得
        List<Product> result = filterAndSearchService.filterAndSearch(null, "");

        // 結果がnullでないこと、サイズが2であることをアサート
        assertNotNull(result);
        assertEquals(2, result.size());
        // repository.findAll()が一度だけ呼ばれたことを検証
        verify(repository, times(1)).findAll();
        // searchRepositoryが一切呼び出されなかったことを検証
        verifyNoInteractions(searchRepository);
    }

    @Test
    @DisplayName("キーワードが空でカテゴリーIDが0の場合、全商品を取得する")
    void filterAndSearch_emptyKeywordAndZeroCategoryId_returnsAllProducts() {
        // repository.findAll()がproduct1とproduct2のリストを返すようにモックを設定
        when(repository.findAll()).thenReturn(Arrays.asList(product1, product2));

        // filterAndSearchService.filterAndSearch()を呼び出し、結果を取得
        List<Product> result = filterAndSearchService.filterAndSearch(0, "");

        // 結果がnullでないこと、サイズが2であることをアサート
        assertNotNull(result);
        assertEquals(2, result.size());
        // repository.findAll()が一度だけ呼ばれたことを検証
        verify(repository, times(1)).findAll();
        // searchRepositoryが一切呼び出されなかったことを検証
        verifyNoInteractions(searchRepository);
    }

    @Test
    @DisplayName("キーワードが空でカテゴリーIDが指定されている場合、そのカテゴリーの商品を取得する")
    void filterAndSearch_emptyKeywordAndValidCategoryId_returnsProductsByCategory() {
        // searchRepository.findByCategoryIdId(1)がproduct1とproduct2のリストを返すようにモックを設定
        when(searchRepository.findByCategoryIdId(1)).thenReturn(Arrays.asList(product1, product2));

        // filterAndSearchService.filterAndSearch()を呼び出し、結果を取得
        List<Product> result = filterAndSearchService.filterAndSearch(1, "");

        // 結果がnullでないこと、サイズが2であることをアサート
        assertNotNull(result);
        assertEquals(2, result.size());
        // searchRepository.findByCategoryIdId(1)が一度だけ呼ばれたことを検証
        verify(searchRepository, times(1)).findByCategoryIdId(1);
        // repositoryが一切呼び出されなかったことを検証
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("キーワードが空でカテゴリーIDが指定されているが、商品がない場合、空リストを返す")
    void filterAndSearch_emptyKeywordAndValidCategoryId_returnsEmptyList() {
        // searchRepository.findByCategoryIdId(1)が空のリストを返すようにモックを設定
        when(searchRepository.findByCategoryIdId(1)).thenReturn(Collections.emptyList());

        // filterAndSearchService.filterAndSearch()を呼び出し、結果を取得
        List<Product> result = filterAndSearchService.filterAndSearch(1, "");

        // 結果が空であることをアサート
        assertTrue(result.isEmpty());
        // searchRepository.findByCategoryIdId(1)が一度だけ呼ばれたことを検証
        verify(searchRepository, times(1)).findByCategoryIdId(1);
        // repositoryが一切呼び出されなかったことを検証
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("キーワードが指定されておりカテゴリーIDが指定されていない場合、キーワードで検索する")
    void filterAndSearch_validKeywordAndNullCategoryId_searchesByKeyword() {
        String keyword = "Product";
        // repository.findByNameContaining(keyword)がproduct1とproduct2のリストを返すようにモックを設定
        when(repository.findByNameContaining(keyword)).thenReturn(Arrays.asList(product1, product2));

        // filterAndSearchService.filterAndSearch()を呼び出し、結果を取得
        List<Product> result = filterAndSearchService.filterAndSearch(null, keyword);

        // 結果がnullでないこと、サイズが2であることをアサート
        assertNotNull(result);
        assertEquals(2, result.size());
        // repository.findByNameContaining(keyword)が一度だけ呼ばれたことを検証
        verify(repository, times(1)).findByNameContaining(keyword);
        // searchRepositoryが一切呼び出されなかったことを検証
        verifyNoInteractions(searchRepository);
    }

    @Test
    @DisplayName("キーワードが指定されておりカテゴリーIDが0の場合、キーワードで検索する")
    void filterAndSearch_validKeywordAndZeroCategoryId_searchesByKeyword() {
        String keyword = "Product";
        // repository.findByNameContaining(keyword)がproduct1とproduct2のリストを返すようにモックを設定
        when(repository.findByNameContaining(keyword)).thenReturn(Arrays.asList(product1, product2));

        // filterAndSearchService.filterAndSearch()を呼び出し、結果を取得
        List<Product> result = filterAndSearchService.filterAndSearch(0, keyword);

        // 結果がnullでないこと、サイズが2であることをアサート
        assertNotNull(result);
        assertEquals(2, result.size());
        // repository.findByNameContaining(keyword)が一度だけ呼ばれたことを検証
        verify(repository, times(1)).findByNameContaining(keyword);
        // searchRepositoryが一切呼び出されなかったことを検証
        verifyNoInteractions(searchRepository);
    }

    @Test
    @DisplayName("キーワードとカテゴリーIDが指定されている場合、両方でフィルタリング・検索する")
    void filterAndSearch_validKeywordAndCategoryId_filtersAndSearches() {
        String keyword = "A";
        // searchRepository.findByCategoryIdIdAndNameContaining(1,
        // keyword)がproduct1のリストを返すようにモックを設定
        when(searchRepository.findByCategoryIdIdAndNameContaining(1, keyword)).thenReturn(Arrays.asList(product1));

        // filterAndSearchService.filterAndSearch()を呼び出し、結果を取得
        List<Product> result = filterAndSearchService.filterAndSearch(1, keyword);

        // 結果がnullでないこと、サイズが1であることをアサート
        assertNotNull(result);
        assertEquals(1, result.size());
        // 結果の商品IDがproduct1のIDと一致することをアサート
        assertEquals(product1.getId(), result.get(0).getId());
        // searchRepository.findByCategoryIdIdAndNameContaining(1,
        // keyword)が一度だけ呼ばれたことを検証
        verify(searchRepository, times(1)).findByCategoryIdIdAndNameContaining(1, keyword);
        // repositoryが一切呼び出されなかったことを検証
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("キーワードとカテゴリーIDが指定されているが、該当商品がない場合、空リストを返す")
    void filterAndSearch_noMatchingProducts_returnsEmptyList() {
        String keyword = "NonExistent";
        // searchRepository.findByCategoryIdIdAndNameContaining(1,
        // keyword)が空のリストを返すようにモックを設定
        when(searchRepository.findByCategoryIdIdAndNameContaining(1, keyword)).thenReturn(Collections.emptyList());

        // filterAndSearchService.filterAndSearch()を呼び出し、結果を取得
        List<Product> result = filterAndSearchService.filterAndSearch(1, keyword);

        // 結果が空であることをアサート
        assertTrue(result.isEmpty());
        // searchRepository.findByCategoryIdIdAndNameContaining(1,
        // keyword)が一度だけ呼ばれたことを検証
        verify(searchRepository, times(1)).findByCategoryIdIdAndNameContaining(1, keyword);
        // repositoryが一切呼び出されなかったことを検証
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("キーワードに日本語が含まれる場合も正しく検索する")
    void filterAndSearch_japaneseKeyword_searchesCorrectly() {
        String keyword = "商品";
        // repository.findByNameContaining(keyword)がproduct1とproduct2のリストを返すようにモックを設定
        when(repository.findByNameContaining(keyword)).thenReturn(Arrays.asList(product1, product2));

        // filterAndSearchService.filterAndSearch()を呼び出し、結果を取得
        List<Product> result = filterAndSearchService.filterAndSearch(null, keyword);

        // 結果がnullでないこと、サイズが2であることをアサート
        assertNotNull(result);
        assertEquals(2, result.size());
        // repository.findByNameContaining(keyword)が一度だけ呼ばれたことを検証
        verify(repository, times(1)).findByNameContaining(keyword);
        // searchRepositoryが一切呼び出されなかったことを検証
        verifyNoInteractions(searchRepository);
    }

    @Test
    @DisplayName("カテゴリーIDが指定され、キーワードが空の場合、カテゴリーの商品を返す")
    void filterAndSearch_emptyKeywordAndCategoryId_returnsProductsByCategory() {
        // searchRepository.findByCategoryIdId(1)がproduct1とproduct2のリストを返すようにモックを設定
        when(searchRepository.findByCategoryIdId(1)).thenReturn(Arrays.asList(product1, product2));

        // filterAndSearchService.filterAndSearch()を呼び出し、結果を取得
        List<Product> result = filterAndSearchService.filterAndSearch(1, "");

        // 結果がnullでないこと、サイズが2であることをアサート
        assertNotNull(result);
        assertEquals(2, result.size());
        // searchRepository.findByCategoryIdId(1)が一度だけ呼ばれたことを検証
        verify(searchRepository, times(1)).findByCategoryIdId(1);
        // repositoryが一切呼び出されなかったことを検証
        verifyNoInteractions(repository);
    }

}
