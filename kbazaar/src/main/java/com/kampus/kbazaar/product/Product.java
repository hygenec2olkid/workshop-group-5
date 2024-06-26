package com.kampus.kbazaar.product;

import com.kampus.kbazaar.cartItem.CartItem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.*;

@Entity
@Table(
        name = "product",
        uniqueConstraints = {@UniqueConstraint(columnNames = "sku")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @NotBlank
    @Size(max = 255)
    @Column(name = "sku", nullable = false, unique = true, length = 255)
    private String sku;

    @NotNull @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull @PositiveOrZero
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItemList;

    public ProductResponse toResponse() {
        return new ProductResponse(this.id, this.name, this.sku, this.price, this.quantity);
    }
}
