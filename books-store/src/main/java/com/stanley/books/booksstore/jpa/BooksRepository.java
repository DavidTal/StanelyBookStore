package com.stanley.books.booksstore.jpa;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

public interface BooksRepository extends CrudRepository<BookData, Integer> {
	//@Override
	//@Lock(LockModeType.PESSIMISTIC_READ)
	//Iterable<BookData> findAll(); 
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<BookData> findByBookName(String bookName); 
}	