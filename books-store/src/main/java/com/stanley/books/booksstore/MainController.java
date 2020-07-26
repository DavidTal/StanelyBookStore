package com.stanley.books.booksstore;

import java.text.MessageFormat;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
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
	public @ResponseBody String addNewBook(@RequestParam String bookName, @RequestParam String author,
			@RequestParam int quantity, HttpServletResponse response) {

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
			book.setAuthor(author);
			book.increase(quantity);
		}
		
		bookRepository.save(book);
		System.out.println("Added book-data: " + book);
		response.addHeader("Access-Control-Allow-Origin", "*");
		return MessageFormat.format("Added book: {0}, Author: {1}, Quantity: {2}, book-id: {3}.", 
				book.getBookName(), book.getAuthor(), book.getQuantity(), book.getBookId()); 
	}

	@Transactional
	@GetMapping(path = "/buy")
	public @ResponseBody String buyBook(@RequestParam String bookName, @RequestParam Integer quantity, HttpServletResponse response) {
		System.out.println(MessageFormat.format("Trying to buy bookName[{0}], quantity[{1}]", bookName, quantity));
		response.addHeader("Access-Control-Allow-Origin", "*");
		
		// First create the order. Then update it
		OrderData orderData = OrderData.create(quantity); 

		Optional<BookData> bookOptional = this.bookRepository.findByBookName(bookName); 
		System.out.println("After findByBookName: " + bookOptional);

		if (bookOptional.isEmpty()) { 
			System.err.println(MessageFormat.format("Order for bookName[{0}], quantity[{1}] - failed, book does not exist", bookName, quantity));
			return updatedOrder(orderData, OrderStatus.CANCELED_NOT_FOUND, bookName); 
		}
		
		BookData book = bookOptional.get();
		orderData.setBookId(book.getBookId()); 
		
		if (!book.has(quantity)) {
			System.out.println(MessageFormat.format("Order for bookName[{0}], quantity[{1}] - failed existing quantity[{2}] is less then required", bookName, quantity, book.getQuantity()));
			return updatedOrder(orderData, OrderStatus.CANCEL_INSIFFCIENT_QUANTITY, bookName); 
		}
		
		System.out.println(MessageFormat.format("Buying book[{0}], quantity[{1}]", bookOptional, quantity));
		book.decrease(quantity);
		bookRepository.save(book);
		
		return updatedOrder(orderData, OrderStatus.SOLD, bookName); 
	}

	private String updatedOrder(OrderData orderData, OrderStatus status, String bookName) {
		orderData.setStatus(status);
		OrderData orderDataResult = orderDataRepository.save(orderData);
		return MessageFormat.format("Order-id: {0}, book-name: {1}, order quantity: {2}, the order status is: {3}",  
				orderDataResult.getOrderId(), bookName, orderData.getQuantity(), orderData.getStatus());  
	}
	
	@GetMapping(path = "/inventory")
	public @ResponseBody Iterable<BookData> getAllBooksData(HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		System.out.println("in get all books");
		return bookRepository.findAll();
	}
	
	@GetMapping(path = "/order")
	public @ResponseBody String getOrderData(@RequestParam Integer orderId, HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		System.out.println("in get order data, orderId: " + orderId);
		
		Optional<OrderData> orderData = this.orderDataRepository.findById(orderId); 
		if (orderData.isEmpty()) {
			System.err.println("Failed to find order data for orderId: " + orderId);
			return "Failed to find order data for orderId: " + orderId;  
		}
		
		System.out.println("Found order-data: " + orderData.get());
		Optional<BookData> bookData = this.bookRepository.findById(orderData.get().getBookId());
		if (bookData.isEmpty()) {
			System.out.println("Failed to find book data for orderData: " + orderData);
			
			return MessageFormat.format("Order id: {0} status is: {1}, quantity: {2}, It seems that no book was found for this order.", 
					orderData.get().getOrderId(), orderData.get().getStatus(), 
					orderData.get().getQuantity()); 
		}
		
		return MessageFormat.format("Order id: {0} status is: {1}, book-name: {2}, quantity: {3}", orderData.get().getOrderId(), orderData.get().getStatus(), 
				bookData.get().getBookName(), orderData.get().getQuantity()); 
	}
}