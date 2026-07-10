package com.cplus.ecommerce.controller;

import com.cplus.ecommerce.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CatalogController {

    private final ProductService productService;

    public CatalogController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String homepage(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "index";
    }
}
