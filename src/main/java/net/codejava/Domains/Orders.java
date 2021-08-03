package net.codejava.Domains;


import javax.persistence.*;

@Entity
@Table(name = "orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String Customer;
    private int quantity;
    private String user_address;
    private String user_number;
    private String totalPrice;
    private Long userId;
    private String listName;
    private String price;

    @ManyToOne
    @JoinColumn
    private OrderCategory category;

    protected Orders() {
    }

    public Orders(Long id, String customer, OrderList orderList, String user_address, int quantity, String user_number) {
        super();
        this.id = id;
        this.Customer = customer;
        this.user_address = user_address;
        this.quantity = quantity;
        this.user_number = user_number;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public OrderCategory getCategory() {
        return category;
    }

    public void setCategory(OrderCategory category) {
        this.category = category;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCustomer() {
        return Customer;
    }

    public void setCustomer(String customer) {
        Customer = customer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUser_address() {
        return user_address;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public String getUser_number() {
        return user_number;
    }

    public void setUser_number(String user_number) {
        this.user_number = user_number;
    }

    @Override
    public String toString() {
        return "Orders [id=" + id + ", Customer=" + Customer + ", name=" + ", address=" + user_address + ", quantity="
                + quantity + ", nrCel=" + user_number + "]";
    }
}
