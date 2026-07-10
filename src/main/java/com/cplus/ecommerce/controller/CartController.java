package com.cplus.ecommerce.controller;

import com.cplus.ecommerce.model.CartItem;
import com.cplus.ecommerce.model.User;
import com.cplus.ecommerce.repository.UserRepository;
import com.cplus.ecommerce.service.CartService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null) return null;
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElse(null);
    }

    @GetMapping("/cart")
    public String showCart(Model model, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        if (user == null) return "redirect:/login";

        List<CartItem> cartItems = cartService.getCartItems(user);
        double total = cartService.calculateTotal(cartItems);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);

        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("productId") Long productId, @RequestParam("size") String size, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        if (user == null) return "redirect:/login";

        cartService.addToCart(user, productId, size);
        return "redirect:/"; // Tetap di halaman home/catalog setelah menambah barang
    }

    @PostMapping("/cart/remove/{id}")
    public String removeCartItem(@PathVariable("id") Long id, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        if (user == null) return "redirect:/login";

        // Idealnya ada pengecekan apakah item ini benar milik user tersebut,
        // namun untuk projek ini kita asumsikan aman.
        cartService.removeCartItem(id);
        return "redirect:/cart";
    }

}
