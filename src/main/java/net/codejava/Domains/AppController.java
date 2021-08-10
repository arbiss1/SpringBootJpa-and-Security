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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

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
    public String viewHomePageUser(Model model, User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> username1 = repo.findByUsername(username);
        if (username1.isPresent()) {
            System.out.println(username1);
            List<Orders> listOrders = service.listAllByUser(username1.get().getUserId());
            model.addAttribute("listOrders", listOrders);
            model.addAttribute("userDetails", username1);
        }

        return "index";
    }

    @RequestMapping("/admin")
    public String viewHomePageAdmin(Model model) {
        List<Orders> listOrders = service.listAll();
        model.addAttribute("listOrders", listOrders);

        return "index";
    }

    @RequestMapping("/login")
    public String showUserSignin(Model model, @Valid User user, BindingResult result) {
        return "signinUser";
    }

    @RequestMapping("/procces-login")
    public String showUserPage(Model model, @Valid User user, BindingResult result) {
        if (!userService.isUsernamePresent(user)) {

            System.out.println(user.getUsername());
            String message = "Username doesn't exist !";
            model.addAttribute("noUsernameExists", message);
            return showUserSignin(model, user, result);
        } else if (user.getRoles().equals("USER")) {
            return "redirect:/user";
        } else {
            return "redirect:/admin";
        }
    }

    @RequestMapping("/new")
    public String showNewProductPage(Model model, OrderList choseOrder, net.codejava.Domains.OrderCategory choseCategory) {
        List<Orders> listOrders = service.listAll();
        List<net.codejava.Domains.OrderCategory> listCategory = categoryService.listAll();
        List<OrderList> orderList = orderlistService.listAllorders();
        System.out.println(choseCategory.getCategory());
        Orders order = new Orders();
        model.addAttribute("choseCategory", choseCategory);
        model.addAttribute("listCategory", listCategory);
        model.addAttribute("listOrders", listOrders);
        model.addAttribute("order", order);
        model.addAttribute("orderList", orderList);
        model.addAttribute("choseOrder", choseOrder);
        return "new_product";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model, User user2, BindingResult result) {
        if (result.hasErrors()) {
            return "signup_form";
        } else {
            System.out.println(result);
            User user = new User();
            model.addAttribute("user", user);
            return "signup_form";
        }
    }

    @PostMapping("/process_register")
    public String processRegister(@Valid User user, Model model, BindingResult result) {
        if (result.hasErrors()) {
            for (Object object : result.getAllErrors()) {
                if (object instanceof FieldError) {
                    FieldError fieldError = (FieldError) object;

                    System.out.println(fieldError.getCode());
                }

                if (object instanceof ObjectError) {
                    ObjectError objectError = (ObjectError) object;

                    System.out.println(objectError.getCode());
                }
            }
            return "signup_form";
        }
        if (userService.isUsernamePresent(user)) {
            String message = String.format("Username already exists !");
            model.addAttribute("nonUniqueUsername", message);
            return showRegistrationForm(model, user, result);
        } else {
            String firstnameUppercase = user.getfirst_name().substring(0, 1).toUpperCase(Locale.ROOT)
                    + user.getfirst_name().substring(1).toLowerCase();
            user.setFirst_name(firstnameUppercase);
            String lastnameUppercase = user.getLastName().substring(0, 1).toUpperCase(Locale.ROOT)
                    + user.getLastName().substring(1).toLowerCase();
            user.setLastName(lastnameUppercase);
            repo.save(user);
        }
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
    public String processRegisterSave(Model model, Orders order, OrderList orderList) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("here");
        String username = authentication.getName();
        System.out.println(username);
        List<OrderList> listAllProdcts = orderlistService.listAllorders();
        Optional<User> user = repo.findByUsername(username);
        ;
        String orderName = orderList.getListName();
        order.setUser_address(user.get().getUser_address());
        order.setUser_number(user.get().getUser_number());
        String lastName = user.get().getLastName();
        order.setCustomer(user.get().getFirst_name() + " " + lastName);
        order.setUserId(user.get().getUserId());
        int quantity = order.getQuantity();
        Optional<OrderList> orderListDb = listRepo.findByListName(orderList.getListName());
        net.codejava.Domains.OrderCategory category = orderlistService.get(orderList.getListName()).getCategory();
        String priceWithout$sign = orderListDb.get().getPrice().substring(0, orderListDb.get().getPrice().length() - 1);
        System.out.println(priceWithout$sign);
        double sumofPrice = (Double.valueOf(priceWithout$sign) * quantity);
        System.out.println(sumofPrice);
        order.setTotalPrice(Integer.toString((int) sumofPrice) + "$");
        order.setPrice(orderListDb.get().getPrice());
        order.setListName(orderList.getListName());
        repoOrders.save(order);
        if (user.get().getRoles().equals("USER")) {
            return "redirect:/user";
        } else {
            return "redirect:/admin";
        }
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
    public String editOrder(@PathVariable(name = "id") int id, Orders orderEdit, Model model) {
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
        String priceWithout$sign = order.getPrice().substring(0, order.getPrice().length() - 1);
        Integer quantity = order.getQuantity();
        System.out.println(quantity);
        double newSum = (Double.valueOf(priceWithout$sign) * quantity);
        order.setTotalPrice(Integer.toString((int) newSum) + "$");
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
        } else if (user.get().getRoles().equals("ADMIN")) {
            return "redirect:/admin";
        } else {
            return "redirect:/";
        }
    }

    @RequestMapping("/new_product_listItems")
    public String showProductForm(Model model, net.codejava.Domains.OrderCategory choseCategory) {
        OrderList orderList = new OrderList();
        List<net.codejava.Domains.OrderCategory> listCategory = categoryService.listAll();
        List<OrderList> listProducts = listRepo.findAll();
        model.addAttribute("listCategory", listCategory);
        System.out.println(listCategory);
        model.addAttribute("choseCategory", choseCategory);
        model.addAttribute("orderList", orderList);
        model.addAttribute("listProducts", listProducts);
        return "new_product_listItems";
    }

    @RequestMapping("/admin-panel")
    public String showAdminPanel(Model model) {
        List<User> users = userService.listAll();
        User user = new User();
        model.addAttribute("user", user);
        model.addAttribute("users", users);
        return "adminPanel";
    }

    @RequestMapping("/process_registerAdmin")
    public String processRegisterAmin(User user1, Model model, BindingResult result) {
        if (userService.isUsernamePresent(user1)) {
            String message = String.format("Username already exists !");
            model.addAttribute("nonUniqueUsername", message);
            return showRegistrationForm(model, user1, result);
        } else {
            String firstnameUppercase = user1.getfirst_name().substring(0, 1).toUpperCase(Locale.ROOT)
                    + user1.getfirst_name().substring(1).toLowerCase();
            user1.setFirst_name(firstnameUppercase);
            String lastnameUppercase = user1.getLastName().substring(0, 1).toUpperCase(Locale.ROOT)
                    + user1.getLastName().substring(1).toLowerCase();
            user1.setLastName(lastnameUppercase);
            user1.setRoles("ADMIN");
            user1.isEnabled();
            repo.save(user1);
        }
        return "redirect:/admin-panel";
    }

    @RequestMapping("/delete-user-admin/{userId}")
    public String deleteUser(@PathVariable(name = "userId") long userId) {
        userService.delete(userId);
        return "redirect:/admin-panel";
    }

    @RequestMapping("/process_register_product")
    public String processRegisterProduct(Model model, OrderList orderList, net.codejava.Domains.OrderCategory listCategory) {
        Optional<net.codejava.Domains.OrderCategory> categoryName = categoryRepo.findByCategory(listCategory.getCategory());
        System.out.println(categoryName);
        model.addAttribute("listCategory", listCategory);
        listRepo.save(orderList);
        return "redirect:/admin";
    }

    @RequestMapping("/registerCategory")
    public String saveCategory(Model model, net.codejava.Domains.OrderCategory choseCategory) {
        net.codejava.Domains.OrderCategory category = new net.codejava.Domains.OrderCategory();
        List<net.codejava.Domains.OrderCategory> listCategories = categoryService.listAll();
        model.addAttribute("choseCategory", choseCategory);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("category", category);
        return "new_category_listItems";
    }

    @RequestMapping("/process_register_category")
    public String processRegister(net.codejava.Domains.OrderCategory orderCategory, Model model) {
        if (categoryService.isPresent(orderCategory)) {
            String message = "Category already exists !";
            model.addAttribute("nonUniqueCategory", message);
            return saveCategory(model, new OrderCategory());
        } else {
            categoryRepo.save(orderCategory);
            return "redirect:/registerCategory";
        }
    }

    @RequestMapping("/delete-category/{categoryId}")
    public String deleteCategory(@PathVariable(name = "categoryId") long categoryId, OrderCategory orderCategory, Model model) {
        System.out.println(categoryId);
        Optional<OrderCategory> orderCategory1 = categoryRepo.findById(categoryId);
        List<OrderList> orderLists = listRepo.findByCategory(orderCategory1);
        orderLists.forEach(orderList -> {
            listRepo.delete(orderList);
        });
        categoryService.delete(categoryId);
        return "redirect:/registerCategory";
    }

    @RequestMapping("/delete-product/{listId}")
    public String deleteProduct(@PathVariable(name = "listId") long listId) {
        System.out.println(listId);
        orderlistService.delete(listId);
        return "redirect:/new_product_listItems";
    }

    @RequestMapping(value = "/user/edit/{userId}", method = RequestMethod.POST)
    public String editUser(@PathVariable(name = "userId") int userId, User userEdit, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        long id = repo.findByUsername(username).get().getUserId();
        User user = userService.get(id);
        Optional<User> user1 = repo.findByUsername(username);
        user.setUserId(repo.findByUsername(username).get().getUserId());
        user.setUser_address(userEdit.getUser_address());
        user.setUser_number(userEdit.getUser_number());
        user.setFirst_name(userEdit.getFirst_name());
        user.setLastName(userEdit.getLastName());
        System.out.println(user);
        repo.save(user);
        return "redirect:/user";
    }

    @RequestMapping("/user/edit/{userId}")
    public ModelAndView showEditUserPage(@PathVariable(name = "userId") int userId) {
        ModelAndView mav = new ModelAndView("userProfile");
        User user = userService.get(userId);
        mav.addObject("user", user);
        System.out.println(mav);
        repo.save(user);
        return mav;
    }

}
