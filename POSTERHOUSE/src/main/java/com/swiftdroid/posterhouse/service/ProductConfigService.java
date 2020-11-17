package com.swiftdroid.posterhouse.service;

import com.swiftdroid.posterhouse.model.Product;
import com.swiftdroid.posterhouse.model.ProductConfig;

public interface ProductConfigService {
	public ProductConfig findProductConfigByProductId(Product product);

	public ProductConfig findProducConfigtByID(long id); 

}
