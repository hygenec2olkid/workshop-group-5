package com.kampus.kbazaar.promotion;

import com.kampus.kbazaar.cart.Cart;
import com.kampus.kbazaar.cart.CartRepository;
import com.kampus.kbazaar.cart.bodyReq.RequestBodyCode;
import com.kampus.kbazaar.cartItem.CartItem;
import com.kampus.kbazaar.cartItem.CartItemRepository;
import com.kampus.kbazaar.exceptions.NotFoundException;
import com.kampus.kbazaar.product.Product;
import com.kampus.kbazaar.product.ProductRepository;
import com.kampus.kbazaar.shopper.ShopperRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PromotionService {
    private PromotionRepository promotionRepository;

    private ShopperRepository shopperRepository;

    private CartRepository cartRepository;

    private CartItemRepository cartItemRepository;

    private ProductRepository productRepository;

    public PromotionService(
            PromotionRepository promotionRepository,
            ShopperRepository shopperRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository) {
        this.promotionRepository = promotionRepository;
        this.shopperRepository = shopperRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public List<PromotionResponse> getAll() {
        return promotionRepository.findAll().stream().map(Promotion::toResponse).toList();
    }

    public PromotionResponse getPromotionByCode(String code) {
        return promotionRepository
                .findByCode(code)
                .map(Promotion::toResponse)
                .orElseThrow(() -> new NotFoundException("Promotion not found"));
    }

    public String handleUsePromoSpecific(String username, RequestBodyCode req) {
        if (checkProductInCart(username, req)) {
            return "true";
        }
        return "false";
    }

    private boolean checkProductInCart(String username, RequestBodyCode req) {
        Cart cart =
                this.cartRepository
                        .findByShopper_name(username)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                String.format(
                                                        "user: %s not have cart yet", username)));

        Product product =
                this.productRepository
                        .findBySku(req.productSkus())
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                String.format(
                                                        "not found productSku: %s",
                                                        req.productSkus())));

        CartItem cartItem =
                this.cartItemRepository
                        .findByCartIdAndProductId(cart.getId(), product.getId())
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                String.format(
                                                        "%s not have this product in cart",
                                                        username)));

        String product_skus =
                this.promotionRepository
                        .findProductSkuByCode(req.code())
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                String.format(
                                                        "not found promotionCode: %s",
                                                        req.code())));

        return product_skus.contains(req.productSkus());
    }
}
