package com.kampus.kbazaar.promotion;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.kampus.kbazaar.cart.Cart;
import com.kampus.kbazaar.cart.CartRepository;
import com.kampus.kbazaar.cart.CartResponse;
import com.kampus.kbazaar.cart.bodyReq.RequestBodyCode;
import com.kampus.kbazaar.cartItem.CartItem;
import com.kampus.kbazaar.cartItem.CartItemRepository;
import com.kampus.kbazaar.exceptions.NotFoundException;
import com.kampus.kbazaar.exceptions.PromoCodeExpiredException;
import com.kampus.kbazaar.exceptions.PromoCodeNotApplicableException;
import com.kampus.kbazaar.product.Product;
import com.kampus.kbazaar.product.ProductRepository;
import com.kampus.kbazaar.shopper.Shopper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PromotionServiceTest {

    @Mock private PromotionRepository promotionRepository;
    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private ProductRepository productRepository;
    @InjectMocks private PromotionService promotionService;
    private Promotion promotion;
    private Shopper shopper;
    private Cart cart;
    private CartItem cartItem;
    private String product_skus;
    private Product product;
    private RequestBodyCode req;

    @BeforeEach
    void setUp() {
        promotion = new Promotion();
        promotion.setCode("PROMO1");
        promotion.setStartDate(LocalDateTime.now().minusDays(1));
        promotion.setEndDate(LocalDateTime.now().plusDays(1));
        promotion.setDiscountType("FIXED_AMOUNT");
        promotion.setDiscountAmount(BigDecimal.TEN);

        shopper = new Shopper();
        shopper.setUsername("username");

        cart = new Cart();
        cart.setId(1L);
        cart.setShopper(shopper);
        cart.setTotal(BigDecimal.valueOf(100));
        cart.setDiscount(BigDecimal.ZERO);

        product = new Product();
        product.setId(1L);
        product.setSku("PRODUCT-SKUS1");
        product.setName("PRODUCT-NAME1");

        cartItem = new CartItem();
        cartItem.setDiscount(BigDecimal.ZERO);
        cartItem.setSubTotal(BigDecimal.valueOf(100));
        cartItem.setPromotionCode("");
        cartItem.setProduct(product);
        cartItem.setProductSku("PRODUCT-SKUS1");
        cartItem.setQuantity(1);
        cartItem.setCart(cart);

        cart.setCartItemList(List.of(cartItem));

        product_skus = "PRODUCT-SKUS1,PRODUCT-SKUS2";

        req = new RequestBodyCode("PROMO1", Optional.of("PRODUCT-SKUS1"));
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("should be able to get all promotions")
    void shouldBeAbleToGetAllPromotion() {
        // Arrange
        Promotion promotion1 = new Promotion();
        Promotion promotion2 = new Promotion();
        when(promotionRepository.findAll()).thenReturn(Arrays.asList(promotion1, promotion2));

        // Act
        List<PromotionResponse> promotionResponses = promotionService.getAll();

        // Assert
        assertEquals(2, promotionResponses.size());
    }

    @Test
    @DisplayName("should be able to get promotion by code")
    void getPromotionByCode_ExistingCode_ShouldReturnPromotionResponse() {
        // Arrange
        String code = "BUY2GET1FREE";
        Promotion promotion = new Promotion();
        promotion.setCode(code);
        when(promotionRepository.findByCode(code)).thenReturn(Optional.of(promotion));

        // Act
        PromotionResponse promotionResponse = promotionService.getPromotionByCode(code);

        // Assert
        assertNotNull(promotionResponse);
        assertEquals(code, promotionResponse.code());
    }

    @Test
    @DisplayName("should throw NotFoundException when promotion code not found")
    void getPromotionByCode_NonExistingCode_ShouldThrowNotFoundException() {
        // Arrange
        String code = "NON-EXISTING-CODE";
        when(promotionRepository.findByCode(code)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> promotionService.getPromotionByCode(code));
    }

    @Test
    @DisplayName("should return cart response after use promotion code specific success")
    void testHandleUsePromoSpecific_Success() {
        when(promotionRepository.findByCode(req.code())).thenReturn(Optional.of(promotion));
        when(promotionRepository.findProductSkuByCode(req.code()))
                .thenReturn(Optional.of(product_skus));
        when(cartRepository.findByShopper_name("username")).thenReturn(Optional.of(cart));
        when(productRepository.findBySku(req.productSkus().get())).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.findByCartId(1L)).thenReturn(List.of(cartItem));

        CartResponse actual = promotionService.handleUsePromoSpecific("username", req);

        assertEquals(BigDecimal.TEN, actual.discount());
        assertEquals(BigDecimal.valueOf(90), actual.total());
        verify(cartItemRepository, times(1)).save(cartItem);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testThrowNotFoundException_Promotion() {
        RequestBodyCode req = new RequestBodyCode("PROMO1", Optional.of("PRODUCT-SKUS1"));
        when(promotionRepository.findByCode(req.code())).thenReturn(Optional.empty());

        Exception actual =
                assertThrows(
                        NotFoundException.class,
                        () -> promotionService.handleUsePromoSpecific("test", req));

        String expected = String.format("not found promoCode: %s", "PROMO1");
        assertEquals(expected, actual.getMessage());
    }

    @Test
    void testThrowPromotionNotApplied() {
        req = new RequestBodyCode("PROMO3", Optional.of("PRODUCT-SKUS3"));
        when(promotionRepository.findByCode(req.code())).thenReturn(Optional.of(promotion));
        when(cartRepository.findByShopper_name("test")).thenReturn(Optional.of(cart));
        when(productRepository.findBySku(req.productSkus().get())).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(Optional.of(cartItem));
        when(promotionRepository.findProductSkuByCode(req.code()))
                .thenReturn(Optional.of(product_skus));

        Exception actual =
                assertThrows(
                        PromoCodeNotApplicableException.class,
                        () -> promotionService.handleUsePromoSpecific("test", req));

        String expected =
                String.format("Can't use this promoCode for product %s", req.productSkus().get());
        assertEquals(expected, actual.getMessage());
    }

    @Test
    void testThrowPromoCodeExpiredException() {
        promotion.setStartDate(LocalDateTime.now().minusDays(2));
        promotion.setEndDate(LocalDateTime.now().minusDays(1));

        when(cartRepository.findByShopper_name("test")).thenReturn(Optional.of(cart));
        when(productRepository.findBySku(req.productSkus().get())).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(Optional.of(cartItem));
        when(promotionRepository.findByCode(req.code())).thenReturn(Optional.of(promotion));
        when(promotionRepository.findProductSkuByCode(req.code()))
                .thenReturn(Optional.of(product_skus));

        Exception actual =
                assertThrows(
                        PromoCodeExpiredException.class,
                        () -> promotionService.handleUsePromoSpecific("test", req));

        String expected = "promotion code is expire";
        assertEquals(expected, actual.getMessage());
    }

    @Test
    void testThrowNotFoundException_findProductSkuByCode() {
        when(promotionRepository.findProductSkuByCode(req.code())).thenReturn(Optional.empty());

        Exception actual =
                assertThrows(
                        NotFoundException.class, () -> promotionService.checkCodeAvailable(req));

        String expected = String.format("not found promotionCode: %s", req.code());
        assertEquals(expected, actual.getMessage());
    }

    @Test
    void testThrowNotFoundException_findCartItem_cart() {
        when(cartRepository.findByShopper_name("test")).thenReturn(Optional.empty());

        Exception actual =
                assertThrows(
                        NotFoundException.class, () -> promotionService.findCartItem("test", req));

        String expected = "user: test not have cart yet";
        assertEquals(expected, actual.getMessage());
    }

    @Test
    void testThrowNotFoundException_findCartItem_product() {
        when(cartRepository.findByShopper_name("test")).thenReturn(Optional.of(cart));
        when(productRepository.findBySku(req.productSkus().get())).thenReturn(Optional.empty());

        Exception actual =
                assertThrows(
                        NotFoundException.class, () -> promotionService.findCartItem("test", req));

        String expected = String.format("not found productSku: %s", req.productSkus().get());
        assertEquals(expected, actual.getMessage());
    }

    @Test
    void testThrowNotFoundException_findCartItem_cartItem() {
        when(promotionRepository.findByCode("PROMO1")).thenReturn(Optional.of(promotion));
        when(cartRepository.findByShopper_name("test")).thenReturn(Optional.of(cart));
        when(productRepository.findBySku("PRODUCT-SKUS1")).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(Optional.empty());

        Exception actual =
                assertThrows(
                        NotFoundException.class,
                        () -> promotionService.handleUsePromoSpecific("test", req));

        String expected = "test not have this product in cart";
        assertEquals(expected, actual.getMessage());
    }
}
