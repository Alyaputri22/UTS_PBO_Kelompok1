package com.cplus.ecommerce.repository;

import com.cplus.ecommerce.model.CartItem;
import com.cplus.ecommerce.model.Product;
import com.cplus.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    Optional<CartItem> findByUserAndProductAndSize(User user, Product product, String size);
    void deleteByUser(User user);
}
