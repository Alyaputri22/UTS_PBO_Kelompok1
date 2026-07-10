package com.cplus.ecommerce.controller;

import com.cplus.ecommerce.model.CartItem;
import com.cplus.ecommerce.model.User;
import com.cplus.ecommerce.repository.UserRepository;
import com.cplus.ecommerce.service.CartService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final CartService cartService;
    private final UserRepository userRepository;

    public GlobalControllerAdvice(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    // Fungsi ini akan otomatis menyuntikkan variabel "cartItemCount" ke SEMUA halaman HTML
    @ModelAttribute("cartItemCount")
    public int populateCartItemCount(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userRepository.findByUsername(authentication.getName()).orElse(null);
            // Hanya hitung untuk role USER biasa (admin tidak punya fitur cart)
            if (user != null && user.getRole() == com.cplus.ecommerce.model.Role.ROLE_USER) {
                List<CartItem> items = cartService.getCartItems(user);
                int count = 0;
                for (CartItem item : items) {
                    count += item.getQuantity();
                }
                return count;
            }
        }
        return 0;
    }
}
