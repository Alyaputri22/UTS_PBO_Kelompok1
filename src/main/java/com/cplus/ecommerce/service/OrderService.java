package com.cplus.ecommerce.service;

import com.cplus.ecommerce.model.CartItem;
import com.cplus.ecommerce.model.Order;
import com.cplus.ecommerce.model.OrderItem;
import com.cplus.ecommerce.model.User;
import com.cplus.ecommerce.repository.OrderRepository;
import com.cplus.ecommerce.repository.ProductRepository;
import com.cplus.ecommerce.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, CartService cartService, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order createOrder(User user, String fullName, String phone, String address, String paymentMethod) {
        List<CartItem> cartItems = cartService.getCartItems(user);
        
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        double totalAmount = cartService.calculateTotal(cartItems);

        Order order = new Order();
        order.setUser(user);
        order.setFullName(fullName);
        order.setPhone(phone);
        order.setAddress(address);
        order.setPaymentMethod(paymentMethod);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(totalAmount);

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            String size = cartItem.getSize();
            int qty = cartItem.getQuantity();

            // Cek dan kurangi stok
            if ("S".equals(size)) {
                if (product.getStockS() < qty) throw new RuntimeException("Stok S habis untuk " + product.getName());
                product.setStockS(product.getStockS() - qty);
            } else if ("M".equals(size)) {
                if (product.getStockM() < qty) throw new RuntimeException("Stok M habis untuk " + product.getName());
                product.setStockM(product.getStockM() - qty);
            } else if ("L".equals(size)) {
                if (product.getStockL() < qty) throw new RuntimeException("Stok L habis untuk " + product.getName());
                product.setStockL(product.getStockL() - qty);
            } else if ("XL".equals(size)) {
                if (product.getStockXL() < qty) throw new RuntimeException("Stok XL habis untuk " + product.getName());
                product.setStockXL(product.getStockXL() - qty);
            }
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setSize(size);
            orderItem.setQuantity(qty);
            orderItem.setPrice(product.getPrice().doubleValue());
            
            order.getItems().add(orderItem);
        }

        // Save order and its items (because of CascadeType.ALL)
        Order savedOrder = orderRepository.save(order);

        // Clear the user's cart
        cartService.clearCart(user);

        return savedOrder;
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }
}
