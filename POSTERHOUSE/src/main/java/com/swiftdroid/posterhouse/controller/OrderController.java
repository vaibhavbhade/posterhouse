package com.swiftdroid.posterhouse.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.swiftdroid.posterhouse.config.CustomOAuth2User;
import com.swiftdroid.posterhouse.model.Order;
import com.swiftdroid.posterhouse.model.User;
import com.swiftdroid.posterhouse.service.OrderService;
import com.swiftdroid.posterhouse.service.UserService;

@Controller
public class OrderController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderService orderService;
	
@SuppressWarnings("finally")
@RequestMapping("/userOrder")
public String orderPage(Model model,Authentication authentication,Principal principal) {
	
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
List<Order> orderList =user.getOrderList();
model.addAttribute("orderList", orderList);
model.addAttribute("user",user);
	return "orderPage";	

}
}

@SuppressWarnings("finally")
@RequestMapping("/orderDetail")
public String orderPage(@RequestParam("id")Long orderId, Model model,Authentication authentication,Principal principal) {
	
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
		
		Order order = orderService.findOrderById(orderId);
		
		if(order.getUser().getId()!=user.getId()) {
			return "badRequestPage";
		}
		
    model.addAttribute("user",user);
    model.addAttribute("ShippingAddress", order.getShippingAddress());
	model.addAttribute("BillingAddress", order.getBillingAddress());
	model.addAttribute("order",order);
    model.addAttribute("estimatedDeliveryDate", order.getEstimateDate());
    
    
	return "orderDetails";	

}
}


}
