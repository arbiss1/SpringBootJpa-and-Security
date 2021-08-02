package net.codejava.Domains;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.codejava.Domains.OrderList;
import net.codejava.Domains.Orders;
import net.codejava.Domains.User;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
		String username = authentication.getName(); // merr username e perdoruesit
		Optional<User> user = repo.findByUsername(username); // gjen perdoruesin me emrin e futur ne databaze
		List<Orders> listOrders = service.listAllByUser(user.get().getUserId()); // liston gjithe porosite e userit perkates
		model.addAttribute("listOrders", listOrders);

		return "index";
	}

	@RequestMapping("/homepage")
		public String homepage(){

			return "homepage";
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
	public String showNewProductPage(Model model , OrderList choseOrder ) {
		List<Orders> listOrders = service.listAll();
		List<OrderList> orderList = orderlistService.listAllorders();
		Orders order = new Orders();
//		OrderList choseOrder = new OrderList();
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
		String lastName = user.get().getLastName();
		order.setUser_address(user.get().getUser_address());
		order.setUser_number(user.get().getUser_number());
		order.setCustomer(user.get().getFirst_name() + " " +  lastName);
		System.out.println(lastName);
			String[] listNameSplit = orderList.getListName().split("[(]|[)]");
			String category = listNameSplit[0];
			String price = listNameSplit[1];
			orderList.setListName(category);
			System.out.println(orderList.getListName());

		order.setOrderList(orderList);//krijon nje orderlist te ri brenda order.OrderList
		order.setUserId(user.get().getUserId());
//		orderList.getListName().substring(orderList.getListName().indexOf("(")+1,orderList.getListName().indexOf(")"));
		order.setPrice(price);

		repoOrders.save(order);
		model.addAttribute("order", order);
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
		order.setCustomer(orderEdit.getCustomer());
		order.setPrice(order.getPrice());
		repoOrders.save(order);
		model.addAttribute("order", order);
		if (user.get().getRoles().equals("USER")) {
			return "redirect:/user";
		} else {
			return "redirect:/admin";
		}
	}

	@RequestMapping("/edit/{id}")
	public ModelAndView showEditOrderPage(@PathVariable(name = "id") int id) {
		ModelAndView mav = new ModelAndView("edit_product");
		Orders order = service.get(id);
		System.out.println(order);
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
	public String addNewProduct(Model model){
		List<OrderCategory> categories = categoryService.listAll();
		model.addAttribute("listCategory", categories);
		return "new_product_listItems";
	}

	@RequestMapping(value = "/saveProduct", method = RequestMethod.POST)
	public String saveProduct(@ModelAttribute("category")OrderCategory orderCategory, Model model) {
		String categoryName = orderCategory.getCategory();
		System.out.println(categoryName);
		return "new_product_listItems";
	}

	@RequestMapping("/registerCategory")
	public String saveCategory(Model model) {
		OrderCategory category = new OrderCategory();
		model.addAttribute("category", category);
		return "new_category_listItems";
	}

	@RequestMapping("/process_register_category")
	public String processRegister(OrderCategory orderCategory) {
		categoryRepo.save(orderCategory);
		return "redirect:/admin";
	}
	@RequestMapping("/listAllCategories")
	public String listCategories(Model model){
		List<OrderCategory> listCategories = categoryService.listAll();
		model.addAttribute("listCategories" , listCategories);
		System.out.println(listCategories);
		return "new_category_listItems";
	}

}
