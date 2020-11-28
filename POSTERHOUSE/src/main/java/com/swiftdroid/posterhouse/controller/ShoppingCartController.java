package com.swiftdroid.posterhouse.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.swiftdroid.posterhouse.config.CustomOAuth2User;
import com.swiftdroid.posterhouse.model.CartItem;
import com.swiftdroid.posterhouse.model.Product;
import com.swiftdroid.posterhouse.model.ProductConfig;
import com.swiftdroid.posterhouse.model.ShoppingCart;
import com.swiftdroid.posterhouse.model.User;
import com.swiftdroid.posterhouse.service.CartItemService;
import com.swiftdroid.posterhouse.service.ProductConfigService;
import com.swiftdroid.posterhouse.service.ProductService;
import com.swiftdroid.posterhouse.service.ShoppingCartService;
import com.swiftdroid.posterhouse.service.UserService;


@Controller
public class ShoppingCartController {
	
@Autowired
private UserService userService;

@Autowired
private CartItemService cartItemService;

/*
@Autowired
private BookService bookService;
*/

@Autowired
private ShoppingCartService shoppingCartService;

@Autowired
private ProductConfigService productConfigService;

@Autowired
private ProductService productService;

@RequestMapping("/cart")
public String userShoppingCart(Model model,Principal principal,Authentication authentication )
{
	
	User user=null;
	try {
	CustomOAuth2User oAuth2User=(CustomOAuth2User) authentication.getPrincipal();
    String email=oAuth2User.getEmail();
	System.out.println("eeeeeeeeeeeeeeee"+email);
	user = userService.findByEmail(email);
	}catch (Exception e) {
		user = userService.findByUsername(principal.getName());
	}
	finally {
	
	ShoppingCart shoppingCart = user.getShoppingCart();
	
	List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);
	
	
	shoppingCartService.updateShoppingCart(shoppingCart);
	
	model.addAttribute("cartItemList", cartItemList);
	model.addAttribute("shoppingCart", shoppingCart);
	
	return "shoppingCart";
	
}
}	

@SuppressWarnings("finally")
@RequestMapping("/addItem")
public String addItem(
        @ModelAttribute("qty") String qty,
		@ModelAttribute("productConfig") ProductConfig productConfig,

		Model model, Principal principal,Authentication authentication
		) {
	User user=null;
	try {
	CustomOAuth2User oAuth2User=(CustomOAuth2User) authentication.getPrincipal();
    String email=oAuth2User.getEmail();
	System.out.println("eeeeeeeeeeeeeeee"+email);
	user = userService.findByEmail(email);
	}catch (Exception e) {
		user = userService.findByUsername(principal.getName());
	}
	finally {

		System.out.println("ProductCofig ID :: "+productConfig.getId());

	System.out.println("*************************************************************11*********************************************");	
		productConfig = productConfigService.findProducConfigtByID(productConfig.getId());
		
		//System.out.println(productConfig.getProduct().getId());

		Product product=productService.findProductById(productConfig.getProduct().getId());//product
		
		//System.out.println(product.getId());
		
		
		//System.out.println("qty :: "+qty);
		System.out.println("*************************************************************22*********************************************");	

	
	
	if (Integer.parseInt(qty) > product.getMaximumQuantity()) {
		
		model.addAttribute("notEnoughStock", true);
		
		return "forward:/productDetail?id="+product.getId();
	}
	
	System.out.println("*************************************************************33*********************************************");	
    System.out.println("productConfig::::"+productConfig);
    
	CartItem cartItem = cartItemService.addProductToCartItem(productConfig,product, user, Integer.parseInt(qty));
	
	model.addAttribute("addBookSuccess", true);
	
	return "forward:/productDetail?id="+product.getId();
	
}
}

@RequestMapping("/updateCartItem")
public String updateShoppingCart(
		@ModelAttribute("id") Long cartItemId,
		@ModelAttribute("qty") int qty,Model model
		) {
if(qty==0) {

	model.addAttribute("qtyMsg", true);
	return "forward:/cart";
}
	System.out.println("cartItemId::"+cartItemId);
	CartItem cartItem = cartItemService.findById(cartItemId);
	cartItem.setQty(qty);
	cartItemService.updateCartItem(cartItem);
	
	return "forward:/cart";
}


@RequestMapping("/removeItem")
public String removeItem(@RequestParam("id") Long id) {
	
	cartItemService.removeCartItem(cartItemService.findById(id));
	
	return "forward:/cart";
}



}
