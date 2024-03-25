package com.kampus.kbazaar.product;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.kampus.kbazaar.exceptions.NotFoundException;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class ProductServiceTest {

    @Mock private ProductRepository productRepository;

    @InjectMocks private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("should be able to get all products")
    void shouldBeAbleToGetAllProducts() {
        int mockPage = 1;
        int mockLimit = 1000;
        Pageable mockPageable = PageRequest.of(mockPage - 1, mockLimit);
        // Mock data
        Product product1 =
                new Product(
                        1L,
                        "Google Pixel 5",
                        "MOBILE-GOOGLE-PIXEL-5",
                        new BigDecimal(12990.75),
                        100);
        Product product2 =
                new Product(2L, "Coca-Cola", "BEV-COCA-COLA", new BigDecimal(20.75), 150);
        List<Product> productList = List.of(product1, product2);

        // Mock repository method
        when(productRepository.findAll(mockPageable)).thenReturn(new PageImpl<>(productList));

        // Call service method
        List<ProductResponse> result = productService.getProductWithPagination(mockPage, mockLimit);

        // Assertions
        assertEquals(2, result.size());

        assertEquals("Google Pixel 5", result.get(0).name());
        assertEquals("BEV-COCA-COLA", result.get(1).sku());
    }

    @Test
    @DisplayName("should return product list with pagination")
    void shouldReturnProductWithPagination() {
        int mockPage = 1;
        int mockLimit = 2;
        Pageable mockPageable = PageRequest.of(0, 2);
        Product product1 =
                new Product(
                        1L,
                        "Google Pixel 5",
                        "MOBILE-GOOGLE-PIXEL-5",
                        new BigDecimal(12990.75),
                        100);
        Product product2 = new Product(2L, "OPPO Flip", "OPPO Flip", new BigDecimal(5000.75), 100);
        Product product3 =
                new Product(
                        3L,
                        "i-phone 15 pro max",
                        "I-PHONE-15-PRO-Max",
                        new BigDecimal(99990.75),
                        100);

        List<Product> productList = List.of(product1, product2);
        // Mock repository method
        when(productRepository.findAll(mockPageable)).thenReturn(new PageImpl<>(productList));

        List<ProductResponse> result = productService.getProductWithPagination(mockPage, mockLimit);

        assertEquals(mockLimit, result.size());
        assertEquals("Google Pixel 5", result.get(0).name());
        assertEquals("OPPO Flip", result.get(1).sku());
    }

    @Test
    @DisplayName("should return empty list when no product found")
    void shouldReturnEmptyListWhenNoProductFoundGetAllProducts() {
        int mockPage = 1;
        int mockLimit = 1000;
        Pageable mockPageable = PageRequest.of(mockPage - 1, mockLimit);
        // Mock repository method returning empty list
        when(productRepository.findAll(mockPageable))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // Call service method
        List<ProductResponse> result = productService.getProductWithPagination(mockPage, mockLimit);

        // Assertions
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("should be able to get product by SKU")
    void shouldBeAbleToGetProductBySku() {
        // Mock data
        Product product =
                new Product(1L, "Pens", "STATIONERY-PEN-BIC-BALLPOINT", new BigDecimal(14.99), 100);

        // Mock repository method
        when(productRepository.findBySku("STATIONERY-PEN-BIC-BALLPOINT"))
                .thenReturn(Optional.of(product));

        // Call service method
        ProductResponse result = productService.getBySku("STATIONERY-PEN-BIC-BALLPOINT");

        // Assertions
        assertEquals("Pens", result.name());
        assertEquals(new BigDecimal(14.99), result.price());
    }

    @Test
    @DisplayName("should return null when get product non-existing SKU")
    void shouldReturnNullWhenGetProductNonExistingSKU() {
        // Mock repository method returning empty optional
        when(productRepository.findBySku(anyString())).thenReturn(Optional.empty());

        // Assertions
        assertThrows(NotFoundException.class, () -> productService.getBySku("NonExistingSKU"));
    }
}
