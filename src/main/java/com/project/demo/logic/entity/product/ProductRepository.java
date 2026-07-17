package com.project.demo.logic.entity.product;

import com.project.demo.logic.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;



public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> getProductByCategoryId(Long id, Pageable pageable);
    Page<Product> getByNameContainingIgnoreCase(String name, Pageable pageable);
}
