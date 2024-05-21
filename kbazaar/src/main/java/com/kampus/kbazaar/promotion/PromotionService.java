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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class PromotionService {
    private final PromotionRepository promotionRepository;

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final ProductRepository productRepository;

    public PromotionService(
            PromotionRepository promotionRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository) {
        this.promotionRepository = promotionRepository;
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

    public CartResponse handleUsePromo(String username, RequestBodyCode req) {
        if (req.productSkus().isPresent()) {
            return handleUsePromoSpecific(username, req);
        } else {
            return handleUsePromoGeneral(username, req);
        }
    }

    public CartResponse handleUsePromoGeneral(String username, RequestBodyCode req) {
        Cart cart =
                this.cartRepository
                        .findByShopper_name(username)
                        .orElseThrow(() -> new NotFoundException("not ufnd"));
        return cart.toResponse();
    }

    public CartResponse handleUsePromoSpecific(String username, RequestBodyCode req) {
        Promotion promotion = getPromotion(req.code());

        CartItem cartItem =
                findCartItem(username, req)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                String.format(
                                                        "%s not have this product in cart",
                                                        username)));
        if (checkCodeAvailable(req)) {
            if (validateTimeAvailable(promotion)) {
                updatePriceByDiscountType(promotion, cartItem);
                return cartItem.getCart().toResponse();
            }
            throw new PromoCodeExpiredException("promotion code is expire");
        }
        throw new PromoCodeNotApplicableException(
                "Can't use this promoCode for product " + req.productSkus().get());
    }

    private Promotion getPromotion(String code) {
        return this.promotionRepository
                .findByCode(code)
                .orElseThrow(
                        () ->
                                new NotFoundException(
                                        String.format("not found promoCode: %s", code)));
    }

    public boolean checkCodeAvailable(RequestBodyCode req) {
        String product_skus =
                this.promotionRepository
                        .findProductSkuByCode(req.code())
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                String.format(
                                                        "not found promotionCode: %s",
                                                        req.code())));

        return product_skus.contains(req.productSkus().get());
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

    public Optional<CartItem> findCartItem(String username, RequestBodyCode req) {
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
                        .findBySku(req.productSkus().get())
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                String.format(
                                                        "not found productSku: %s",
                                                        req.productSkus().get())));

        return this.cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());
    }
}
