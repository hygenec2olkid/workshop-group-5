package com.kampus.kbazaar.cartItem;

import com.kampus.kbazaar.cart.Cart;
import com.kampus.kbazaar.cart.CartRepository;
import com.kampus.kbazaar.cart.CartService;
import com.kampus.kbazaar.exceptions.NotFoundException;
import com.kampus.kbazaar.product.Product;
import com.kampus.kbazaar.product.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    public CartItemService(
            CartItemRepository cartItemRepository,
            CartRepository cartRepository,
            ProductRepository productRepository,
            CartService cartService) {
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    public String deleteCartItem(String username, String productSku) {
        Cart cart =
                this.cartRepository
                        .findByShopper_name(username)
                        .orElseThrow(
                                () -> new NotFoundException(username + " not have any cartItem"));

        Product product =
                this.productRepository
                        .findBySku(productSku)
                        .orElseThrow(
                                () -> new NotFoundException("Not found product: " + productSku));

        CartItem cartItem =
                this.cartItemRepository
                        .findByCartIdAndProductId(cart.getId(), product.getId())
                        .orElseThrow(() -> new NotFoundException("Product not found in the cart"));

        this.cartItemRepository.delete(cartItem);
        this.cartService.updateTotal(cart);

        return "Delete Success";
    }
}
