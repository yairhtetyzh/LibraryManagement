package com.book.library.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "borrow_book_history")
public class BorrowBookHistory extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7595629945158221533L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "book_id")
	private Book book;
	
	@ManyToOne
	@JoinColumn(name = "borrower_id")
	private Borrower borrower;
	
	@Column(name = "borrow_status")
	private boolean borrowStatus; // 0 - borrow and 1 - return
	
	@Column(name = "borrow_date")
	private LocalDateTime borrowDate;
	
	@Column(name = "return_date")
	private LocalDateTime returnDate;
}
