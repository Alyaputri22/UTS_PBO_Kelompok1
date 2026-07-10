package com.cplus.ecommerce.controller;

import com.cplus.ecommerce.model.CartItem;
import com.cplus.ecommerce.model.Order;
import com.cplus.ecommerce.model.User;
import com.cplus.ecommerce.repository.UserRepository;
import com.cplus.ecommerce.service.CartService;
import com.cplus.ecommerce.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, CartService cartService, UserRepository userRepository) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null) return null;
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElse(null);
    }

    @GetMapping("/checkout")
    public String showCheckoutForm(Model model, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        if (user == null) return "redirect:/login";

        List<CartItem> cartItems = cartService.getCartItems(user);
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        double total = cartService.calculateTotal(cartItems);
        
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        
        return "checkout";
    }

    @PostMapping("/checkout/process")
    public String processCheckout(
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("paymentMethod") String paymentMethod,
            Authentication authentication) {
        
        User user = getAuthenticatedUser(authentication);
        if (user == null) return "redirect:/login";

        try {
            Order order = orderService.createOrder(user, fullName, phone, address, paymentMethod);
            return "redirect:/invoice/" + order.getId();
        } catch (RuntimeException e) {
            return "redirect:/cart";
        }
    }

    @GetMapping("/invoice/{id}")
    public String showInvoice(@PathVariable("id") Long id, Model model, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        if (user == null) return "redirect:/login";

        Order order = orderService.getOrderById(id);
        
        // Pastikan hanya pemilik order yang bisa melihat
        if (order == null || !order.getUser().getId().equals(user.getId())) {
            return "redirect:/";
        }

        model.addAttribute("order", order);
        return "invoice";
    }
}
