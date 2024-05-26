package com.kampus.kbazaar.cartItem;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.kampus.kbazaar.cart.Cart;
import com.kampus.kbazaar.cart.CartRepository;
import com.kampus.kbazaar.cart.CartService;
import com.kampus.kbazaar.exceptions.NotFoundException;
import com.kampus.kbazaar.product.Product;
import com.kampus.kbazaar.product.ProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CartItemServiceTest {
    @Mock private CartItemRepository cartItemRepository;
    @Mock private CartRepository cartRepository;
    @Mock private ProductRepository productRepository;
    @Mock private CartService cartService;
    @InjectMocks private CartItemService cartItemService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteCartItem() {
        String username = "TEST";
        String productSku = "PRODUCT_SKU";

        Cart cart = new Cart();
        cart.setId(1L);

        CartItem cartItem1 = new CartItem();
        cartItem1.setId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setSku("PRODUCT_SKU");

        cartItem1.setProduct(product);

        when(cartRepository.findByShopper_name(username)).thenReturn(Optional.of(cart));
        when(productRepository.findBySku(productSku)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L))
                .thenReturn(Optional.of(cartItem1));

        String actual = cartItemService.deleteCartItem(username, productSku);

        assertEquals("Delete Success", actual);
    }

    @Test
    void shouldThrowExceptionNotFound() {
        String username = "TEST";
        String productSku = "PRODUCT_SKU";

        Cart cart = new Cart();
        cart.setId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setSku("PRODUCT_SKU");
        when(cartRepository.findByShopper_name(username)).thenReturn(Optional.of(cart));
        when(productRepository.findBySku(productSku)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(Optional.empty());

        Exception actual =
                assertThrows(
                        NotFoundException.class,
                        () -> cartItemService.deleteCartItem(username, productSku));

        assertEquals("Product not found in the cart", actual.getMessage());
    }
}
