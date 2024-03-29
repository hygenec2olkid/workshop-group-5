package com.kampus.kbazaar.cart;

import org.springframework.http.HttpStatusCode;
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
    public ResponseEntity addProductsToCart(
            @PathVariable String username, @RequestBody ProductDetailBody productDetailBody) {
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).build();
    }

    @PostMapping("/carts/{username}/items-mock")
    public String addProductsToCartReal(
            @PathVariable String username, @RequestBody ProductDetailBody productDetailBody) {
        return this.cartService.addProductToCart(username, productDetailBody);
    }
}

record ProductDetailBody(Long productSku, int quantity) {}
