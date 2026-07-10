package com.cplus.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String imageUrl;

    @Column
    private String backImageUrl;

    // Stok per ukuran
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int stockS;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int stockM;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int stockL;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int stockXL;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    private List<OrderItem> orderItems;
}
