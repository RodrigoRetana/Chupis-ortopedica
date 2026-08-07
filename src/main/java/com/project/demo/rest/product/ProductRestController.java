package com.project.demo.rest.product;

import com.project.demo.logic.entity.category.Category;
import com.project.demo.logic.entity.category.CategoryRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.product.Product;
import com.project.demo.logic.entity.product.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/products")

public class ProductRestController {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String name,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Product> productsPage;

        if (categoryId != null) {
            productsPage = productRepository.findByCategoryId(categoryId, pageable);
        } else if (name != null && !name.isBlank()) {
            productsPage = productRepository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            productsPage = productRepository.findAll(pageable);
        }

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(productsPage.getTotalPages());
        meta.setTotalElements(productsPage.getTotalElements());
        meta.setPageNumber(productsPage.getNumber() + 1);
        meta.setPageSize(productsPage.getSize());

        return new GlobalResponseHandler().handleResponse("Products retrieved successfully",
                productsPage.getContent(), HttpStatus.OK, meta);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getById(@PathVariable Long productId, HttpServletRequest request) {
        Optional<Product> foundProduct = productRepository.findById(productId);
        if (foundProduct.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Product retrieved successfully",
                    foundProduct.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Product id " + productId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }



    @PostMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> create(@RequestBody Product product, HttpServletRequest request) {
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Optional<Category> category = categoryRepository.findById(product.getCategory().getId());
            category.ifPresent(product::setCategory);
        }
        Product savedProduct = productRepository.save(product);
        return new GlobalResponseHandler().handleResponse("Product created successfully",
                savedProduct, HttpStatus.CREATED, request);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long productId, @RequestBody Product product, HttpServletRequest request) {
        Optional<Product> foundProduct = productRepository.findById(productId);
        if (foundProduct.isPresent()) {
            product.setId(foundProduct.get().getId());
            if (product.getCategory() != null && product.getCategory().getId() != null) {
                categoryRepository.findById(product.getCategory().getId()).ifPresent(product::setCategory);
            }
            productRepository.save(product);
            return new GlobalResponseHandler().handleResponse("Product updated successfully",
                    product, HttpStatus.OK, request);
        }
        return new GlobalResponseHandler().handleResponse("Product id " + productId + " not found",
                HttpStatus.NOT_FOUND, request);
    }
    @PatchMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> patch(@PathVariable Long productId, @RequestBody Product product, HttpServletRequest request) {
        Optional<Product> foundProduct = productRepository.findById(productId);
        if (foundProduct.isPresent()) {
            Product existing = foundProduct.get();
            if (product.getName() != null) existing.setName(product.getName());
            if (product.getDescription() != null) existing.setDescription(product.getDescription());
            if (product.getPrice() != null) existing.setPrice(product.getPrice());
            if (product.getCategory() != null && product.getCategory().getId() != null) {
                categoryRepository.findById(product.getCategory().getId()).ifPresent(existing::setCategory);
            }
            productRepository.save(existing);
            return new GlobalResponseHandler().handleResponse("Product updated successfully",
                    existing, HttpStatus.OK, request);
        }
        return new GlobalResponseHandler().handleResponse("Product id " + productId + " not found",
                HttpStatus.NOT_FOUND, request);
    }



    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId, HttpServletRequest request) {
        Optional<Product> foundProduct = productRepository.findById(productId);
        if (foundProduct.isPresent()) {
            productRepository.deleteById(productId);
            return new GlobalResponseHandler().handleResponse("Product deleted successfully",
                    foundProduct.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Product id " + productId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }
}
