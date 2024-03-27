package com.kampus.kbazaar.cart;

import com.kampus.kbazaar.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "cart_item")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    //    @NotNull @Column(name = "cart_session", nullable = false)
    //    private Long cartSession;

    //    @NotNull @Column(name = "product_id", nullable = false)
    //    private Long productId;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "cart_session", referencedColumnName = "id")
    private Cart cart;

    @NotNull @Column(name = "quantity", nullable = false)
    private Integer quantity;

    //    @ManyToOne
    //    @JoinColumn(name = "id",nullable = false)
    //    private Cart cart;
}
