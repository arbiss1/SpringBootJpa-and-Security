package net.codejava.Domains;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.google.gson.Gson;
import net.codejava.Repositories.*;
import net.codejava.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
    @Autowired
    private ProductRequestService productService;
    @Autowired
    private ProductRequestsRepository productRepo;


    private static String UPLOADED_FOLDER = "C://Users//arbis//Desktop//OrderManager//src//main//resources//images//";


    public Object getUsername(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = repo.findByUsername(username);
        model.addAttribute("username", username);
        return username;
    }
    public Object getProfileImage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = repo.findByUsername(username);
        String filename = user.get().getFileName();
        String url = "images/" + filename;
        if(filename == null){
            model.addAttribute("profileImage",
                    "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png");
        }else {
            model.addAttribute("profileImage", url);
        }
        return url;
    }


    @RequestMapping("/user")
    public String viewHomePageUser(Model model) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = repo.findByUsername(username);
        if (!user.isPresent()) {
            return "/user";
        }
        System.out.println(username);
        List<Orders> listOrders = service.listAllByUser(user.get().getUserId());
        model.addAttribute("listOrders", listOrders);
        model.addAttribute("userDetails", user);
        model.addAttribute("username", getUsername(model));
        model.addAttribute("profileImage", getProfileImage(model));

        return "index";
    }

    @RequestMapping("/admin")
    public String viewHomePageAdmin(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = repo.findByUsername(username);
        List<Orders> listOrders = service.listAll();
        model.addAttribute("listOrders", listOrders);
        model.addAttribute("adminUsername", getUsername(model));
        model.addAttribute("profileImage", getProfileImage(model));

        return "index";
    }

    @RequestMapping("/login")
    public String showUserSignin() {
        return "signinUser";

    }

    @RequestMapping("/procces-login")
    public String showUserPage(Model model, @Valid User user, BindingResult result) {
        if (!userService.isUserValid(user)) {
            String message = "Username or password is incorrect !";
            model.addAttribute("noUsernameExists", message);
            return "signinUser";
        }

        return user.getRoles().equals("USER")
                ? "redirect:/user"
                : "redirect:/admin";
    }

    @RequestMapping("/new")
    public String showNewProductPage(Model model, OrderList choseOrder, net.codejava.Domains.OrderCategory choseCategory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        List<Orders> listOrders = service.listAll();
        List<net.codejava.Domains.OrderCategory> listCategory = categoryService.listAll();
        List<OrderList> orderList = orderlistService.listAllorders();
        System.out.println(choseCategory.getCategory());
        Orders order = new Orders();
        model.addAttribute("username", getUsername(model));
        model.addAttribute("adminUsername", getUsername(model));
        model.addAttribute("choseCategory", choseCategory);
        model.addAttribute("listCategory", listCategory);
        model.addAttribute("listOrders", listOrders);
        model.addAttribute("order", order);
        model.addAttribute("orderList", orderList);
        model.addAttribute("choseOrder", choseOrder);
        return "new_product";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "signup_form";
    }

    @PostMapping("/process_register")
    public String processRegister(User user, @RequestParam("file") MultipartFile file, Model model, BindingResult result) {
        if (result.hasErrors()) {
            System.out.println(result);
        }
        if (userService.isUsernamePresent(user)) {
            String message = "Username already exists !";
            model.addAttribute("nonUniqueUsername", message);
            return "signup_form";
        } else {
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
                Files.write(path, bytes);
                model.addAttribute("Emessage",
                        "You successfully uploaded '" + file.getOriginalFilename() + "'");
            } catch (IOException e) {
                e.printStackTrace();
            }
            user.setFileName(file.getOriginalFilename());
            System.out.println(file.getOriginalFilename());
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
        model.addAttribute("username", getUsername(model));
        model.addAttribute("adminUsername", getUsername(model));
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
        model.addAttribute("username", getUsername(model));
        model.addAttribute("adminUsername", getUsername(model));
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
        model.addAttribute("username", getUsername(model));
        model.addAttribute("adminUsername", getUsername(model));
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
        model.addAttribute("username", getUsername(model));
        model.addAttribute("adminUsername", getUsername(model));
        model.addAttribute("choseCategory", choseCategory);
        model.addAttribute("orderList", orderList);
        model.addAttribute("listProducts", listProducts);
        return "new_product_listItems";
    }

    @RequestMapping("/admin-panel")
    public String showAdminPanel(Model model) {
        User user = new User();
        model.addAttribute("username", getUsername(model));
        model.addAttribute("adminUsername", getUsername(model));
        model.addAttribute("user", user);
        return "adminPanel";
    }

    @RequestMapping("/all-users")
    public String showallUsers(Model model) {
        List<User> users = userService.listAll();
        model.addAttribute("adminUsername", getUsername(model));
        model.addAttribute("users", users);

        return "allUsers";
    }

    @RequestMapping("/all-requested-products")
    public String showallProducts(Model model) {
        List<ProductRequests> productRequests = productService.listAllProductRequested();
        model.addAttribute("adminUsername", getUsername(model));
        model.addAttribute("productRequests", productRequests);
        return "requestedProducts";
    }

    @RequestMapping("/process_registerAdmin")
    public String processRegisterAmin(User user1, Model model, BindingResult result) {
        if (userService.isUsernamePresent(user1)) {
            String message = String.format("Username already exists !");
            model.addAttribute("nonUniqueUsername", message);
            return "adminPanel";
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
        model.addAttribute("username", getUsername(model));
        model.addAttribute("adminUsername", getUsername(model));
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
            model.addAttribute("username", getUsername(model));
            model.addAttribute("adminUsername", getUsername(model));
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


    @RequestMapping("/request-product")
    public String showRequestProduct(Model model, ProductRequests requests) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = repo.findByUsername(username);
        System.out.println(username);
        long id = user.get().getUserId();
        List<ProductRequests> productRequests1 = productService.listAllByProduct(id);
        System.out.println(productRequests1);
        System.out.println(productRepo.findByuserRequestedId(user.get().getUserId()));
        ProductRequests productRequests = new ProductRequests();
        model.addAttribute("username", getUsername(model));
        model.addAttribute("adminUsername", getUsername(model));
        model.addAttribute("productRequests", productRequests);
        model.addAttribute("listProductsRequested", productRequests1);
        return "requestNewProducts";
    }

//    @RequestMapping("/user")
//    public String viewHomePageUser(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//        Optional<User> user = repo.findByUsername(username);
//        if (user.isPresent()) {
//            List<Orders> listOrders = service.listAllByUser(user.get().getUserId());
//            model.addAttribute("listOrders", listOrders);
//            model.addAttribute("userDetails", user);
//        }

    @RequestMapping("/process-product-register")
    public String processRequestProduct(Model model, ProductRequests productRequests) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = repo.findByUsername(username);
        long id = user.get().getUserId();
        List<ProductRequests> productRequests1 = productService.listAllByProduct(id);
        model.addAttribute("listProductsRequested", productRequests1);
        if (productService.isRequestedProductPresent(productRequests)) {
            String messageError = "Product already exists !";
            model.addAttribute("messageError", messageError);
            return "requestNewProducts";
        } else {
            productRequests.setRequestedBy(user.get().getFirst_name() + " " + user.get().getLastName());
            productRequests.setUserRequestedId(user.get().getUserId());
            productRequests.setStatus("...");
            productRepo.save(productRequests);
            return "redirect:/request-product";
        }
    }

    @RequestMapping("/delete-requested-product/{productId}")
    public String deleteRequestedProduct(@PathVariable(name = "productId") long productId, ProductRequests productRequests, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = repo.findByUsername(username);
        productService.delete(productId);
        return user.get().getRoles().equals("USER")
                ? "redirect:/request-product"
                : "redirect:/all-requested-products";
    }

    @RequestMapping("/product-approved/{product}")
    public String approvedProduct(@PathVariable(name = "product") String product, ProductRequests productRequests) {
        Optional<ProductRequests> productName = productRepo.findByProduct(product);
        String statusM = "Approved";
        productRequests.setStatus(statusM);
        productRequests.setProductId(productName.get().getProductId());
        productRequests.setUserRequestedId(productName.get().getUserRequestedId());
        productRequests.setRequestedBy(productName.get().getRequestedBy());
        productRequests.setProduct(productName.get().getProduct());
        System.out.println(productRequests);
        productRepo.save(productRequests);
        return "redirect:/all-requested-products";
    }


    //User profile implementation
    @RequestMapping("/user-profile")
    public String userProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = repo.findByUsername(username);
        String lastName = user.get().getLastName();
        String usernam1 = user.get().getUsername();
        String firstName = user.get().getFirst_name();
        String address = user.get().getUser_address();
        String phonenumber = user.get().getUser_number();
        Long userId = user.get().getUserId();
        model.addAttribute("username", getUsername(model));
        model.addAttribute("adminUsername", getUsername(model));
        model.addAttribute("userDetails", user);
        model.addAttribute("profileImage", getProfileImage(model));
        System.out.println(lastName);
        return "userProfile";
    }

    @RequestMapping(value = "/user-edit/{userId}", method = RequestMethod.POST)
    public String editProfile(@PathVariable(name = "userId") int userId, User userEdit, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.get(userId);
        Optional<User> user1 = repo.findByUsername(username);
        user.setUserId(user1.get().getUserId());
        user.setLastName(userEdit.getLastName());
        user.setPassword(userEdit.getPassword());
        user.setMatchingPassword(userEdit.getMatchingPassword());
        user.setUser_address(userEdit.getUser_address());
        user.setFirst_name(userEdit.getFirst_name());
        user.setUser_number(userEdit.getUser_number());
        repo.save(user);
        model.addAttribute("username", getUsername(model));
        model.addAttribute("adminUsername", getUsername(model));
        model.addAttribute("userDetails", user);
        if (user.getRoles().equals("USER")) {
            return "redirect:/user";
        } else {
            return "redirect:/admin";
        }
    }

    @RequestMapping(value = "/user-edit/{userId}", method = RequestMethod.GET)
    public ModelAndView showEditProfilePage(@PathVariable(name = "userId") int userId, Model model) {
        ModelAndView mav = new ModelAndView("userProfileEdit");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.get(userId);
        model.addAttribute("username", getUsername(model));
        model.addAttribute("adminUsername", getUsername(model));
        mav.addObject("userDetails", user);
        model.addAttribute("profileImage", getProfileImage(model));
        System.out.println(mav);
        repo.save(user);
        return mav;
    }

    @RequestMapping(value = "/user-edit-password/{userId}", method = RequestMethod.POST)
    public String editPassword(@PathVariable(name = "userId") int userId, User userEdit, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.get(userId);
        Optional<User> user1 = repo.findByUsername(username);
        user.setPassword(userEdit.getPassword());
        user.setMatchingPassword(userEdit.getMatchingPassword());
        repo.save(user);
        model.addAttribute("username", getUsername(model));
        model.addAttribute("adminUsername", getUsername(model));
        model.addAttribute("user", user);
        if (user.getRoles().equals("USER")) {
            return "redirect:/user";
        } else {
            return "redirect:/admin";
        }
    }

    @RequestMapping(value = "/user-edit-password/{userId}", method = RequestMethod.GET)
    public ModelAndView showEditPasswordPage(@PathVariable(name = "userId") int userId, Model model) {
        ModelAndView mav = new ModelAndView("changePassword");
        User user = userService.get(userId);
        model.addAttribute("username", getUsername(model));
        model.addAttribute("adminUsername", getUsername(model));
        model.addAttribute("profileImage", getProfileImage(model));
        mav.addObject("user", user);
        System.out.println(mav);
        repo.save(user);
        return mav;
    }

//
//    @ResponseBody
//    @RequestMapping(value = "loadProductByCategory/{id}", method = RequestMethod.GET)
//    public String loadProductByCategory(@PathVariable("id") int id) {
//        Gson gson = new Gson();
//        return gson.toJson(orderlistService.findByOrderCategory(id));
//    }


}
