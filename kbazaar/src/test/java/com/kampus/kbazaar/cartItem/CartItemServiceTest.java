package com.kampus.kbazaar.cartItem;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.kampus.kbazaar.cart.Cart;
import com.kampus.kbazaar.cart.CartService;
import com.kampus.kbazaar.exceptions.NotFoundException;
import com.kampus.kbazaar.product.Product;
import com.kampus.kbazaar.product.ProductService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CartItemServiceTest {
    @Mock private CartItemRepository cartItemRepository;
    @Mock private ProductService productService;
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

        when(cartService.findCart(username)).thenReturn(cart);
        when(productService.getProductBySku(productSku)).thenReturn(product);
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L))
                .thenReturn(Optional.of(cartItem1));

        String actual = cartItemService.deleteCartItem(username, productSku);

        assertEquals("Delete Success", actual);
    }

    @Test
    @DisplayName("should return List of cart item")
    void shouldReturnListOfCartItem() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        List<CartItem> cartItemList = List.of(cartItem);

        when(cartItemRepository.findByCart_Id(1L)).thenReturn(Optional.of(cartItemList));

        List<CartItem> actual = cartItemService.findCartItemByCardId(1L);

        assertEquals(1L, actual.get(0).getId());
    }

    @Test
    @DisplayName("should return cart item")
    void shouldReturnCartItem() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);

        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(Optional.of(cartItem));

        CartItem actual = cartItemService.findByCartIdAndProductId(1L, 1L);

        assertEquals(1L, actual.getId());
    }

    @Test
    @DisplayName("should throw not found  cart item")
    void shouldThrowNotFoundCartItem() {

        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(Optional.empty());

        Exception actual =
                assertThrows(
                        NotFoundException.class,
                        () -> cartItemService.findByCartIdAndProductId(1L, 1L));

        assertEquals("Not found cartItem of this product", actual.getMessage());
    }

    @Test
    @DisplayName("should throw not found list of cart item")
    void shouldThrowNotFoundListOfCartItem() {

        when(cartItemRepository.findByCart_Id(1L)).thenReturn(Optional.empty());

        Exception actual =
                assertThrows(
                        NotFoundException.class, () -> cartItemService.findCartItemByCardId(1L));

        assertEquals("Not found list of cartItem", actual.getMessage());
    }
}
