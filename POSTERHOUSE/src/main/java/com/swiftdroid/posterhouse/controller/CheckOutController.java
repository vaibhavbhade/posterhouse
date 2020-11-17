package com.swiftdroid.posterhouse.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.swiftdroid.posterhouse.config.CustomOAuth2User;
import com.swiftdroid.posterhouse.model.BillingAddress;
import com.swiftdroid.posterhouse.model.CartItem;
import com.swiftdroid.posterhouse.model.ShippingAddress;
import com.swiftdroid.posterhouse.model.ShoppingCart;
import com.swiftdroid.posterhouse.model.User;
import com.swiftdroid.posterhouse.model.UserShipping;
import com.swiftdroid.posterhouse.service.CartItemService;
import com.swiftdroid.posterhouse.service.ShippingAddressService;
import com.swiftdroid.posterhouse.service.UserService;
import com.swiftdroid.posterhouse.utility.INDConstants;

@Controller
public class CheckOutController {
	
	
	private ShippingAddress shippingAddress = new ShippingAddress();
	private BillingAddress billingAddress = new BillingAddress();
	
	@Autowired
	private ShippingAddressService shippingAddressService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CartItemService cartItemService;
	
	
	@SuppressWarnings("finally")
	@RequestMapping("/checkout")
	public String checkoutPage(@RequestParam("id") Long cartId,
			@RequestParam(value="missingRequiredField", required=false) boolean missingRequiredField,
			Model model, Principal principal ,Authentication authentication) {
		
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
			if(cartId != user.getShoppingCart().getId()) {
				return "badRequestPage";
			}
			
			List<CartItem> cartItemList = cartItemService.findByShoppingCart(user.getShoppingCart());
			
			if(cartItemList.size() == 0) {
				model.addAttribute("emptyCart", true);
				return "forward:/cart";
			}
			
			for (CartItem cartItem : cartItemList) {
				if(cartItem.getProduct().getMaximumQuantity() < cartItem.getQty()) {
					model.addAttribute("notEnoughStock", true);
					return "forward:/cart";
				}
			}
			
			List<UserShipping> userShippingList = user.getUserShippingList();
			//List<UserPayment> userPaymentList = user.getUserPaymentList();
			
			model.addAttribute("userShippingList", userShippingList);
			//model.addAttribute("userPaymentList", userPaymentList);
			
		/*	if (userPaymentList.size() == 0) {
				model.addAttribute("emptyPaymentList", true);
			} else {
				model.addAttribute("emptyPaymentList", false);
			}*/
			
			if (userShippingList.size() == 0) {
				model.addAttribute("emptyShippingList", true);
			} else {
				model.addAttribute("emptyShippingList", false);

				model.addAttribute("notemptyShippingList", true);
			}
			
			ShoppingCart shoppingCart = user.getShoppingCart();
			
			for(UserShipping userShipping : userShippingList) {
				if(userShipping.isUserShippingDefault()) {
					shippingAddressService.setByUserShipping(userShipping, shippingAddress);
				}
			}
			
			/*for (UserPayment userPayment : userPaymentList) {
				if(userPayment.isDefaultPayment()) {
					paymentService.setByUserPayment(userPayment, payment);
					billingAddressService.setByUserBilling(userPayment.getUserBilling(), billingAddress);
				}
			}*/
			
			model.addAttribute("shippingAddress", shippingAddress);
			//model.addAttribute("payment", payment);
			//model.addAttribute("billingAddress", billingAddress);
			model.addAttribute("cartItemList", cartItemList);
			model.addAttribute("shoppingCart", user.getShoppingCart());
			
			List<String> stateList = INDConstants.listOfIndianStateCodes;
			
			Collections.sort(stateList);
			
			model.addAttribute("stateList", stateList);
			
			model.addAttribute("classActiveShipping", true);
			
			if(missingRequiredField) {
				model.addAttribute("missingRequiredField", true);
			}
			
			return "checkout";
		
	   }
	}
}
