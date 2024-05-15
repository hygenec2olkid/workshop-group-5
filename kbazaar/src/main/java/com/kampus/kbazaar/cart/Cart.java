package com.kampus.kbazaar.cart;

import com.kampus.kbazaar.cartItem.CartItem;
import com.kampus.kbazaar.cartItem.CartItemResponse;
import com.kampus.kbazaar.promotion.Promotion;
import com.kampus.kbazaar.shopper.Shopper;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.*;

@Entity
@Table(name = "cart")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToMany(mappedBy = "cart")
    private List<CartItem> cartItemList;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private Shopper shopper;

    @ManyToOne
    @JoinColumn(name = "discount_id", referencedColumnName = "promotion_id")
    private Promotion promotion;

    @NotNull @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @NotNull @Column(name = "discount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discount;

    private List<CartItemResponse> getCartItemList() {
        return cartItemList.stream()
                .map(
                        item ->
                                new CartItemResponse(
                                        item.getProduct().getName(),
                                        item.getProductSku(),
                                        item.getQuantity(),
                                        item.getSubTotal(),
                                        item.getDiscount(),
                                        item.getPromotionCode()))
                .collect(Collectors.toList());
    }

    public CartResponse toResponse() {
        return new CartResponse(
                this.shopper.getUsername(), getCartItemList(), this.total, this.discount);
    }
}
