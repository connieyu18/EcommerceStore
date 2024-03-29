package com.connie.EcommerceStore.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.connie.EcommerceStore.models.Event;
import com.connie.EcommerceStore.models.Product;
import com.connie.EcommerceStore.models.Review;
import com.connie.EcommerceStore.models.User;
import com.connie.EcommerceStore.services.EventService;
import com.connie.EcommerceStore.services.ProductService;
import com.connie.EcommerceStore.services.ReviewService;
import com.connie.EcommerceStore.services.UserService;

@Controller
public class EcommerceController {
	private final ProductService productService;
	private final EventService eventService;
	private final UserService userService;
	private final UsersController usersController;
	private final ReviewService reviewService;

	public EcommerceController(UsersController usersController, ProductService productService,
			EventService eventService, UserService userService, ReviewService reviewService) {
		this.productService = productService;
		this.eventService = eventService;
		this.userService = userService;
		this.reviewService = reviewService;
		this.usersController = usersController;
	}

	@RequestMapping("/edit/{id}")
	public String edit(@PathVariable("id") long id, @ModelAttribute("editProductObject") Product editProduct, Model model){
		Product product1=productService.findProduct(id);
		model.addAttribute("products", product1);
		return "admin.jsp";
	}
		
    @RequestMapping(value= "/editProduct/{id}", method=RequestMethod.POST)
	    public String editProduct (@PathVariable("id") long id, @Valid @ModelAttribute("editProductObject") Product product, BindingResult result) {
	    	if (result.hasErrors()) {
				System.out.println("bbb");
				return "admin.jsp";
			}
			else {
				productService.updateProduct(product);
				return "redirect:/admin";
			}
	    }

	@RequestMapping("/products/{id}/delete")
	public String delete(@PathVariable("id") long id) {
		productService.deleteProduct(id);
		return "redirect:/admin";
	}

	@RequestMapping(value = { "/productList" })
	public String home(Principal principal, Model model) {
		User u = usersController.addUserToModel(principal, model);
		Iterable<Product> products = productService.getAllProducts();
//		System.out.println("products\n" + ((ArrayList<Product>) products).get(0).getName());
		model.addAttribute("products", products);
		return "productList.jsp";
	}

	/**
	 * Increment the quantity of an order
	 * @param session
	 * @param id
	 * @param model
	 * @param principal
	 * @return
	 */
	@RequestMapping("/cart/{id}")
	public String Cart(HttpSession session, @PathVariable Long id, Model model, Principal principal) {
		Product product2 = productService.findProduct(id);
		User u = usersController.addUserToModel(principal, model);
		if (session.getAttribute("cart") == null) {
			HashMap<Product, Integer> cart = new HashMap<Product, Integer>();
			cart.put(product2, 1);
			session.setAttribute("cart", cart);
		} else {
			HashMap<Product, Integer> cart = (HashMap<Product, Integer>) session.getAttribute("cart");

//			System.out.print("******p**********\n" + product2);
			if (!cart.containsKey(product2)) {
				cart.put(product2, 1);
			} else {
				int count = cart.get(product2);
				cart.put(product2, ++count);
			}
			session.setAttribute("cart", cart);
		}
		return "redirect:/cart";
	}
	
	/**
	 * Decrements the quantity of an order
	 * @param session
	 * @param id
	 * @param model
	 * @param principal
	 * @return
	 */
	@RequestMapping("/cart/remove/{id}")
	public String removeItemInCart(HttpSession session, @PathVariable Long id, Model model, Principal principal) {
//		System.out.println("in checkout");

		Product p = productService.findProduct(id);

//		System.out.println(product2);

		User u = usersController.addUserToModel(principal, model);

		if (session.getAttribute("cart") != null) {
			HashMap<Product, Integer> cart = (HashMap<Product, Integer>) session.getAttribute("cart");
			if (cart.containsKey(p)) {
				int count = cart.get(p);
				if (count >= 1) {
					cart.put(p, --count);
				}
			}
			session.setAttribute("cart", cart);
		}
		return "redirect:/cart";
	}

	/**
	 * Delete an order
	 * @param session
	 * @param id
	 * @return
	 */
	@RequestMapping("/remove/{id}")
	public String remove(HttpSession session, @PathVariable("id") Long id) {
		HashMap<Product, Integer> cart = (HashMap<Product, Integer>) session.getAttribute("cart");
		Product prod = productService.findProduct(id);
		cart.remove(productService.findProduct(id));
		session.setAttribute("cart", cart);
		return "redirect:/cart";
	}


	//add product in admin page
	@RequestMapping("/admin/products/new")
	public String displayAddProductPage(@Valid @ModelAttribute("user") User user, Principal principal, Model model) {
		usersController.addUserToModel(principal, model);
		return "addProducts.jsp";
	}


	@RequestMapping("/cart")
	public String Cart(Principal principal, Model model) {
		User u = usersController.addUserToModel(principal, model);
		return "cart.jsp";
	}


	//show each product 
	@RequestMapping("/show/{id}")
	public String ShowProduct(@PathVariable Long id, Principal principal, @ModelAttribute("review") Review review, @ModelAttribute("searchProduct") String name, Model model) {
		Product product2 = productService.findProduct(id);
		model.addAttribute("product", product2);
		List review2 = product2.getReviews();
		model.addAttribute("reviews", review2);
		Integer avg=reviewService.getAvgRatingByProduct(id); 
		model.addAttribute("avgRating",avg);
		return "show.jsp";
	}
	

	//search bar 
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String search(@RequestParam("name") String name, Model model) {
		System.out.println("in search" + name);
		Product productByName = productService.findProductByName(name);
		if (productByName == null) {
			return "redirect:/productList";
		}
		System.out.println("in search111" + productByName.getId());
		model.addAttribute("product", productByName);
		return "redirect:/show/" + productByName.getId();
	}


	@RequestMapping(value = "/showCategoryProduct", method = RequestMethod.POST)
	public String ShowProductCategory(@RequestParam("category") String category, Model model) {
		List<Product> productsByCategory = productService.findProductByCategory(category);
		if (productsByCategory == null) {
			return "redirect:/productList";
		}
		model.addAttribute("productCat",productsByCategory);
		return "showCategory.jsp";
	}

//	@RequestMapping("/product/category")
//	public String ShowCat(Principal principal,@RequestParam("category") String category,
//			@ModelAttribute("showProductCat") Product productcat, Model model) {
////		Product productByName = productService.findProductByCategory(category);
//		User u = usersController.addUserToModel(principal, model);
//		Product productByName = productService.findProductByCategory(category);
//
//		model.addAttribute("productsCategory", productByName);
//		return "showCategory.jsp";
//	}

	@RequestMapping("/newEvent")
	public String newEvent(Principal principal, @ModelAttribute("event") Event event, Model model) {
		User u = usersController.addUserToModel(principal, model);
		List<Event> event2 = u.getEvents();
		model.addAttribute("allEvents", event2);
		model.addAttribute("products", productService.getAllProducts());
		return "form.jsp";
	}
	
	@RequestMapping("/events/new")
	public String newEvent(@ModelAttribute("event") Event Event) {
		return "form.jsp";
	}
	
	@RequestMapping("/removeEvent/{id}")
	public String removeEvent(@PathVariable("id") Long id) {
		eventService.deleteEvent(id);
		return "redirect:/newEvent";
	}

	@RequestMapping(value = "/addEvent", method = RequestMethod.POST)
	public String addEvent(Principal principal, @Valid @ModelAttribute("event") Event event,
			@RequestParam("products") Long id, BindingResult result, Model model) {
		if (result.hasErrors()) {
			System.out.println("ddd");
			return "form.jsp";
		} else {
			User u = usersController.addUserToModel(principal, model);
			List<Event> event2 = u.getEvents();
			event.setUser(u);
			event.setProduct(productService.findProduct(id));
			Event event1 = eventService.save(event);
			return "redirect:/newEvent";
		}
	}

	@RequestMapping(value = "/createReview", method = RequestMethod.POST)
	public String addRating(Principal principal, @Valid @ModelAttribute("review") Review review,
			@RequestParam("rating") int rating, BindingResult result, Model model) {
		if (result.hasErrors()) {
			System.out.println("in add rating");
			return "show.jsp";
		} else {
			System.out.println("in create rate" + review);
			User u = usersController.addUserToModel(principal, model);
			List<Review> review2 = u.getReviews();
			review.setUser(u);
			Product product1 = review.getProduct();
			System.out.println("in creating review:" + product1.getId());
			Review review1 = reviewService.save(review);
			return "redirect:/show/" + product1.getId();
		}
	}

	@RequestMapping(value = "/deleteReview/{id}")
	public String removeReview(@PathVariable("id") Long id ) {
		reviewService.deleteReview(id);
		return "redirect:/productList";
	}
}
//		return  "redirect:/show/" + product1.getId();

//		@RequestMapping("/checkout/{id}")
//		public String Checkout(HttpSession session, Model model) {
//			System.out.println("in checkout");
//	    	Long userId=(Long)session.getAttribute("userId"); 
//	    	User u=userService.findUserById(userId); 
//	    	model.addAttribute("user", u); 
////	    	List<Product> usersProducts= u.getProducts(); 
////	    	model.addAttribute("products", usersProducts);
//	    	System.out.println("bbb" + u.getProducts() );
//		return "cart.jsp";
//		}

//	@RequestMapping(value = "/showCategoryProduct", method = RequestMethod.POST)
//	public String ShowProductCategory(@RequestParam("category") String category, Model model) {
////			System.out.println("in productcat"+ product.getCategory());
//		Product productByName = productService.findProductByCategory(category);
//		return "redirect:/show/" + productByName.getId();
//	}

//	@RequestMapping("/product/category")
//	public String ShowCat(HttpSession session, @RequestParam("email") String email,
//			@ModelAttribute("showProductCat") Product productcat, Model model) {
//		
//		User u = usersController.addUserToModel(session, model);
//		return "showCategory.jsp";
//	}

//	@PostMapping("addProducts")
//	public String create(@Valid @ModelAttribute("event") Event event,BindingResult result, Principal principal, 
//			Model model) {
//		System.out.println("create");
//		if (result.hasErrors()) {
//			System.out.println("result.hasErrors()");
//			return setUpHomePage(model, principal, event, result);
//		}else
//			eventService.createEvent(event, getCurrentUser(principal));
////		userService.saveUserWithAdminRole(user);
//		return "redirect:/events/passingMessage/successfulCreate";
//	}

//			System.out.println("in search"+ name);
//			Product productByName= productService.findProductByName(name); 
//			if(productByName==null) {
//				return "redirect:/productList"; 
//			}
//			
//			System.out.println("in search111"+ productByName.getId());
//			model.addAttribute("product",productByName);
//			return "redirect:/show/" + productByName.getId();
////			return "redirect:/show/{id}"; //or redirect /login
//		}
