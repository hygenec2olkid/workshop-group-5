package com.kampus.kbazaar.promotion;

import com.kampus.kbazaar.cart.Cart;
import com.kampus.kbazaar.cart.CartRepository;
import com.kampus.kbazaar.exceptions.NotFoundException;
import com.kampus.kbazaar.shopper.Shopper;
import com.kampus.kbazaar.shopper.ShopperRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PromotionService {
    private PromotionRepository promotionRepository;

    private ShopperRepository shopperRepository;

    private CartRepository cartRepository;

    public PromotionService(
            PromotionRepository promotionRepository,
            ShopperRepository shopperRepository,
            CartRepository cartRepository) {
        this.promotionRepository = promotionRepository;
        this.shopperRepository = shopperRepository;
        this.cartRepository = cartRepository;
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

    public String usePromotionCode(String username, String code) {
        Shopper shopper =
                this.shopperRepository
                        .findByUsername(username)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                "Shopper "
                                                        + username
                                                        + " not have in ShopperRepo"));

        Cart cart =
                this.cartRepository
                        .findByShopper_Id(shopper.getId())
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                "Can't use promotion code because "
                                                        + username
                                                        + " not have item in cart"));
        return cart.getShopper().getUsername();
    }
}
