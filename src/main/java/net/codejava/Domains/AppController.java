package net.codejava.Domains;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.codejava.Repositories.OrderCategoryRepository;
import net.codejava.Repositories.OrderListRepository;
import net.codejava.Repositories.OrdersRepository;
import net.codejava.Repositories.UserRepository;
import net.codejava.Services.OrderCategoryService;
import net.codejava.Services.OrderListService;
import net.codejava.Services.OrderService;
import net.codejava.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppController {

	@Autowired
	private OrderService service;
	@Autowired
	private UserRepository repo;
	@Autowired
	private UserService userService;
	@Autowired
	private OrderListService orderlistService;
	@Autowired
	private OrdersRepository repoOrders;
	@Autowired
	private OrderListRepository listRepo;
	@Autowired
	private OrderCategoryService categoryService;
	@Autowired
	private OrderCategoryRepository categoryRepo;


	@RequestMapping("/user")
	public String viewHomePageUser(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		Optional<User> user = repo.findByUsername(username);
		List<Orders> listOrders = service.listAllByUser(user.get().getUserId());
		model.addAttribute("listOrders", listOrders);

		return "index";
	}

	@RequestMapping("/admin")
	public String viewHomePageAdmin(Model model) {

		List<Orders> listOrders = service.listAll();
		model.addAttribute("listOrders", listOrders);

		return "index";
	}

	@RequestMapping("/login")
	public String showUserSignin() {
		return "signinUser";
	}

	@RequestMapping("/new")
	public String showNewProductPage(Model model , OrderList choseOrder , OrderCategory choseCategory) {
		List<Orders> listOrders = service.listAll();
		List<OrderCategory> listCategory = categoryService.listAll();
		List<OrderList> orderList = orderlistService.listAllorders();
		System.out.println(choseCategory.getCategory());
		Orders order = new Orders();
		model.addAttribute("choseCategory" , choseCategory);
		model.addAttribute("listCategory" , listCategory);
		model.addAttribute("listOrders", listOrders);
		model.addAttribute("order", order);
		model.addAttribute("orderList", orderList);
		model.addAttribute("choseOrder", choseOrder);
		return "new_product";
	}

	@RequestMapping("/register")
	public String showRegistrationForm(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		return "signup_form";
	}

	@RequestMapping("/process_register")
	public String processRegister(User user) {
		String firstnameUppercase = user.getfirst_name().substring(0, 1).toUpperCase(Locale.ROOT)
				+  user.getfirst_name().substring(1).toLowerCase();
		user.setFirst_name(firstnameUppercase);
		String lastnameUppercase = user.getLastName().substring(0,1).toUpperCase(Locale.ROOT)
				+ user.getLastName().substring(1).toLowerCase();
		user.setLastName(lastnameUppercase);
		System.out.println(firstnameUppercase);
		repo.save(user);
		return "signinUser";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String saveOrder(@ModelAttribute("product") Orders order, OrderList orderList, Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		Optional<User> user = repo.findByUsername(username);
		model.addAttribute("order", order);
		if (user.get().getRoles().equals("USER")) {
			return "redirect:/user";
		} else {
			return "redirect:/admin";
		}
	}

	@PostMapping(value = "/proccess-save")
	public String processRegisterSave(Model model , Orders order  , OrderList orderList) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		System.out.println("here");
		String username = authentication.getName();
		System.out.println(username);
		List<OrderList> listAllProdcts = orderlistService.listAllorders();
		Optional<User> user = repo.findByUsername(username);;
		String orderName = orderList.getListName();
		order.setUser_address(user.get().getUser_address());
		order.setUser_number(user.get().getUser_number());
		String lastName = user.get().getLastName();
		order.setCustomer(user.get().getFirst_name() + " " +  lastName);
		order.setUserId(user.get().getUserId());
		int quantity = order.getQuantity();
		Optional<OrderList> orderListDb = listRepo.findByListName(orderList.getListName());
		OrderCategory category = orderlistService.get(orderList.getListName()).getCategory();
		String priceWithout$sign = orderListDb.get().getPrice().substring(0,orderListDb.get().getPrice().length() - 1);
		System.out.println(priceWithout$sign);
		double sumofPrice = (Double.valueOf(priceWithout$sign)  * quantity);
		System.out.println(sumofPrice);
		order.setTotalPrice(Integer.toString((int) sumofPrice) + "$");
		order.setPrice(orderListDb.get().getPrice());
		order.setCategory(category);
		order.setListName(orderList.getListName());
		repoOrders.save(order);
		if (user.get().getRoles().equals("USER")) {
			return "redirect:/user";
		} else {
			return "redirect:/admin";
		}
	}



	@RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
	public String editOrder(@PathVariable(name = "id") int id,Orders orderEdit,  Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		Orders order = service.get(id);
		Optional<User> user = repo.findByUsername(username);
		order.setUserId(user.get().getUserId());
		order.setUser_address(orderEdit.getUser_address());
		order.setQuantity(orderEdit.getQuantity());
		order.setUser_number(orderEdit.getUser_number());
		System.out.println(orderEdit.getCustomer());
		order.setCustomer(orderEdit.getCustomer());
		String priceWithout$sign = order.getPrice().substring(0,order.getPrice().length() - 1);
		Integer quantity = order.getQuantity();
		double newSum = (Double.valueOf(priceWithout$sign) *quantity);
		order.setTotalPrice(Integer.toString((int)newSum )+"$");
		repoOrders.save(order);
		model.addAttribute("order", order);
		if (user.get().getRoles().equals("USER")) {
			return "redirect:/user";
		} else {
			return "redirect:/admin";
		}
	}

	@RequestMapping("/edit/{id}")
	public ModelAndView showEditOrderPage(@PathVariable(name = "id") int id ,Orders orderEdit) {
		ModelAndView mav = new ModelAndView("edit_product");
		Orders order = service.get(id);
		System.out.println(order);
		order.setCustomer(orderEdit.getCustomer());
		mav.addObject("order", order);
		System.out.println(mav);
		repoOrders.save(order);
		return mav;
	}

	@RequestMapping("/delete/{id}")
	public String deleteOrder(@PathVariable(name = "id") int id) {
		service.delete(id);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		Optional<User> user = repo.findByUsername(username);
		user.get().getRoles().equals("USER");
		if (user.get().getRoles().equals("USER")) {
			return "redirect:/user";
		}
		else if (user.get().getRoles().equals("ADMIN")) {
			return "redirect:/admin";
		}else {
			return "redirect:/";
		}
	}
	@RequestMapping("/new_product_listItems")
	public String showProductForm(Model model , OrderCategory choseCategory) {
		OrderList orderList = new OrderList();
		List<OrderCategory> listCategory = categoryService.listAll();
		model.addAttribute("listCategory", listCategory);
		System.out.println(listCategory);
		model.addAttribute("choseCategory", choseCategory);
		model.addAttribute("orderList", orderList);
		return "new_product_listItems";
	}

	@RequestMapping("/process_register_product")
	public String processRegisterProduct(Model model , OrderList orderList , OrderCategory listCategory) {
		String categoryName = listCategory.getCategory();
		model.addAttribute("listCategory" , listCategory);
		listRepo.save(orderList);
		return "redirect:/admin";
	}
	@RequestMapping("/registerCategory")
	public String saveCategory(Model model , OrderCategory choseCategory) {
		OrderCategory category = new OrderCategory();
		List<OrderCategory> listCategories = categoryService.listAll();
		model.addAttribute("choseCategory",choseCategory);
		model.addAttribute("listCategories" , listCategories);
		model.addAttribute("category", category);
		return "new_category_listItems";
	}

	@RequestMapping("/process_register_category")
	public String processRegister(OrderCategory orderCategory) {
		categoryRepo.save(orderCategory);
		return "redirect:/registerCategory";
	}
	@RequestMapping("/delete-category/{categoryId}")
	public String deleteCategory(@PathVariable(name = "categoryId")long categoryId) {
		System.out.println(categoryId);
		categoryService.delete(categoryId);
			return "redirect:/registerCategory";
	}

}
