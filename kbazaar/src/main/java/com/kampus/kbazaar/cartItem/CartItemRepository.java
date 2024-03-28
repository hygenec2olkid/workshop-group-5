package com.kampus.kbazaar.cartItem;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT u FROM CartItem u WHERE u.cart.id = :cart_session AND u.product.id = :productId")
    Optional<CartItem> findByCartIdAndProductId(
            @Param("cart_session") Long cartSession, @Param("productId") Long productId);
}
