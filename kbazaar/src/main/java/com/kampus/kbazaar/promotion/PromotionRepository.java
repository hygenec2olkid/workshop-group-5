package com.kampus.kbazaar.promotion;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByCode(String code);

    @Query("SELECT p.productSkus FROM promotion p WHERE p.code = :code")
    Optional<String> findProductSkuByCode(String code);
}
