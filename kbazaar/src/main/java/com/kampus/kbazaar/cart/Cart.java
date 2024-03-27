package com.kampus.kbazaar.cart;

import com.kampus.kbazaar.promotion.Promotion;
import com.kampus.kbazaar.shopper.Shopper;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "cart")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToMany(mappedBy = "cart")
    private List<CartItem> cartItemList;

    //    @NotNull @Column(name = "user_id", unique = true, nullable = false)
    //    private Long userId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private Shopper shopper;

    //    @Column(name = "discount_id")
    //    private Long discountId;

    //    @OneToOne(fetch = FetchType.LAZY)
    //    @JoinColumn(name = "discount_id",referencedColumnName = "promotion_id")
    //    private Promotion promotion;

    @ManyToOne
    @JoinColumn(name = "discount_id", referencedColumnName = "promotion_id")
    private Promotion promotion;

    @NotNull @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @NotNull @Column(name = "discount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discount;

    //    @OneToMany(mappedBy = "cart")
    //    private Set<CartItem> cartItemSet;

}
