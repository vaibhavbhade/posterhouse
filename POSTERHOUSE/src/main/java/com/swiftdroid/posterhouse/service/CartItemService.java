package com.swiftdroid.posterhouse.service;

import java.util.List;

import com.swiftdroid.posterhouse.model.CartItem;
import com.swiftdroid.posterhouse.model.Product;
import com.swiftdroid.posterhouse.model.ProductConfig;
import com.swiftdroid.posterhouse.model.ShoppingCart;
import com.swiftdroid.posterhouse.model.User;

public interface CartItemService {

	List<CartItem> findByShoppingCart(ShoppingCart shoppingCart);

	CartItem updateCartItem(CartItem cartItem);

	CartItem addProductToCartItem(ProductConfig productConfig,Product product, User user, int parseInt);

	CartItem findById(Long cartItemId);
	
	public void removeCartItem(CartItem cartItem);

	void saveCart(CartItem cartItem);

	
}
