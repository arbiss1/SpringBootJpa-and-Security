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
	private String address;
	private int quantity;
	@Column(name = "nr_cel")
	private int nrCel;

	private Long userId;

	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn
	private OrderList order;

//	@ManyToOne(targetEntity = User.class , cascade = CascadeType.ALL)
//	@JoinColumn(name = "user_id")
//	private User user;
	//private int order_list;
	
//	@ManyToMany
//	@JoinTable(
//            name = "order_item",
//            joinColumns = { @JoinColumn(name = "id") },
//            inverseJoinColumns = { @JoinColumn(name = "list_Id") }
//    )
	
	
	protected Orders() {
	}
	
	public Orders(Long id, String customer, OrderList orderList, String address, int quantity, int nrCel) {
		super();
		this.id = id;
		Customer = customer;
		this.order = orderList;
		this.address = address;
		this.quantity = quantity;
		this.nrCel = nrCel;
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

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

//	public User getUser() {
//		return user;
//	}
//
//	public void setUser(User user) {
//		this.user = user;
//	}
//public int getOrder_list() {
		//return order_list;
	//}
	//public void setOrder_list(int order_list) {
		//this.order_list = order_list;
	//}

//	public List<OrderList> getItems() {
//		return items;
//	}
//
//	public void setItems(List<OrderList> items) {
//		this.items = items;
//	}

	public int getNrCel() {
		return nrCel;
	}

	public void setNrCel(int nrCel) {
		this.nrCel = nrCel;
	}

	@Override
	public String toString() {
		return "Orders [id=" + id + ", Customer=" + Customer + ", name=" + order + ", address=" + address + ", quantity="
				+ quantity + ", nrCel=" + nrCel + "]";
	}

	
	
	/*ublic List<OrderList> getOrderList() {
		return orderList;
	}
	public void setOrderList(List<OrderList> orderList) {
		this.orderList = orderList;
	}*/
	
	
	/*public OrderList getOrderList() {
		return orderList;
	}
	public void setOrderList(OrderList orderList) {
		this.orderList = orderList;
	}*/
}
