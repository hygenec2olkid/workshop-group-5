package com.kampus.kbazaar.cart;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/carts")
    public ResponseEntity getCart() { // NOSONAR
        return ResponseEntity.ok().build();
    }

    @PostMapping("/carts/{username}/items")
    public ResponseEntity<CartResponse> addProductsToCart(
            @PathVariable String username, @RequestBody ProductDetailBody productDetailBody) {
        return new ResponseEntity<>(
                this.cartService.addProductToCart(username, productDetailBody), HttpStatus.CREATED);
    }
}

record ProductDetailBody(Long productSku, int quantity) {}
