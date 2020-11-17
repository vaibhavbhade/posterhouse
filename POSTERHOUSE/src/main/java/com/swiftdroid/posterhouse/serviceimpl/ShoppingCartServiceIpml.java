package com.swiftdroid.posterhouse.serviceimpl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swiftdroid.posterhouse.model.CartItem;
import com.swiftdroid.posterhouse.model.ShoppingCart;
import com.swiftdroid.posterhouse.repo.ShoppingCartRepository;
import com.swiftdroid.posterhouse.service.CartItemService;
import com.swiftdroid.posterhouse.service.ShoppingCartService;

@Service
public class ShoppingCartServiceIpml implements ShoppingCartService {

	@Autowired
	private ShoppingCartRepository shoppingCartRepository;
	
	@Autowired
	private CartItemService cartItemService;
	
	@Override
	public ShoppingCart updateShoppingCart(ShoppingCart shoppingCart) {
		// TODO Auto-generated method stub
		BigDecimal cartTotal=new BigDecimal(0);
		
		List<CartItem> cartItemList=cartItemService.findByShoppingCart(shoppingCart);		
		System.out.println(cartItemList.size());
		
		for (CartItem cartItem : cartItemList) {
			if(cartItem.getProduct().getMaximumQuantity() >0) {
				cartItemService.updateCartItem(cartItem);
				cartTotal=cartTotal.add(cartItem.getSubtotal());
				
			}
		}
		System.out.println("fffffffffffffffffffffffffffffdffffffffffffffffffffffff"+cartTotal);
		shoppingCart.setGrandTotal(cartTotal);
		
		
		shoppingCartRepository.save(shoppingCart);
		
		return shoppingCart;
	}
	
	

}
