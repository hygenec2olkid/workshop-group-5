package com.kampus.kbazaar.cart;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.kampus.kbazaar.cartItem.CartItem;
import com.kampus.kbazaar.cartItem.CartItemRepository;
import com.kampus.kbazaar.exceptions.NotFoundException;
import com.kampus.kbazaar.product.Product;
import com.kampus.kbazaar.product.ProductRepository;
import com.kampus.kbazaar.shopper.Shopper;
import com.kampus.kbazaar.shopper.ShopperRepository;
import java.math.BigDecimal;
import java.util.List;
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

    @Mock private CartService mockedCartService;
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

    @Test
    @DisplayName("should return new cartItem of shopper after add product")
    public void shouldReturnNewCartItem() {
        String mockUserName = "test";
        Shopper shopper = new Shopper();
        shopper.setId(10L);
        shopper.setUsername(mockUserName);
        Product product = new Product();
        product.setId(10L);
        product.setPrice(BigDecimal.TEN);
        ProductDetailBody productDetailBody = new ProductDetailBody(1L, 3);
        when(shopperRepository.findByUsername(mockUserName)).thenReturn(Optional.of(shopper));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        Cart cartShopper = new Cart();
        cartShopper.setId(10L);
        cartShopper.setShopper(shopper);
        cartShopper.setDiscount(BigDecimal.ZERO);

        CartItem cartItem1 = new CartItem();
        cartItem1.setId(10L);
        cartItem1.setQuantity(1);
        Product product1 = new Product();
        product1.setId(1L);
        cartItem1.setProduct(product1);

        CartItem cartItem2 = new CartItem();
        cartItem2.setId(20L);
        cartItem2.setQuantity(2);
        Product product2 = new Product();
        product2.setId(2L);
        cartItem2.setProduct(product2);

        cartShopper.setCartItemList(List.of(cartItem1, cartItem2));
        when(cartRepository.findByShopper_Id(any())).thenReturn(Optional.of(cartShopper));
        when(cartItemRepository.findByCartIdAndProductId(any(), any()))
                .thenReturn(Optional.empty());
        CartItem cartItemAfterSave = new CartItem();
        cartItemAfterSave.setProduct(product);
        cartItemAfterSave.setCart(cartShopper);
        cartItemAfterSave.setQuantity(productDetailBody.quantity());
        when(cartItemRepository.findByCartId(any()))
                .thenReturn(List.of(cartItem1, cartItem2, cartItemAfterSave));
        Product productSum = new Product();
        productSum.setPrice(BigDecimal.TEN);
        when(productRepository.findByProductId(any())).thenReturn(productSum);

        CartResponse actual = cartService.addProductToCart(mockUserName, productDetailBody);

        assertEquals(BigDecimal.valueOf(60), actual.total());
        assertEquals(mockUserName, actual.userName());
        assertEquals(BigDecimal.ZERO, actual.discount());
    }

    @Test
    @DisplayName("should return updated cartItem of shopper after add product")
    public void shouldReturnUpdatedCartItem() {
        String mockUserName = "test";
        Shopper shopper = new Shopper();
        shopper.setId(10L);
        shopper.setUsername(mockUserName);
        Product product = new Product();
        product.setId(10L);
        product.setPrice(BigDecimal.TEN);
        ProductDetailBody productDetailBody = new ProductDetailBody(1L, 3);
        when(shopperRepository.findByUsername(mockUserName)).thenReturn(Optional.of(shopper));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        Cart cartShopper = new Cart();
        cartShopper.setId(10L);
        cartShopper.setShopper(shopper);
        cartShopper.setDiscount(BigDecimal.ZERO);

        CartItem cartItemThatFound = new CartItem();
        cartItemThatFound.setId(10L);
        cartItemThatFound.setQuantity(2);
        cartItemThatFound.setProduct(product);

        cartShopper.setCartItemList(List.of(cartItemThatFound));

        when(cartRepository.findByShopper_Id(any())).thenReturn(Optional.of(cartShopper));
        when(cartItemRepository.findByCartIdAndProductId(any(), any()))
                .thenReturn(Optional.of(cartItemThatFound));
        CartItem cartItemAfterSave = new CartItem();
        cartItemAfterSave.setProduct(product);
        cartItemAfterSave.setCart(cartShopper);
        cartItemAfterSave.setQuantity(
                cartItemThatFound.getQuantity() + productDetailBody.quantity());
        when(cartItemRepository.findByCartId(any())).thenReturn(List.of(cartItemAfterSave));
        Product productSum = new Product();
        productSum.setPrice(BigDecimal.TEN);
        when(productRepository.findByProductId(any())).thenReturn(productSum);

        CartResponse actual = cartService.addProductToCart(mockUserName, productDetailBody);

        assertEquals(BigDecimal.valueOf(50), actual.total());
        assertEquals(mockUserName, actual.userName());
        assertEquals(BigDecimal.ZERO, actual.discount());
    }

    @Test
    @DisplayName("should throw error not found exception when not found shopper")
    public void shouldThrowNotFoundExceptionIfNotFoundShopper() {
        String mockUsername = "test";
        when(shopperRepository.findByUsername(mockUsername)).thenReturn(Optional.empty());
        ProductDetailBody productDetailBody = new ProductDetailBody(1L, 3);
        String expected = mockUsername + " is not member of Kbazaar";

        Exception actual =
                assertThrows(
                        NotFoundException.class,
                        () -> cartService.addProductToCart(mockUsername, productDetailBody));

        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("should throw error not found exception when not found product")
    public void shouldThrowNotFoundExceptionIfNotFoundProduct() {
        String mockUsername = "test";
        ProductDetailBody productDetailBody = new ProductDetailBody(1L, 3);
        Shopper shopper = new Shopper();
        shopper.setId(1L);

        when(shopperRepository.findByUsername(mockUsername)).thenReturn(Optional.of(shopper));
        when(productRepository.findById(productDetailBody.productSku()))
                .thenReturn(Optional.empty());
        String expected = productDetailBody.productSku() + " is not have in product";

        Exception actual =
                assertThrows(
                        NotFoundException.class,
                        () -> cartService.addProductToCart(mockUsername, productDetailBody));

        assertEquals(expected, actual.getMessage());
    }

    @Test
    public void testUpdateTotal() {
        Cart cart = new Cart();
        cart.setId(1L);
        CartItem cartItem1 = new CartItem();
        cartItem1.setId(1L);
        cartItem1.setProduct(new Product());
        cartItem1.setQuantity(2);
        CartItem cartItem2 = new CartItem();
        cartItem2.setId(2L);
        cartItem2.setProduct(new Product());
        cartItem2.setQuantity(3);
        List<CartItem> cartItemList = List.of(cartItem1, cartItem2);
        when(cartItemRepository.findByCartId(any())).thenReturn(cartItemList);
        Product product = new Product();
        product.setPrice(BigDecimal.TEN);
        when(productRepository.findByProductId(any())).thenReturn(product);

        cartService.updateTotal(cart);

        assertEquals(BigDecimal.valueOf(50), cart.getTotal());
    }
}