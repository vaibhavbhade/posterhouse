package com.swiftdroid.posterhouse.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.swiftdroid.posterhouse.config.CustomOAuth2User;
import com.swiftdroid.posterhouse.model.BillingAddress;
import com.swiftdroid.posterhouse.model.CartItem;
import com.swiftdroid.posterhouse.model.Order;
import com.swiftdroid.posterhouse.model.ShippingAddress;
import com.swiftdroid.posterhouse.model.ShoppingCart;
import com.swiftdroid.posterhouse.model.User;
import com.swiftdroid.posterhouse.model.UserBilling;
import com.swiftdroid.posterhouse.model.UserShipping;
import com.swiftdroid.posterhouse.service.BillingAddressService;
import com.swiftdroid.posterhouse.service.CartItemService;
import com.swiftdroid.posterhouse.service.OrderService;
import com.swiftdroid.posterhouse.service.ShippingAddressService;
import com.swiftdroid.posterhouse.service.ShoppingCartService;
import com.swiftdroid.posterhouse.service.UserService;
import com.swiftdroid.posterhouse.service.UserShippingService;
import com.swiftdroid.posterhouse.utility.INDConstants;
import com.swiftdroid.posterhouse.utility.MailConstructor;

@Controller
public class CheckOutController {
	
	
	private ShippingAddress shippingAddress = new ShippingAddress();
	private BillingAddress billingAddress = new BillingAddress();
	
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private MailConstructor mailConstructor;
	
	
	@Autowired
	private ShippingAddressService shippingAddressService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private ShoppingCartService shoppingCartService;
	
	
	@Autowired
	private BillingAddressService billingAddressService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CartItemService cartItemService;
	
	@Autowired
	private UserShippingService userShippingService;
	
	@SuppressWarnings("finally")
	@RequestMapping("/checkout")
	public String checkoutPage(@RequestParam("id") Long cartId,
			@RequestParam(value="missingRequiredField", required=false) boolean missingRequiredField,
			Model model, Principal principal ,Authentication authentication) {
		try {
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
			
			System.out.println("**********  ******   "+cartId+ " == "+ user.getShoppingCart().getId());
			
			if(cartId.longValue() != user.getShoppingCart().getId().longValue()) {
				return "badRequestPage";
			}
			System.out.println("########################################");
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
			List<UserBilling> UserBillingtList = user.getUserBilingList();
			
			model.addAttribute("userShippingList", userShippingList);
			model.addAttribute("UserBillingtList", UserBillingtList);
			
			if (UserBillingtList.size() == 0) {
				model.addAttribute("UserBillingtList", true);
			} else {
				model.addAttribute("UserBillingtList", false);
			}
			
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
			
           for (UserBilling userBilling : UserBillingtList) {
        	   if(userBilling.isUserBillingDefault()) {
        		   billingAddressService.setByUserBilling(userBilling, billingAddress);
        	   }
				
			}
		/*	
		for (UserPayment userPayment : userPaymentList) {
				if(userPayment.isDefaultPayment()) {
					paymentService.setByUserPayment(userPayment, payment);
					billingAddressService.setByUserBilling(userPayment.getUserBilling(), billingAddress);
				}
			}
			*/
           
			model.addAttribute("shippingAddress", shippingAddress);
			//model.addAttribute("payment", payment);
	        model.addAttribute("billingAddress", billingAddress);
			model.addAttribute("cartItemList", cartItemList);
			model.addAttribute("shoppingCart", user.getShoppingCart());
			
			List<String> stateList = INDConstants.listOfIndianStateName;
			
			Collections.sort(stateList);
			
			model.addAttribute("stateList", stateList);
			
			model.addAttribute("classActiveShipping", true);
			
			if(missingRequiredField) {
				model.addAttribute("missingRequiredField", true);
			}
			
			return "checkout";
		}
	   }catch (Exception e) {
		// TODO: handle exception
		  return "badRequestPage";
	}
	}
	
	
	@SuppressWarnings("finally")
	@RequestMapping(value = "/checkout", method = RequestMethod.POST)
	public String checkoutPost(@ModelAttribute("shippingAddress") ShippingAddress shippingAddress,
			@ModelAttribute("billingAddress") BillingAddress billingAddress,
			@ModelAttribute("billingSameAsShipping") String billingSameAsShipping,
			@ModelAttribute("shippingMethod") String shippingMethod, Principal principal, Model model,Authentication authentication) {
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
		
		model.addAttribute("cartItemList", cartItemList);
		
		

		if (billingSameAsShipping.equals("true")) {
			billingAddress.setBillingAddressName(shippingAddress.getShippingAddressName());
			billingAddress.setBillingAddressStreet1(shippingAddress.getShippingAddressStreet1());
			billingAddress.setBillingAddressStreet2(shippingAddress.getShippingAddressStreet2());
			billingAddress.setBillingAddressCity(shippingAddress.getShippingAddressCity());
			billingAddress.setBillingAddressState(shippingAddress.getShippingAddressState());
			billingAddress.setBillingAddressCountry(shippingAddress.getShippingAddressCountry());
			billingAddress.setBillingAddressZipcode(shippingAddress.getShippingAddressZipcode());
		}

		if (shippingAddress.getShippingAddressStreet1().isEmpty() || shippingAddress.getShippingAddressCity().isEmpty()
				|| shippingAddress.getShippingAddressState().isEmpty()
				|| shippingAddress.getShippingAddressName().isEmpty()
				|| shippingAddress.getShippingAddressZipcode().isEmpty() 
				|| billingAddress.getBillingAddressStreet1().isEmpty()
				|| billingAddress.getBillingAddressCity().isEmpty() || billingAddress.getBillingAddressState().isEmpty()
				|| billingAddress.getBillingAddressName().isEmpty()
				|| billingAddress.getBillingAddressZipcode().isEmpty())
			return "redirect:/checkout?id=" + shoppingCart.getId() + "&missingRequiredField=true";
		
		
		Order order = orderService.createOrder(shoppingCart, shippingAddress, billingAddress, shippingMethod, user);
		
		mailSender.send(mailConstructor.constructOrderConfirmationEmail(user, order, Locale.ENGLISH));
		
		shoppingCartService.clearShoppingCart(shoppingCart);
		
		LocalDate today = LocalDate.now();
		LocalDate estimatedDeliveryDate;
		
		if (shippingMethod.equals("groundShipping")) {
			estimatedDeliveryDate = today.plusDays(7);
		} else {
			estimatedDeliveryDate = today.plusDays(5);
		}
		
		model.addAttribute("ShippingAddress", order.getShippingAddress());
		model.addAttribute("BillingAddress", order.getBillingAddress());
		model.addAttribute("order",order);
		
		model.addAttribute("estimatedDeliveryDate", estimatedDeliveryDate);
		
		return "orderConfirmationPage";
	}

	}	
	
	
	
	
	
	
	
	@RequestMapping("/setShippingAddress")
	public String setShippingAddress(
			@RequestParam("userShippingId") Long userShippingId,
			Principal principal, Model model,Authentication authentication
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
	UserShipping userShipping = userShippingService.findById(userShippingId);
		
		if(userShipping.getUser().getId() != user.getId()) {
			return "badRequestPage";
		} else {
			shippingAddressService.setByUserShipping(userShipping, shippingAddress);
			
			List<CartItem> cartItemList = cartItemService.findByShoppingCart(user.getShoppingCart());
			
			model.addAttribute("shippingAddress", shippingAddress);
			//model.addAttribute("payment", payment);
			model.addAttribute("billingAddress", billingAddress);
			model.addAttribute("cartItemList", cartItemList);
			model.addAttribute("shoppingCart", user.getShoppingCart());
			
			List<String> stateList = INDConstants.listOfIndianStateName;
			Collections.sort(stateList);
			model.addAttribute("stateList", stateList);
			
			List<UserShipping> userShippingList = user.getUserShippingList();
		//	List<UserPayment> userPaymentList = user.getUserPaymentList();
			
			model.addAttribute("userShippingList", userShippingList);
			//model.addAttribute("userPaymentList", userPaymentList);
			
			model.addAttribute("shippingAddress", shippingAddress);
			
			model.addAttribute("classActiveShipping", true);
			
			/*if (userPaymentList.size() == 0) {
				model.addAttribute("emptyPaymentList", true);
			} else {
				model.addAttribute("emptyPaymentList", false);
			}*/
			
			
			
			model.addAttribute("emptyShippingList", false);
			
			
			return "checkout";
		}
	}
	}
}
