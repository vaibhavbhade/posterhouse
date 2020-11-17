package com.swiftdroid.posterhouse.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swiftdroid.posterhouse.model.Product;
import com.swiftdroid.posterhouse.model.ProductConfig;
import com.swiftdroid.posterhouse.repo.ProductConfigRepository;
import com.swiftdroid.posterhouse.service.ProductConfigService;

@Service
public class ProductConfigServiceImpl  implements ProductConfigService{

	@Autowired
	private ProductConfigRepository productConfigRepository;
	
	
	@Override
	public ProductConfig findProductConfigByProductId(Product product) {
		// TODO Auto-generated method stub
		return productConfigRepository.findByproduct(product);
	}


	@Override
	public ProductConfig findProducConfigtByID(long id) {
		// TODO Auto-generated method stub
		return productConfigRepository.findById(id).orElse(null);
	}

}
