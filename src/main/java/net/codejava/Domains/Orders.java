package net.codejava.Domains;


import java.util.List;

import javax.persistence.*;

@Entity
@Table(name="orders")
public class Orders {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String Customer;
	private int quantity;
    private String user_address;
    private String user_number;
    private String price;
	private Long userId;

	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn
	private OrderList order;

	protected Orders() {
	}
	
	public Orders(Long id, String customer, OrderList orderList, String user_address, int quantity, String user_number) {
		super();
		this.id = id;
		Customer = customer;
		this.order = orderList;
		this.user_address = user_address;
		this.quantity = quantity;
		this.user_number = user_number;
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
	public OrderList getOrderList() {
		return order;
	}
	public void setOrderList(OrderList orderList) {
		this.order = orderList;
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

	public OrderList getOrder() {
		return order;
	}

	public void setOrder(OrderList order) {
		this.order = order;
	}

	@Override
	public String toString() {
		return "Orders [id=" + id + ", Customer=" + Customer + ", name=" + order + ", address=" + user_address + ", quantity="
				+ quantity + ", nrCel=" + user_number + "]";
	}
}
