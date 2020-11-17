package com.swiftdroid.posterhouse.repo;

import org.springframework.data.repository.CrudRepository;

import com.swiftdroid.posterhouse.model.Product;
import com.swiftdroid.posterhouse.model.ProductConfig;

public interface ProductConfigRepository extends CrudRepository<ProductConfig, Long> {

	ProductConfig findByproduct(Product product);

}
