package com.kampus.kbazaar.product;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    Optional<Product> findById(Long productSku);

    @Query("SELECT p FROM Product p WHERE p.id = :productSku")
    Product findByProductId(@Param("productSku") Long productSku);
}
