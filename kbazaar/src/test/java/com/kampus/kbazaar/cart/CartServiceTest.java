package com.kampus.kbazaar.cart;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.kampus.kbazaar.cartItem.CartItemRepository;
import com.kampus.kbazaar.product.Product;
import com.kampus.kbazaar.product.ProductRepository;
import com.kampus.kbazaar.shopper.Shopper;
import com.kampus.kbazaar.shopper.ShopperRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CartServiceTest {
    @Mock private CartRepository cartRepository;
    @Mock private ProductRepository productRepository;
    @Mock private ShopperRepository shopperRepository;
    @Mock private CartItemRepository cartItemRepository;
    @InjectMocks private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("should return new cart of shopper after add product")
    public void shouldReturnNewCart() {
        String mockUserName = "test";
        Shopper shopper = new Shopper();
        shopper.setId(1L);
        shopper.setUsername(mockUserName);
        Product product = new Product();
        product.setId(1L);
        product.setPrice(BigDecimal.TEN);
        ProductDetailBody productDetailBody = new ProductDetailBody(1L, 3);
        when(shopperRepository.findByUsername(mockUserName)).thenReturn(Optional.of(shopper));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(cartRepository.findByShopper_Id(any())).thenReturn(Optional.empty());

        CartResponse actual = cartService.addProductToCart(mockUserName, productDetailBody);

        assertEquals(BigDecimal.valueOf(30), actual.total());
        assertEquals(1, actual.products().size());
        assertEquals(mockUserName, actual.userName());
        assertEquals(BigDecimal.ZERO, actual.discount());
    }
}
