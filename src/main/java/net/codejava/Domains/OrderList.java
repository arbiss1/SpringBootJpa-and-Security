package net.codejava.Domains;

import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "orderlist")
public class OrderList {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long listId ;
	@Column(name="list_Name")
	private String listName;
	@ManyToOne
	@JoinColumn
	private OrderCategory category;

	private String price;
	public OrderList() {
	}
	public OrderList(int listId, String listName) {
		super();
		this.listId = listId;
		this.listName = listName;
	}

	public OrderCategory getCategory() {
		return category;
	}

	public void setCategory(OrderCategory category) {
		this.category = category;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public long getListId() {
		return listId;
	}
	public void setListId(long listId) {
		this.listId = listId;
	}
	public String getListName() {
		return listName;
	}
	public void setListName(String listName) {
		this.listName = listName;
	}

	@Override
	public String toString() {
		return "OrderList [listId=" + listId + ", listName=" + listName + ", order="  + "]";
	}
}

