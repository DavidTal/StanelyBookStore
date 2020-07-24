package com.stanley.books.booksstore.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity 
public class OrderData {
	public enum OrderStatus{
		OPEN, CANCELED_NOT_FOUND, CANCEL_INSIFFCIENT_QUANTITY, SOLD; 
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer orderId;
	
	private int bookId; 
	private int quantity; 
	private OrderStatus status; 
	
	
	public static OrderData create(int quantity) {
		return new OrderData(quantity);
	}
	
	public OrderData() {
		
	}
	
	public OrderData(int quantity) {
		this.quantity = quantity;
		this.setStatus(OrderStatus.OPEN);
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "OrderData [orderId=" + orderId + ", bookId=" + bookId + ", quantity=" + quantity + ", status=" + status
				+ "]";
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}
}
