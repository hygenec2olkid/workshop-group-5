package com.kampus.kbazaar.shopper;

import com.kampus.kbazaar.cart.Cart;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "shopper")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Shopper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @OneToOne(mappedBy = "shopper")
    private Cart cart;

    public ShopperResponse toResponse() {
        return new ShopperResponse(this.id, this.username, this.email);
    }
}
