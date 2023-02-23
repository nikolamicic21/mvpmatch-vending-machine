package io.nikolamicic21.mvpmatchvendingmachine.repository;

import io.nikolamicic21.mvpmatchvendingmachine.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByProductId(UUID productId);

    Optional<Product> findByProductIdAndSeller_Username(UUID productId, String username);

}
