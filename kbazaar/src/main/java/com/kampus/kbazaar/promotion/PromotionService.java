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
import com.kampus.kbazaar.util.BigDecimalPercentages;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PromotionService {
    private final PromotionRepository promotionRepository;

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final ProductRepository productRepository;

    private final BigDecimalPercentages bigDecimalPercentages;

    public PromotionService(
            PromotionRepository promotionRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            BigDecimalPercentages bigDecimalPercentages) {
        this.promotionRepository = promotionRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.bigDecimalPercentages = bigDecimalPercentages;
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

    public CartResponse handleUsePromoSpecific(String username, RequestBodyCode req) {
        Promotion promotion = getPromotion(req.code());

        CartItem cartItem = findCartItem(username, req);

        if (checkCodeAvailable(req)) {
            if (validateTimeAvailable(promotion)) {
                handleAppliedToCartItemSpecific(promotion, cartItem);
            }
        }
        return cartItem.getCart().toResponse();
    }

    public CartResponse handleUsePromoGeneral(String username, RequestBodyCode req) {
        Promotion promotion = getPromotion(req.code());

        Cart cart = findCart(username);

        if (validateTimeAvailable(promotion)) {
            if ("ENTIRE_CART".equals(promotion.getApplicableTo())) {
                handleAppliedToEntireCart(promotion, cart);
            }
        }
        return cart.toResponse();
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

        boolean isAvailable = product_skus.contains(req.productSkus().get());

        if (isAvailable) return true;

        throw new PromoCodeNotApplicableException(
                "Can't use this promoCode for product " + req.productSkus().get());
    }

    public boolean validateTimeAvailable(Promotion promo) {
        LocalDate localDate = LocalDate.now();

        LocalDate startDate = LocalDate.from(promo.getStartDate());
        LocalDate endDate = LocalDate.from(promo.getEndDate());

        boolean isAvailable = localDate.isAfter(startDate) && localDate.isBefore(endDate);
        if (isAvailable) return true;

        throw new PromoCodeExpiredException("promotion code is expire");
    }

    private boolean isGetFreeProduct(Promotion promo) {
        return promo.getMinQuantity() != null && promo.getFreeQuantity() != null;
    }

    public void handleAppliedToCartItemSpecific(Promotion promo, CartItem cartItem) {
        if (isGetFreeProduct(promo)) {
            updateGetFreeProduct(promo, cartItem);
        } else {
            BigDecimal discount = calDiscount(promo, cartItem.getSubTotal());
            updateCartItem(discount, cartItem, promo);
            updateCartTotal(cartItem.getCart(), cartItem.getCart().getDiscount());
        }
    }

    public void handleAppliedToEntireCart(Promotion promo, Cart cart) {
        BigDecimal discount = BigDecimal.ZERO;
        List<CartItem> cartItemList = this.cartItemRepository.findByCartId(cart.getId());
        if (isGetFreeProduct(promo)) {
            for (CartItem product : cartItemList) {
                updateGetFreeProduct(promo, product);
            }

        } else {
            discount = calDiscount(promo, cart.getTotal());
            for (CartItem product : cartItemList) {
                product.setPromotionCode("");
                product.setFreeProduct(0);
                this.cartItemRepository.save(product);
            }
        }
        updateCartTotal(cart, discount);
        cart.setPromotion(promo);
        cart.setPromotionCode(promo.getCode());
        this.cartRepository.save(cart);
    }

    public void updateGetFreeProduct(Promotion promo, CartItem cartItem) {
        Integer min = promo.getMinQuantity();
        Integer free = promo.getFreeQuantity();
        if ("ENTIRE_CART".equals(promo.getApplicableTo())) {
            cartItem.setDiscount(BigDecimal.ZERO);
            cartItem.setFreeProduct(free);
        }
        if ("SPECIFIC_PRODUCTS".equals(promo.getApplicableTo())) {
            if (cartItem.getQuantity() >= min) {
                cartItem.setFreeProduct(free);
            }
        }
        cartItem.setPromotionCode(promo.getCode());
        this.cartItemRepository.save(cartItem);
    }

    private BigDecimal calDiscount(Promotion promo, BigDecimal total) {
        BigDecimal discount = promo.getDiscountAmount();
        if ("FIXED_AMOUNT".equals(promo.getDiscountType())) {
            BigDecimal maxDiscount = promo.getMaxDiscountAmount();
            if (maxDiscount != null && discount.compareTo(maxDiscount) > 0) {
                discount = maxDiscount;
            }

        } else if ("PERCENTAGE".equals(promo.getDiscountType())) {
            discount = bigDecimalPercentages.percentOf(discount, total);
            BigDecimal maxDiscount = promo.getMaxDiscountAmount();
            if (maxDiscount != null && discount.compareTo(maxDiscount) > 0) {
                discount = maxDiscount;
            }
        }

        return discount;
    }

    public void updateCartTotal(Cart cart, BigDecimal discount) {
        List<CartItem> cartItemList = this.cartItemRepository.findByCartId(cart.getId());

        BigDecimal subDiscount =
                cartItemList.stream()
                        .map(CartItem::getDiscount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setDiscount(discount);
        cart.setTotalDiscount(cart.getDiscount().add(subDiscount));
        cart.setFinalTotal(cart.getTotal().subtract(cart.getTotalDiscount()));
        this.cartRepository.save(cart);
    }

    public void updateCartItem(BigDecimal discount, CartItem cartItem, Promotion promo) {
        cartItem.setFreeProduct(0);
        cartItem.setDiscount(discount);
        cartItem.setPromotionCode(promo.getCode());
        this.cartItemRepository.save(cartItem);
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
                        .findBySku(req.productSkus().get())
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                String.format(
                                                        "not found productSku: %s",
                                                        req.productSkus().get())));

        return this.cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseThrow(
                        () ->
                                new NotFoundException(
                                        String.format(
                                                "%s not have this product in cart", username)));
    }

    private Cart findCart(String username) {
        return this.cartRepository
                .findByShopper_name(username)
                .orElseThrow(() -> new NotFoundException("not found"));
    }
}
