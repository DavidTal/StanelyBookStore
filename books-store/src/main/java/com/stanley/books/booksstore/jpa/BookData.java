package com.stanley.books.booksstore.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity 
public class BookData {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer bookId;
	
	@Column(name="book_name")
	private String bookName;
	private String author;
	private int quantity; 
 
	public static BookData create(String name, String author, int quantity) {
		return new BookData(name, author, quantity); 
	}
	
	public BookData() {
		
	}
	
	public BookData(String name, String author, int quantity) {
		this.setBookName(name);
		this.setAuthor(author);
		this.setQuantity(quantity);
	}

	public boolean has(int quantity) {
		return this.quantity >= quantity; 
	}
	
	public String getAuthor() {
		return author;
	}

	@Override
	public String toString() {
		return "BookData [bookId=" + bookId + ", bookName=" + bookName + ", author=" + author + ", quantity=" + quantity
				+ "]";
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Integer getBookId() {
		return bookId;
	}

	public void setBookId(Integer bookId) {
		this.bookId = bookId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public void increase(int quantity) {
		this.quantity += quantity;
	}

	public void decrease(Integer quantity) {
		this.quantity -= quantity; 
	}
}