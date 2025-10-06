package com.acme.repository;

import com.acme.model.Product;
import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(String productId);
}
