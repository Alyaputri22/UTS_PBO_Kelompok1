package com.cplus.ecommerce.controller;

import com.cplus.ecommerce.model.Product;
import com.cplus.ecommerce.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;

    public AdminController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/dashboard"; 
    }

    @GetMapping("/product/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin/product-form";
    }

    @PostMapping("/product/save")
    public String saveProduct(@ModelAttribute("product") Product product, 
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              @RequestParam(value = "backImageFile", required = false) MultipartFile backImageFile) {
        
        Path uploadPath = Paths.get("src/main/resources/static/images/");
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Proses Foto Depan
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_front_" + imageFile.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                product.setImageUrl("/images/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (product.getId() != null) {
                Product existingProduct = productService.getProductById(product.getId());
                if (existingProduct != null) {
                    product.setImageUrl(existingProduct.getImageUrl());
                }
            } else {
                product.setImageUrl("/img/tshirt_black.png");
            }
        }

        // Proses Foto Belakang
        if (backImageFile != null && !backImageFile.isEmpty()) {
            try {
                String backFileName = System.currentTimeMillis() + "_back_" + backImageFile.getOriginalFilename();
                Path backFilePath = uploadPath.resolve(backFileName);
                Files.copy(backImageFile.getInputStream(), backFilePath, StandardCopyOption.REPLACE_EXISTING);
                product.setBackImageUrl("/images/" + backFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (product.getId() != null) {
                Product existingProduct = productService.getProductById(product.getId());
                if (existingProduct != null) {
                    product.setBackImageUrl(existingProduct.getBackImageUrl());
                }
            }
        }
        
        productService.saveProduct(product);
        return "redirect:/admin";
    }

    @GetMapping("/product/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product != null) {
            model.addAttribute("product", product);
            return "admin/product-form";
        }
        return "redirect:/admin";
    }

    @GetMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin";
    }
}
