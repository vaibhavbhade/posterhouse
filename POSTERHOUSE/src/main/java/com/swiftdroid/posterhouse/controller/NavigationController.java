package com.swiftdroid.posterhouse.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.swiftdroid.posterhouse.config.CustomOAuth2User;
import com.swiftdroid.posterhouse.model.AuthenticationProvider;
import com.swiftdroid.posterhouse.model.Product;
import com.swiftdroid.posterhouse.model.ProductConfig;
import com.swiftdroid.posterhouse.model.ProductType;
import com.swiftdroid.posterhouse.model.Role;
import com.swiftdroid.posterhouse.model.User;
import com.swiftdroid.posterhouse.model.UserRole;
import com.swiftdroid.posterhouse.model.UserShipping;
import com.swiftdroid.posterhouse.service.CategoryService;
import com.swiftdroid.posterhouse.service.ProductConfigService;
import com.swiftdroid.posterhouse.service.ProductService;
import com.swiftdroid.posterhouse.service.UserService;
import com.swiftdroid.posterhouse.service.UserShippingService;
import com.swiftdroid.posterhouse.serviceimpl.UserSecurityService;
import com.swiftdroid.posterhouse.utility.INDConstants;
import com.swiftdroid.posterhouse.utility.MailConstructor;
import com.swiftdroid.posterhouse.utility.SecurityUtility;

@Controller
public class NavigationController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserShippingService userShippingService;
	
	@Autowired
	private UserSecurityService userSecurityService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ProductConfigService productConfigService;
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private MailConstructor mailConstructor;
	

	

	
    @GetMapping("/fetchProductBysizeandproductId")
	public String getProductsizeAndProductWise(@RequestParam("productId") long producttId,@RequestParam("size")String size,Model model) {

	Product product=productService.findProductById(producttId);
		ProductConfig productConfig=productConfigService.findProductConfigByProductAndSize(product,size);
		//ProductConfig productConfig=null;
		model.addAttribute("categoryId", product.getProductType().getId());
	     model.addAttribute("categoryName", product.getProductType().getProductTypeName());
		
		ProductType productType=categoryService.findCategoryById(product.getProductType().getId());//fetch category by cat id
		
	List<ProductConfig> productConfigList=productConfigService.findProductConfigByProductId(product);
		
		//ProductConfig productConfig=productConfigList.get(0);
		
		List<String> sizeList=new ArrayList<>();
	
		
		for (ProductConfig productConfigdata : productConfigList) {
			sizeList.add(productConfigdata.getSize());
		}
	
		
		System.out.println("productId::"+producttId);
		System.out.println("size:: "+size);
		System.out.println(productConfig.toString());
		
		model.addAttribute("productConfig", productConfig);
		
		model.addAttribute("productType", productType);
		
		model.addAttribute("product",product);
		
		
		List<Integer> qtyList=Arrays.asList(1,2,3,4,5,6,7,8,9,10);

//		List<ProductConfig> sizeList=productConfigService.findProductSize(product.getId());
		
		System.out.println("#########################################################################");
		System.out.println(sizeList.size());
		
		model.addAttribute("qtyList", qtyList);

		model.addAttribute("sizeList", sizeList);
	//	model.addAttribute("size",sizeList.get(0));
		model.addAttribute("qty", 1);
		
		return "single-product";
	
	}
	

	
	
	
	
	
	
	
	
	@RequestMapping("/fetchProduct")
	public String getAllproduct(Model model) {
		
	//List<Long> catCount=new ArrayList<>();//category count
	Map<Long, ProductType> catCount = new HashMap<Long, ProductType>(); 
	
		
		List<ProductType> categoryList=categoryService.findAllProductType();
		for (ProductType productType : categoryList) {
			
		      System.out.println("count ::    "+productType.getProductTypeName()+"  :" +      productService.countProductCatIdWise(productType.getId()));
		    catCount.put(productService.countProductCatIdWise(productType.getId()),productType);
		     
		}
		
		
      List<Product> productList =productService.getAllProduct();
		
      
      model.addAttribute("catgoryCountList",catCount);
		model.addAttribute("productList", productList);
	//	model.addAttribute("categoryList", categoryList);
		return "category";
			
	}
	
	@RequestMapping("/fetchProductCategoryWise")
	public String fetchProductCategoryWise(Model model,@RequestParam("id")Long id  ) {

		Map<Long, ProductType> catCount = new HashMap<Long, ProductType>(); 

		List<ProductType> categoryList=categoryService.findAllProductType();
	
		
		for (ProductType productType : categoryList) {
			
		      System.out.println("count ::    "+productType.getProductTypeName()+"  :" +      productService.countProductCatIdWise(productType.getId()));
		    catCount.put(productService.countProductCatIdWise(productType.getId()),productType);
		     
		}
	
		
		ProductType productType=categoryService.findCategoryById(id);
		
		
        List<Product> productList =productService.getProductCategoryWise(productType);
      
        System.out.println(productList.isEmpty());
        if(productList.isEmpty()) 
        {
        	model.addAttribute("emptyProductList", true);
        }
        		
        
        
        
        model.addAttribute("catgoryCountList",catCount);
        model.addAttribute("productList", productList);
		model.addAttribute("categoryList", categoryList);
		return "category";
			
	}
	
	
	@RequestMapping("/productDetail")
	public String BookDetails(Model model,Principal principal,@PathParam("id") Long id,Authentication authentication ) {
		
		 if(principal!=null) {
			 User user=null;
				try {
				CustomOAuth2User oAuth2User=(CustomOAuth2User) authentication.getPrincipal();
		        String email=oAuth2User.getEmail();
				System.out.println("eeeeeeeeeeeeeeee"+email);
				user = userService.findByEmail(email);
				}catch (Exception e) {
					user = userService.findByUsername(principal.getName());
				} 
			 
		 }
		Product product=productService.findProductById(id);//fetch product by product id
		
		
		
		//System.out.println("Category id :=  "+product.getProductType().getId());
		
		model.addAttribute("categoryId", product.getProductType().getId());
		
		//System.out.println("caregory name="+ product.getProductType().getProductTypeName());
		
		model.addAttribute("categoryName", product.getProductType().getProductTypeName());
		
		ProductType productType=categoryService.findCategoryById(product.getProductType().getId());//fetch category by cat id
		
		List<ProductConfig> productConfigList=productConfigService.findProductConfigByProductId(product);
		
		ProductConfig productConfig=productConfigList.get(0);
		
		List<String> sizeList=new ArrayList<>();
	
		
		for (ProductConfig productConfigdata : productConfigList) {
			sizeList.add(productConfigdata.getSize());
		}
		//System.out.println("product con Id:::"+product.getId());

		//System.out.println("productConfig con Id:::"+productConfig.getId());

		//System.out.println(productConfig.toString());
		
		model.addAttribute("productConfig", productConfig);
		
		model.addAttribute("productType", productType);
		
		model.addAttribute("product",product);
		
		
		List<Integer> qtyList=Arrays.asList(1,2,3,4,5,6,7,8,9,10);

//		List<ProductConfig> sizeList=productConfigService.findProductSize(product.getId());
		
		System.out.println("#########################################################################");
		System.out.println(sizeList.size());
		
		model.addAttribute("qtyList", qtyList);

		model.addAttribute("sizeList", sizeList);
	//	model.addAttribute("size",sizeList.get(0));
		model.addAttribute("qty", 1);
		
		return "single-product";
		
	}
	
	@RequestMapping("/")
	public String check(Model model) {
		String str="Poster House | Home";
		model.addAttribute("page-title",str);
		return "index";
		
	}
	
	@RequestMapping("/login")
	public String loginPage() {
		Authentication	authentication=SecurityContextHolder.getContext().getAuthentication();
		if(authentication==null || authentication instanceof AnonymousAuthenticationToken) {
		return "login";
		}
		return "redirect:/";
	}
	
	@RequestMapping("/registration")
	public String userRegistration() {
		Authentication	authentication=SecurityContextHolder.getContext().getAuthentication();
		if(authentication==null || authentication instanceof AnonymousAuthenticationToken) {
	
		return "registration";
		}
		return "redirect:/";
	}
	

	@PostMapping("/newUser")
	public String createNewUser(Model model,HttpServletRequest request,@ModelAttribute("email") String userEmail,@ModelAttribute("username") String username,@ModelAttribute("firstname") String firstname,@ModelAttribute("lastname") String lastname,@ModelAttribute("password") String password,@ModelAttribute("phonenumber") String phonenumber) throws Exception {
		model.addAttribute("classActiveNewAccount",true);
		model.addAttribute("email",userEmail);
		model.addAttribute("username",username);
		
		System.out.println(userEmail+" "+username+" "+password+" "+firstname);
		
		if(userService.findByUsername(username) != null)
		{
			model.addAttribute("usernameExists",true);
			return "registration";
		}
		
		
		if(userService.findByEmail(userEmail) !=null) {
			model.addAttribute("emailExists",true);
			return "registration";
		}
		
		User user=new User();
		user.setUsername(username);
		user.setEmail(userEmail);
		String encryptedPassword=passwordEncoder.encode(password);
		user.setPassword(encryptedPassword);
		user.setFirstName(firstname);
		user.setLastName(lastname);
		user.setPhone(phonenumber);
		user.setCretedBy(firstname);
		user.setCretedDate(new Date());
		user.setAuthProvider(AuthenticationProvider.LOCAL);
		
		Role role=new Role();
		role.setRoleId(1);
		role.setName("ROLE_USER");
		
		Set<UserRole> userRoles=new HashSet<>();
		
		
		userRoles.add( new UserRole(user,role));
		
		userService.createUser(user,userRoles);
		model.addAttribute("registerSuccess", true);
		return "registration";
		
	}
	
	
	
	@RequestMapping("/myProfile")
	public String myProfilePage(Model model,Principal principal,Authentication authentication) {

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
		model.addAttribute("user", user);
		System.out.println("user id************************+ "+user.getId());
	//	model.addAttribute("userPaymentList", user.getUserPaymentList());
		//model.addAttribute("userShippingList", user.getUserShippingList());
		//model.addAttribute("orderList", user.getOrderList());
		
		UserShipping userShipping = new UserShipping();
		model.addAttribute("userShipping", userShipping);
		
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("listOfShippingAddresses", true);
		
		List<String> stateList = INDConstants.listOfIndianStateName;
		Collections.sort(stateList);
		model.addAttribute("stateList", stateList);
		model.addAttribute("classActiveEdit", true);
		
		return "myProfile";
		}
	}
	@RequestMapping("/forgetPassword")
	public  String forgetPassword(Model model,@RequestParam("email")String email,HttpServletRequest request){
		
		model.addAttribute("classActiveForgetPassword", true);
		
		User user=userService.findByEmail(email);
		
		if(user == null) {
			model.addAttribute("emailNotExists",true);
			return "login";
		}
		
String password=SecurityUtility.randomPassword();
		
		String encryptedPassword=passwordEncoder.encode(password);
		
		user.setPassword(encryptedPassword);
		
		userService.save(user);
		try {
		mailSender.send(mailConstructor.constructPasswordEmail(user, password, Locale.ENGLISH));
		}catch (Exception e) {
			// TODO: handle exception
			model.addAttribute("forgetPasswordEmailSent", false);

			return "badRequestPage";
		}


		model.addAttribute("forgetPasswordEmailSent", true);
		
		return "login";
		
	}

	
	
	
	@RequestMapping(value="/addNewShippingAddress", method=RequestMethod.POST)
	public String addNewShippingAddressPost(
			@ModelAttribute("userShipping") UserShipping userShipping,
			Principal principal, Model model,Authentication authentication
			){
		
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
		
		userService.updateUserShipping(userShipping, user);
		
		model.addAttribute("user", user);
		//model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("userShippingList", user.getUserShippingList());
		model.addAttribute("listOfShippingAddresses", true);
		model.addAttribute("classActiveShipping", true);
		model.addAttribute("listOfCreditCards", true);
		//model.addAttribute("orderList", user.getOrderList());
		
		return "myProfile";
	}
	}
	@RequestMapping("/addNewShippingAddress")
	public String addNewShippingAddress(
			Model model, Principal principal,Authentication authentication
			){
		
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
				model.addAttribute("user", user);
			
		
		model.addAttribute("addNewShippingAddress", true);
		model.addAttribute("classActiveShipping", true);
	//	model.addAttribute("listOfCreditCards", true);
		
		UserShipping userShipping = new UserShipping();
		
		model.addAttribute("userShipping", userShipping);
		
		List<String> stateList = INDConstants.listOfIndianStateName;
		Collections.sort(stateList);
		model.addAttribute("stateList", stateList);
		//model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("userShippingList", user.getUserShippingList());
		//model.addAttribute("orderList", user.getOrderList());
		
		return "myProfile";
			}
	}
	@SuppressWarnings("finally")
	@RequestMapping("/listOfShippingAddresses")
	public String listOfShippingAddresses(
			Model model, Principal principal, HttpServletRequest request,
			Authentication authentication) {
		
               User user=null;
		System.out.println("principle namw::::: "+principal.getName());
		try {
			CustomOAuth2User oAuth2User=(CustomOAuth2User) authentication.getPrincipal();
	        String email=oAuth2User.getEmail();
		//	System.out.println("eeeeeeeeeeeeeeee"+email);
			user = userService.findByEmail(email);
			}catch (Exception e) {
				user = userService.findByUsername(principal.getName());
			}
			finally {		
				
				model.addAttribute("user", user);
			for (UserShipping list : user.getUserShippingList()) {
				System.out.println("list::"+list);

			}
		//model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("userShippingList", user.getUserShippingList());
	//	model.addAttribute("orderList", user.getOrderList());
		
		//model.addAttribute("listOfCreditCards", true);
		model.addAttribute("classActiveShipping",true);
		model.addAttribute("listOfShippingAddresses",true);
		
		
		return "myProfile";
	}
}
	
	@RequestMapping("/updateUserShipping")
	public String updateUserShipping(
			@ModelAttribute("id") Long shippingAddressId, Principal principal, Model model,
			Authentication authentication) {
		User user=null;
		try {
			CustomOAuth2User oAuth2User=(CustomOAuth2User) authentication.getPrincipal();
	        String email=oAuth2User.getEmail();
			System.out.println("eeeeeeeeeeeeeeee"+email);
			user = userService.findByEmail(email);
			}catch (Exception e) {
				user = userService.findByUsername(principal.getName());
			}
			finally
			{	
				UserShipping userShipping = userShippingService.findById(shippingAddressId);
			
		
		if(user.getId() != userShipping.getUser().getId()) {
			return "badRequestPage";
		} else {
			model.addAttribute("user", user);
			
			model.addAttribute("userShipping", userShipping);
			
			List<String> stateList = INDConstants.listOfIndianStateName;
			Collections.sort(stateList);
			model.addAttribute("stateList", stateList);
			
			model.addAttribute("addNewShippingAddress", true);
			model.addAttribute("classActiveShipping", true);
			model.addAttribute("listOfCreditCards", true);
			
	//		model.addAttribute("userPaymentList", user.getUserPaymentList());
			model.addAttribute("userShippingList", user.getUserShippingList());
		//	model.addAttribute("orderList", user.getOrderList());
			return "myProfile";
		}
			}
		
	}
	
	@RequestMapping(value="/setDefaultShippingAddress", method=RequestMethod.POST)
	public String setDefaultShippingAddress(
			@ModelAttribute("defaultShippingAddressId") Long defaultShippingId, Principal principal, Model model,Authentication authentication
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
			finally
			{	
		userService.setUserDefaultShipping(defaultShippingId, user);
		
		model.addAttribute("user", user);
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("classActiveShipping", true);
		model.addAttribute("listOfShippingAddresses", true);
		
		//model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("userShippingList", user.getUserShippingList());
	//	model.addAttribute("orderList", user.getOrderList());
		
		return "myProfile";
	}
	}
	
	@RequestMapping("/removeUserShipping")
	public String removeUserShipping(
			@ModelAttribute("id") Long userShippingId, Principal principal, Model model,Authentication authentication
			){
		
		User user=null;
		try {
			CustomOAuth2User oAuth2User=(CustomOAuth2User) authentication.getPrincipal();
	        String email=oAuth2User.getEmail();
			System.out.println("eeeeeeeeeeeeeeee"+email);
			user = userService.findByEmail(email);
			}catch (Exception e) {
				user = userService.findByUsername(principal.getName());
			}
			finally
			{	
		
	    UserShipping userShipping = userShippingService.findById(userShippingId);
		
		if(user.getId() != userShipping.getUser().getId()) {
			return "badRequestPage";
		} else {
			model.addAttribute("user", user);
			
			userShippingService.removeById(userShippingId);
			
			model.addAttribute("listOfShippingAddresses", true);
			model.addAttribute("classActiveShipping", true);
			
			System.out.println("#############################################"+user.getUserShippingList());
			
		//	model.addAttribute("userPaymentList", user.getUserPaymentList());
			model.addAttribute("userShippingList", user.getUserShippingList());
		//  model.addAttribute("orderList", user.getOrderList());
			
			return "myProfile";
		}
			}
}
	
	@RequestMapping(value="/updateUserInfo", method=RequestMethod.POST)
	public String updateUserInfo(
			@ModelAttribute("user") User user,
			@ModelAttribute("newPassword") String newPassword,
			Model model
			) throws Exception {
		
		User currentUser = userService.findById(user.getId());
		
		if(currentUser == null) {
			throw new Exception ("User not found");
		}
		
		/*check email already exists*/
		if (userService.findByEmail(user.getEmail())!=null) {
			if(userService.findByEmail(user.getEmail()).getId() != currentUser.getId()) {
				model.addAttribute("emailExists", true);
				return "myProfile";
			}
		}
		
		/*check username already exists*/
		if (userService.findByUsername(user.getUsername())!=null) {
			if(userService.findByUsername(user.getUsername()).getId() != currentUser.getId()) {
				model.addAttribute("usernameExists", true);
				return "myProfile";
			}
		}
		
//		update password
		if (newPassword != null && !newPassword.isEmpty() && !newPassword.equals("")){
			//BCryptPasswordEncoder passwordEncoder = SecurityUtility.passwordEncoder();
				currentUser.setPassword(passwordEncoder.encode(newPassword));
				
		}else{
			model.addAttribute("incorrectPassword", true);
		    return "myProfile";
		}
		currentUser.setFirstName(user.getFirstName());
		currentUser.setLastName(user.getLastName());
		currentUser.setUsername(user.getUsername());
		currentUser.setEmail(user.getEmail());
		
		userService.save(currentUser);
		
		model.addAttribute("updateSuccess", true);
		model.addAttribute("user", currentUser);
		model.addAttribute("classActiveEdit", true);
		
		UserDetails userDetails = userSecurityService.loadUserByUsername(currentUser.getUsername());

		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
				userDetails.getAuthorities());
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		model.addAttribute("orderList", user.getOrderList());
		
		return "myProfile";
	}

}


