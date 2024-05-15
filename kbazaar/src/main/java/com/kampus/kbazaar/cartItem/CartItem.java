package com.kampus.kbazaar.cartItem;

import com.kampus.kbazaar.cart.Cart;
import com.kampus.kbazaar.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "cart_item")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "cart_session", referencedColumnName = "id")
    private Cart cart;

    @NotNull @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull @Column(name = "sub_total", nullable = false)
    private BigDecimal subTotal;

    @NotNull @Column(name = "product_sku", nullable = false)
    private String productSku;

    @NotNull @Column(name = "discount", nullable = false)
    private BigDecimal discount;

    @NotNull @Column(name = "promotion_code", nullable = false)
    private String promotionCode;
}
