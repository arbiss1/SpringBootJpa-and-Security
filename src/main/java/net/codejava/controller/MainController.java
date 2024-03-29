package net.codejava.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import net.codejava.repository.*;
import net.codejava.services.*;
import net.codejava.entities.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import javax.validation.*;


@Controller
public class MainController {

    private final OrderService service;
    private final UserRepository repo;
    private final UserService userService;
    private final OrderListService orderlistService;
    private final OrdersRepository repoOrders;
    private final OrderListRepository listRepo;
    private final OrderCategoryService categoryService;
    private final OrderCategoryRepository categoryRepo;
    private final ProductRequestService productService;
    private final ProductRequestsRepository productRepo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public MainController(OrderService service, UserRepository repo,
                          UserService userService, OrderListService orderlistService,
                          OrdersRepository repoOrders, OrderListRepository listRepo,
                          OrderCategoryService categoryService, OrderCategoryRepository categoryRepo,
                          ProductRequestService productService, ProductRequestsRepository productRepo,
                          BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.service = service;
        this.repo = repo;
        this.userService = userService;
        this.orderlistService = orderlistService;
        this.repoOrders = repoOrders;
        this.listRepo = listRepo;
        this.categoryService = categoryService;
        this.categoryRepo = categoryRepo;
        this.productService = productService;
        this.productRepo = productRepo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    private static final String UPLOADED_FOLDER = "C://Users//arbis//Desktop//OrderManager//src//main//resources//images//";


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
        String baseUrl = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png";
        String url = "images/" + filename;
        if(filename.equals("") || filename == null){
            model.addAttribute("profileImage",
                    baseUrl);
            return baseUrl;
        }else {
            model.addAttribute("profileImage", url);
            return url;
        }
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
    public String showUserPage(Model model,User user, BindingResult result) {
        if (!userService.isUserValid(user)) {
            String message = "Username or password is incorrect !";
            model.addAttribute("noUsernameExists", message);
            return "signinUser";

        }

        return user.getRoles().equals("USER")
                ? "redirect:/user"
                : "redirect:/admin";
    }

    @RequestMapping(value = "/get-products/{category}",method = RequestMethod.GET)
    public ModelAndView getProducts(@PathVariable(name = "category") OrderCategory category, Model model){
        ModelAndView mav = new ModelAndView("new_product");
        System.out.println(category);
        List<OrderList> allProducts = listRepo.findBycategory(category);
        model.addAttribute("products" , allProducts);
        System.out.println(allProducts);
        System.out.println(mav.getViewName());
        return mav;
    }

    @RequestMapping("/new")
    public String showNewProductPage(Model model, OrderList choseOrder, net.codejava.entities.OrderCategory choseCategory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        List<Orders> listOrders = service.listAll();
        List<net.codejava.entities.OrderCategory> listCategory = categoryService.listAll();
        List<OrderList> orderList = orderlistService.listAllorders();
        System.out.println(choseCategory.getCategory());
        Orders order = new Orders();
        model.addAttribute("username", getUsername(model));
        model.addAttribute("adminUsername", getUsername(model));
        model.addAttribute("choseCategory", choseCategory);
        model.addAttribute("profileImage", getProfileImage(model));
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
    public String processRegister(@ModelAttribute(name = "user") @Valid User user,BindingResult result,
                                  @RequestParam("file") MultipartFile file, Model model) {
            if (result.hasErrors())
                return "signup_form";

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
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            user.setFileName(file.getOriginalFilename());
            System.out.println(file.getOriginalFilename());
            String firstnameUppercase = user.getFirst_name().substring(0, 1).toUpperCase(Locale.ROOT)
                    + user.getFirst_name().substring(1).toLowerCase();
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
        model.addAttribute("profileImage", getProfileImage(model));
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
        order.setUser_address(user.get().getUserAddress());
        order.setUser_number(user.get().getUserNumber());
        String lastName = user.get().getLastName();
        order.setCustomer(user.get().getFirst_name() + " " + lastName);
        order.setUserId(user.get().getUserId());
        //time and date of order
        LocalDate toadyDate = LocalDate.now();
        String formattedDate = toadyDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL));
        System.out.println("FULL format: " + formattedDate);
        LocalTime todayTime = LocalTime.now();
        order.setLocalDate(formattedDate);
        order.setLocalTime(todayTime);
        //
        int quantity = order.getQuantity();
        order.setStatus("Order was sent successfully");
        Optional<OrderList> orderListDb = listRepo.findByListName(orderList.getListName());
        net.codejava.entities.OrderCategory category = orderlistService.get(orderList.getListName()).getCategory();
        String priceWithout$sign = orderListDb.get().getPrice().substring(0, orderListDb.get().getPrice().length() - 1);
        System.out.println(priceWithout$sign);
        double sumofPrice = (Double.valueOf(priceWithout$sign) * quantity);
        System.out.println(sumofPrice);
        order.setTotalPrice(Integer.toString((int) sumofPrice) + "$");
        order.setPrice(orderListDb.get().getPrice());
        order.setListName(orderList.getListName());
        model.addAttribute("username", getUsername(model));
        model.addAttribute("adminUsername", getUsername(model));
        model.addAttribute("profileImage", getProfileImage(model));
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
        model.addAttribute("profileImage", getProfileImage(model));
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
        System.out.println(mav.getViewName());
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
    public String showProductForm(Model model, net.codejava.entities.OrderCategory choseCategory) {
        OrderList orderList = new OrderList();
        List<net.codejava.entities.OrderCategory> listCategory = categoryService.listAll();
        List<OrderList> listProducts = listRepo.findAll();
        model.addAttribute("listCategory", listCategory);
        System.out.println(listCategory);
        model.addAttribute("username", getUsername(model));
        model.addAttribute("adminUsername", getUsername(model));
        model.addAttribute("profileImage", getProfileImage(model));
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
        model.addAttribute("profileImage", getProfileImage(model));
        model.addAttribute("user", user);
        return "adminPanel";
    }

    @RequestMapping("/all-users")
    public String showallUsers(Model model) {
        List<User> users = userService.listAll();
        model.addAttribute("adminUsername", getUsername(model));
        model.addAttribute("profileImage", getProfileImage(model));
        model.addAttribute("users", users);

        return "allUsers";
    }

    @RequestMapping("/all-requested-products")
    public String showallProducts(Model model) {
        List<ProductRequests> productRequests = productService.listAllProductRequested();
        model.addAttribute("adminUsername", getUsername(model));
        model.addAttribute("profileImage", getProfileImage(model));
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
            String firstnameUppercase = user1.getFirst_name().substring(0, 1).toUpperCase(Locale.ROOT)
                    + user1.getFirst_name().substring(1).toLowerCase();
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
        return "redirect:/all-users";
    }

    @RequestMapping("/process_register_product")
    public String processRegisterProduct(Model model, OrderList orderList, net.codejava.entities.OrderCategory listCategory) {
        Optional<net.codejava.entities.OrderCategory> categoryName = categoryRepo.findByCategory(listCategory.getCategory());
        System.out.println(categoryName);
        model.addAttribute("listCategory", listCategory);
        listRepo.save(orderList);
        return "redirect:/admin";
    }

    @RequestMapping("/registerCategory")
    public String saveCategory(Model model, net.codejava.entities.OrderCategory choseCategory) {
        net.codejava.entities.OrderCategory category = new net.codejava.entities.OrderCategory();
        List<net.codejava.entities.OrderCategory> listCategories = categoryService.listAll();
        model.addAttribute("username", getUsername(model));
        model.addAttribute("adminUsername", getUsername(model));
        model.addAttribute("profileImage", getProfileImage(model));
        model.addAttribute("choseCategory", choseCategory);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("category", category);
        return "new_category_listItems";
    }

    @RequestMapping("/process_register_category")
    public String processRegister(net.codejava.entities.OrderCategory orderCategory, Model model) {
        if (categoryService.isPresent(orderCategory)) {
            String message = "Category already exists !";
            model.addAttribute("nonUniqueCategory", message);
            model.addAttribute("username", getUsername(model));
            model.addAttribute("adminUsername", getUsername(model));
            model.addAttribute("profileImage", getProfileImage(model));
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
        model.addAttribute("profileImage", getProfileImage(model));
        model.addAttribute("productRequests", productRequests);
        model.addAttribute("listProductsRequested", productRequests1);
        return "requestNewProducts";
    }
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
    public String deleteRequestedProduct(@PathVariable(name = "productId") long productId) {
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
        String address = user.get().getUserAddress();
        String phonenumber = user.get().getUserNumber();
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
        userEdit.setPassword(user.getPassword());
        userEdit.setMatchingPassword(user.getMatchingPassword());
        user.setUserAddress(userEdit.getUserAddress());
        user.setFirst_name(userEdit.getFirst_name());
        user.setUserNumber(userEdit.getUserNumber());
        repo.save(user);
        model.addAttribute("username", getUsername(model));
        model.addAttribute("adminUsername", getUsername(model));
        model.addAttribute("profileImage", getProfileImage(model));
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
        user.setPassword(userEdit.getPassword());
        user.setMatchingPassword(userEdit.getMatchingPassword());
        repo.save(user);
        model.addAttribute("username", getUsername(model));
        model.addAttribute("adminUsername", getUsername(model));
        model.addAttribute("profileImage", getProfileImage(model));
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

    @RequestMapping("/order-status-user")
    public String showStatusPage(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = repo.findByUsername(username);
        System.out.println(username);
        List<Orders> listAllOrders = service.listAll();
        List<Orders> listOrders = service.listAllByUser(user.get().getUserId());
        model.addAttribute("listOrders", listOrders);
        model.addAttribute("listAll",listAllOrders);
        model.addAttribute("userDetails", user);
        model.addAttribute("username", getUsername(model));
        model.addAttribute("adminUsername", getUsername(model));
        model.addAttribute("profileImage", getProfileImage(model));
        return "statusPageUser";
    }
    @RequestMapping("/order-status-admin")
    public String showStatusAdminPage(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = repo.findByUsername(username);
        System.out.println(username);
        List<Orders> listAllOrders = service.listAll();
        model.addAttribute("listAll",listAllOrders);
        model.addAttribute("userDetails", user);
        model.addAttribute("username", getUsername(model));
        model.addAttribute("adminUsername", getUsername(model));
        model.addAttribute("profileImage", getProfileImage(model));
        return "statusPageAdmin";
    }

    @RequestMapping("/status-update/{id}/{value}/{status}")
    public String statusUpdate(@PathVariable(name = "id") long id ,
                               @PathVariable(name = "status") String status,
                               @PathVariable(name = "value") String value){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user2 = repo.findByUsername(username);
        Orders allOrdersByListname = repoOrders.findByidAndOrderStatus(id,status);
        System.out.println(id);
        System.out.println(allOrdersByListname);
        System.out.println(value);
        allOrdersByListname.setStatus(value);
        System.out.println(allOrdersByListname);
        repoOrders.save(allOrdersByListname);
        return "redirect:/order-status-admin";
    }

    @RequestMapping("/purchase-history")
    public String purchaseHistory (Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = repo.findByUsername(username);
        long id = user.get().getUserId();
        String status = "Arrived";
        List<Orders> orders = repoOrders.findByUserIdAndOrderStatus(id , status);
        List<Orders> allOrders = repoOrders.findByOrderStatus(status);
        System.out.println(orders);
        model.addAttribute("listAllorders" ,allOrders);
        model.addAttribute("allOrders", orders);
        model.addAttribute("username", getUsername(model));
        model.addAttribute("adminUsername", getUsername(model));
        model.addAttribute("profileImage", getProfileImage(model));
        return "purchasePage";
    }

}
