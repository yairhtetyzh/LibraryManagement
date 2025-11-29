package com.book.library.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.book.library.dto.BorrowBookHistoryDTO;
import com.book.library.dto.BorrowBookReq;
import com.book.library.exception.BusinessException;
import com.book.library.exception.ResourceNotFoundException;
import com.book.library.model.Book;
import com.book.library.model.BorrowBookHistory;
import com.book.library.model.Borrower;
import com.book.library.repository.BookRepository;
import com.book.library.repository.BorrowBookHistoryRepository;
import com.book.library.repository.BorrowerRepository;
import com.book.library.service.BorrowBookHistoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class BorrowBookHistoryServiceImpl implements BorrowBookHistoryService{
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	private final BorrowBookHistoryRepository borrowBookHistoryRepository;
	
	private final BookRepository bookRepository;
	
	private final BorrowerRepository borrowerRepository;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public BorrowBookHistoryDTO borrowBook(BorrowBookReq req) {
		isBorrowAlready(req);
		BorrowBookHistory borrowBookHistory = prepareToModel(req);
		borrowBookHistory = borrowBookHistoryRepository.save(borrowBookHistory);
		return new BorrowBookHistoryDTO(borrowBookHistory);
	}

	private void isBorrowAlready(BorrowBookReq req) {
		Optional<BorrowBookHistory> borrowOptional = borrowBookHistoryRepository.findByBookIdAndBorrowerIdAndBorrowStatus(req.getBookId(), req.getBorrowerId(), Boolean.FALSE);
		if(borrowOptional.isPresent()) {
			log.info("Book Id : [{}] is Already Borrowed by Borrower : [{}]", req.getBookId(), req.getBorrowerId());
			throw new BusinessException("Borrower Already Borrowed the book.");
		}
		
		Optional<BorrowBookHistory> borrowByAnyoneElseOptional = borrowBookHistoryRepository.findByBookIdAndBorrowStatus(req.getBookId(), Boolean.FALSE);
		if(borrowByAnyoneElseOptional.isPresent()) {
			log.info("Book Id : [{}] is Already Borrowed by Someone Else.", req.getBookId());
			throw new BusinessException("Another Borrower Already Borrowed the book.");
		}
	}

	private BorrowBookHistory prepareToModel(BorrowBookReq req) {
		Book book = checkAndGetBook(req.getBookId());
		Borrower borrower = checkAndGetBorrower(req.getBorrowerId());
		BorrowBookHistory borrowBookHistory = new BorrowBookHistory();
		borrowBookHistory.setBook(book);
		borrowBookHistory.setBorrower(borrower);
		borrowBookHistory.setBorrowStatus(Boolean.FALSE);
		borrowBookHistory.setBorrowDate(LocalDateTime.now());
		borrowBookHistory.setCreatedDate(LocalDateTime.now());
		borrowBookHistory.setUpdatedDate(LocalDateTime.now());
		return borrowBookHistory;
	}

	@Override
	@Transactional
	public BorrowBookHistoryDTO returnBorrowBook(Long bookId, Long borrowerId) {
		BorrowBookHistory borrowBookHistory = checkBorrowRecordExist(bookId, borrowerId);
		updateBorrowBookStatus(borrowBookHistory);
		return new BorrowBookHistoryDTO(borrowBookHistory);
	}

	private BorrowBookHistory checkBorrowRecordExist(Long bookId, Long borrowerId) {
		return borrowBookHistoryRepository
	            .findByBookIdAndBorrowerIdAndBorrowStatus(bookId, borrowerId, Boolean.FALSE)
	            .orElseThrow(() -> {
	                log.error("Borrow record not found for bookId={} and borrowerId={}", bookId, borrowerId);
	                return new BusinessException(
	                        "Borrow record not found for bookId=" + bookId + " and borrowerId=" + borrowerId
	                );
	            });
	}

	private void updateBorrowBookStatus(BorrowBookHistory borrowBookHistory) {
		borrowBookHistory.setBorrowStatus(Boolean.TRUE);
		borrowBookHistory.setReturnDate(LocalDateTime.now());
		borrowBookHistory.setUpdatedDate(LocalDateTime.now());
		borrowBookHistoryRepository.save(borrowBookHistory);
	}
	
	public Borrower checkAndGetBorrower(Long borrowerId) {
		Optional<Borrower> borrowerOptional = borrowerRepository.findById(borrowerId);
		if (!borrowerOptional.isPresent()) {
			log.error("Invalid Borrower id : {} ", borrowerId);
			throw new ResourceNotFoundException("Invalid Borrower");
		}
		return borrowerOptional.get();
	}

	public Book checkAndGetBook(Long bookId) {
		Optional<Book> bookOptional = bookRepository.findById(bookId);
		if (!bookOptional.isPresent()) {
			log.error("Invalid Book id : {} ", bookId);
			throw new ResourceNotFoundException("Invalid Book");
		}
		return bookOptional.get();
	}

}
