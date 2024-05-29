package com.kampus.kbazaar.cartItem;

import com.kampus.kbazaar.cart.Cart;
import com.kampus.kbazaar.cart.CartService;
import com.kampus.kbazaar.exceptions.NotFoundException;
import com.kampus.kbazaar.product.Product;
import com.kampus.kbazaar.product.ProductService;
import java.util.List;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final CartService cartService;

    public CartItemService(
            CartItemRepository cartItemRepository,
            ProductService productService,
            @Lazy CartService cartService) {
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
        this.cartService = cartService;
    }

    public String deleteCartItem(String username, String productSku) {
        Cart cart = this.cartService.findCart(username);

        Product product = this.productService.getProductBySku(productSku);

        CartItem cartItem =
                this.cartItemRepository
                        .findByCartIdAndProductId(cart.getId(), product.getId())
                        .orElseThrow(() -> new NotFoundException("Product not found in the cart"));

        this.cartItemRepository.delete(cartItem);
        this.cartService.updateTotal(cart);

        return "Delete Success";
    }

    public List<CartItem> findCartItemByCardId(Long id) {
        return this.cartItemRepository
                .findByCart_Id(id)
                .orElseThrow(() -> new NotFoundException("Not found list of cartItem"));
    }

    public CartItem findByCartIdAndProductId(Long cartId, Long productId) {
        return this.cartItemRepository
                .findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new NotFoundException("Not found cartItem of this product"));
    }
}
