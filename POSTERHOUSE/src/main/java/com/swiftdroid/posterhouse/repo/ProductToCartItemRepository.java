package com.swiftdroid.posterhouse.repo;

import org.springframework.data.repository.CrudRepository;

import com.swiftdroid.posterhouse.model.CartItem;
import com.swiftdroid.posterhouse.model.ProductToCartItem;

public interface ProductToCartItemRepository extends CrudRepository<ProductToCartItem, Long>{

	public void deleteByCartItem(CartItem cartItem);
		
	

}
