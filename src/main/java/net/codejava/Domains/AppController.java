package net.codejava.Domains;

import java.util.List;
import java.util.Optional;

import net.codejava.Domains.OrderList;
import net.codejava.Domains.Orders;
import net.codejava.Domains.User;
import net.codejava.Repositories.OrderListRepository;
import net.codejava.Repositories.OrdersRepository;
import net.codejava.Repositories.UserRepository;
import net.codejava.Services.OrderListService;
import net.codejava.Services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
	private OrderListService orderlistService;
	@Autowired
	private OrdersRepository repoOrders;
	@Autowired
	private OrderListRepository listRepo;


	@RequestMapping("/user")
	public String viewHomePageUser(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName(); // merr username e perdoruesit
		Optional<User> user = repo.findByUsername(username); // gjen perdoruesin me emrin e futur ne databaze
		List<Orders> listOrders = service.listAllByUser(user.get().getUserId()); // liston gjithe porosite e userit perkates
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
	public String showNewProductPage(Model model) {
		List<Orders> listOrders = service.listAll();
		List<OrderList> orderList = orderlistService.listAllorders();
		Orders order = new Orders();
		OrderList choseOrder = new OrderList();
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
		repo.save(user);
		return "signup_form";
	}


	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String saveOrder(@ModelAttribute("product") Orders order, OrderList orderList, Model model) {
		System.out.println(orderList + "orderlist");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		Optional<User> user = repo.findByUsername(username);
		order.setOrderList(orderList);
		order.setUserId(user.get().getUserId());
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
		order.setAddress(orderEdit.getAddress());
		order.setQuantity(orderEdit.getQuantity());
		order.setNrCel(orderEdit.getNrCel());
		order.setCustomer(orderEdit.getCustomer());
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
}
