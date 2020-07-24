package com.stanley.books.booksstore;

import java.text.MessageFormat;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.stanley.books.booksstore.jpa.BookData;
import com.stanley.books.booksstore.jpa.BooksRepository;
import com.stanley.books.booksstore.jpa.OrderData;
import com.stanley.books.booksstore.jpa.OrderData.OrderStatus;
import com.stanley.books.booksstore.jpa.OrderDataRepository;


@RestController
@RequestMapping(path = "/book")
public class MainController {
	@Autowired
	private BooksRepository bookRepository;
	
	@Autowired
	private OrderDataRepository orderDataRepository;
	
	
	@Transactional
	@PostMapping(path = "/add")
	public @ResponseBody String addNewUser(@RequestParam String bookName, @RequestParam String author,
			@RequestParam int quantity) {

		String inputMessage = MessageFormat.format("In Add, bookName[{0}], author[{1}], quantity[{2}]", bookName, author, quantity);
		System.out.println(inputMessage);
		
		Optional<BookData> bookOptional = this.bookRepository.findByBookName(bookName);
		BookData book; 
		
		if (bookOptional.isEmpty()) {
			book = BookData.create(bookName, author, quantity);
			System.out.println("Book does not exist. Inserting");
		}else {
			book = bookOptional.get(); 
			System.out.println(MessageFormat.format("Book[{0}] already exist, updating with quantity[{1}]", book, quantity)); 
			book.increase(quantity);
		}
		
		bookRepository.save(book);
		
		//Of course in real life it is a bad idea to send internal information to the client
		return "Saved Book: " + book; 
	}

	@Transactional
	@GetMapping(path = "/buy")
	public @ResponseBody Integer buyBook(@RequestParam String bookName, @RequestParam Integer quantity) {
		System.out.println(MessageFormat.format("Trying to buy bookName[{0}], quantity[{1}]", bookName, quantity));
		
		// First create the order. Then update it
		OrderData orderData = OrderData.create(quantity); 

		Optional<BookData> bookOptional = this.bookRepository.findByBookName(bookName); 
		System.out.println("After findByBookName: " + bookOptional);

		if (bookOptional.isEmpty()) { 
			System.err.println(MessageFormat.format("Order for bookName[{0}], quantity[{1}] - failed, book does not exist", bookName, quantity));
			return updateOrder(orderData, OrderStatus.CANCELED_NOT_FOUND); 
		}
		
		BookData book = bookOptional.get();
		if (!book.has(quantity)) {
			System.out.println(MessageFormat.format("Order for bookName[{0}], quantity[{1}] - failed existing quantity[{2}] is less then required", bookName, quantity, book.getQuantity()));
			return updateOrder(orderData, OrderStatus.CANCEL_INSIFFCIENT_QUANTITY); 
		}
		 
		System.out.println("After book fetch - before sleep");
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			System.err.println(e); 
			e.printStackTrace();
		}

		System.out.println("After book fetch - after sleep");
		
		System.out.println(MessageFormat.format("Buying book[{0}], quantity[{1}]", bookOptional, quantity));
		book.decrease(quantity);
		bookRepository.save(book);
		return updateOrder(orderData, OrderStatus.SOLD); 
	}

	private Integer updateOrder(OrderData orderData, OrderStatus status) {
		orderData.setStatus(status);
		OrderData orderDataResult = orderDataRepository.save(orderData);
		return orderDataResult.getOrderId();
	}
	
	@GetMapping(path = "/inventory")
	public @ResponseBody Iterable<BookData> getAllBooksData() { 
		System.out.println("in get all books");
		return bookRepository.findAll();
	}
	
	@GetMapping(path = "/order")
	public @ResponseBody OrderStatus getOrderData(@RequestParam Integer orderId) {
		System.out.println("in get order data");
		Optional<OrderData> orderData = this.orderDataRepository.findById(orderId); 
		if (orderData.isEmpty()) {
			System.err.println("Failed to find order data for orderId: " + orderId);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to find order data for orderId: " + orderId); 
		}
		
		System.out.println("Found order-data: " + orderData.get());
		return orderData.get().getStatus(); 
	}
}