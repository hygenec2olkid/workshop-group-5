package com.kampus.kbazaar.cart;

import com.kampus.kbazaar.cart.bodyReq.ProductDetailBody;
import com.kampus.kbazaar.cart.bodyReq.RequestBodyCode;
import com.kampus.kbazaar.cartItem.CartItemService;
import com.kampus.kbazaar.promotion.PromotionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CartController {
    private final CartService cartService;
    private final PromotionService promotionService;
    private final CartItemService cartItemService;

    public CartController(
            CartService cartService,
            PromotionService promotionService,
            CartItemService cartItemService) {
        this.cartService = cartService;
        this.promotionService = promotionService;
        this.cartItemService = cartItemService;
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

    @GetMapping("/carts/{username}")
    public CartResponse getCartByUsername(@PathVariable String username) {
        return this.cartService.getCartByUsername(username);
    }

    @PostMapping("/carts/{username}/promotions")
    public CartResponse usePromoSpecific(
            @PathVariable String username, @RequestBody RequestBodyCode reqBody) {
        return this.promotionService.handleUsePromo(username, reqBody);
    }

    @DeleteMapping("/carts/{username}/items/{productSku}")
    public String deleteCartItem(@PathVariable String username, @PathVariable String productSku) {
        return this.cartItemService.deleteCartItem(username, productSku);
    }
}
