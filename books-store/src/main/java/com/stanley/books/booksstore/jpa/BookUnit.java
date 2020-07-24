package com.stanley.books.booksstore.jpa;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

//TODO: think I wont use this
//@Entity 
public class BookUnit {
	private Integer bookId;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer unitId; 
	
	public BookUnit() {
		
	}
	public Integer getBookId() {
		return bookId;
	}

	public void setBookId(Integer bookId) {
		this.bookId = bookId;
	}

	public Integer getUnitId() {
		return unitId;
	}

	public void setUnitId(Integer unitId) {
		this.unitId = unitId;
	}

	@Override
	public String toString() {
		return "BookUnit [bookId=" + bookId + ", unitId=" + unitId + "]";
	}
}
