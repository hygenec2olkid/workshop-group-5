package com.kampus.kbazaar.promotion;

import com.kampus.kbazaar.cart.Cart;
import com.kampus.kbazaar.cart.CartRepository;
import com.kampus.kbazaar.cart.CartResponse;
import com.kampus.kbazaar.cart.bodyReq.RequestBodyCode;
import com.kampus.kbazaar.cartItem.CartItem;
import com.kampus.kbazaar.cartItem.CartItemRepository;
import com.kampus.kbazaar.exceptions.NotFoundException;
import com.kampus.kbazaar.exceptions.PromoCodeExpiredException;
import com.kampus.kbazaar.exceptions.PromoCodeNotApplicableException;
import com.kampus.kbazaar.product.Product;
import com.kampus.kbazaar.product.ProductRepository;
import com.kampus.kbazaar.shopper.ShopperRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
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

    public CartResponse handleUsePromoSpecific(String username, RequestBodyCode req) {
        Promotion promotion =
                this.promotionRepository
                        .findByCode(req.code())
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                String.format(
                                                        "not found promoCode: %s", req.code())));

        if (checkProductInCart(req)) {
            if (validateTimeAvailable(promotion)) {
                CartItem cartItem = findCartItem(username, req);
                updatePriceByDiscountType(promotion, cartItem);
                return cartItem.getCart().toResponse();
            }
            throw new PromoCodeExpiredException("promotion code is expire");
        }
        throw new PromoCodeNotApplicableException(
                "Can't use this promoCode for product " + req.productSkus());
    }

    public boolean checkProductInCart(RequestBodyCode req) {
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

    public boolean validateTimeAvailable(Promotion promo) {
        LocalDate localDate = LocalDate.now();

        LocalDate startDate = LocalDate.from(promo.getStartDate());
        LocalDate endDate = LocalDate.from(promo.getEndDate());

        return localDate.isAfter(startDate) && localDate.isBefore(endDate);
    }

    public void updatePriceByDiscountType(Promotion promo, CartItem cartItem) {
        switch (promo.getDiscountType()) {
            case "FIXED_AMOUNT":
                BigDecimal discount = promo.getDiscountAmount();
                cartItem.setDiscount(discount);
                cartItem.setPromotionCode(promo.getCode());
                this.cartItemRepository.save(cartItem);
                updateCartTotal(cartItem.getCart(), promo);
                break;
            case "PERCENTAGE":
                break;
            case "buy1_get1":
                break;
            case "buy2_get1":
                break;
        }
    }

    public void updateCartTotal(Cart cart, Promotion promo) {
        List<CartItem> cartItemList = this.cartItemRepository.findByCartId(cart.getId());

        BigDecimal discount =
                cartItemList.stream()
                        .map(CartItem::getDiscount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal total =
                cartItemList.stream()
                        .map(CartItem::getSubTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotal(total.subtract(discount));
        cart.setDiscount(discount);
        cart.setPromotion(promo);
        this.cartRepository.save(cart);
        cart.toResponse();
    }

    public CartItem findCartItem(String username, RequestBodyCode req) {
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

        return this.cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseThrow(
                        () ->
                                new NotFoundException(
                                        String.format(
                                                "%s not have this product in cart", username)));
    }
}
