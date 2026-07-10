package com.cplus.ecommerce.service;

import com.cplus.ecommerce.model.CartItem;
import com.cplus.ecommerce.model.Product;
import com.cplus.ecommerce.model.User;
import com.cplus.ecommerce.repository.CartItemRepository;
import com.cplus.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }

    public void addToCart(User user, Long productId, String size) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return;

        Optional<CartItem> existingItem = cartItemRepository.findByUserAndProductAndSize(user, product, size);

        int currentQtyInCart = existingItem.map(CartItem::getQuantity).orElse(0);
        int availableStock = 0;
        
        if ("S".equals(size)) availableStock = product.getStockS();
        else if ("M".equals(size)) availableStock = product.getStockM();
        else if ("L".equals(size)) availableStock = product.getStockL();
        else if ("XL".equals(size)) availableStock = product.getStockXL();

        // Jangan tambahkan jika pesanan melebihi ketersediaan stok
        if (currentQtyInCart + 1 > availableStock) {
            return;
        }

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + 1);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setUser(user);
            newItem.setProduct(product);
            newItem.setSize(size);
            newItem.setQuantity(1);
            cartItemRepository.save(newItem);
        }
    }

    public void removeCartItem(Long itemId) {
        cartItemRepository.deleteById(itemId);
    }

    @Transactional
    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }

    public double calculateTotal(List<CartItem> cartItems) {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getSubtotal();
        }
        return total;
    }
}
