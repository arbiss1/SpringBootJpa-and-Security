package net.codejava.Domains;

import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "orderlist")
public class OrderList {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int listId;
	@Column(name="list_Name")
	private String listName;


	public OrderList() {
		
	}
	public OrderList(int listId, String listName) {
		super();
		this.listId = listId;
		this.listName = listName;
	}

	public int getListId() {
		return listId;
	}
	public void setListId(int listId) {
		this.listId = listId;
	}
	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}
	
//	
//	public List<Orders> getOrders() {
//		return orders;
//	}
//	public void setOrders(List<Orders> orders) {
//		this.orders = orders;
//	}
	//public Orders getOrder() {
	//	return order;
	//}
	//public void setOrder(Orders order) {
	//	this.order = order;
	//}
	/*public Orders getOrder() {
		return order;
	}
	public void setOrder(Orders order) {
		this.order = order;
	}*/
	@Override
	public String toString() {
		return "OrderList [listId=" + listId + ", listName=" + listName + ", order="  + "]";
	}
	/*public Orders getOrder() {
		return order;
	}
	public void setOrder(Orders order) {
		this.order = order;
	}*/
	
}

