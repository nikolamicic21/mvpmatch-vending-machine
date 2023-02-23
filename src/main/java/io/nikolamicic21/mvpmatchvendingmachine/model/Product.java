package io.nikolamicic21.mvpmatchvendingmachine.model;

import io.nikolamicic21.mvpmatchvendingmachine.exception.ProductCreationException;
import io.nikolamicic21.mvpmatchvendingmachine.exception.ProductUpdateException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "mvpmatch_product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mvpmatch_gen")
    @SequenceGenerator(name = "mvpmatch_gen", sequenceName = "mvpmatch_seq", allocationSize = 10)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "amount_available", nullable = false)
    @Setter
    private Integer amountAvailable;

    @Column(name = "cost", nullable = false)
    private Integer cost;

    @Column(name = "product_name", nullable = false, length = 30, unique = true)
    @Setter
    private String productName;

    @Column(name = "product_id", nullable = false, unique = true)
    private UUID productId;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    public Product(Integer amountAvailable, Integer cost, String productName, User seller) {
        if (cost % 5 != 0) {
            throw new ProductCreationException("Product's cost should be multiplier of 5");
        }
        this.cost = cost;
        this.productId = UUID.randomUUID();
        this.amountAvailable = amountAvailable;
        this.productName = productName;
        this.seller = seller;
    }

    public void setCost(Integer cost) {
        if (cost % 5 != 0) {
            throw new ProductUpdateException("Product's cost should be in multiples of 5");
        }

        this.cost = cost;
    }
}
