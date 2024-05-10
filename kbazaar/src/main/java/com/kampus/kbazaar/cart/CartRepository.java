package com.kampus.kbazaar.cart;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByShopper_Id(Long shopperId);

    @Query("SELECT c FROM Cart c WHERE c.shopper.username = :username")
    Optional<Cart> findByShopper_name(String username);
}
