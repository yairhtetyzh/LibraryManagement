package com.book.library.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.book.library.dto.BookDTO;
import com.book.library.exception.BusinessException;
import com.book.library.exception.ResourceNotFoundException;
import com.book.library.model.Book;
import com.book.library.model.Borrower;
import com.book.library.repository.BookRepository;
import com.book.library.repository.BorrowerRepository;
import com.book.library.service.BookService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

	Logger logger = LoggerFactory.getLogger(getClass());
	
	private final BookRepository bookRepository;

	private final BorrowerRepository borrowerRepository;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public BookDTO register(BookDTO bookDTO) {
		checkISBNNumberAlreadyExist(bookDTO);
		Book book = generateBookModel(bookDTO);
		book.setCreatedDate(LocalDateTime.now());
		book.setUpdatedDate(LocalDateTime.now());
		book = bookRepository.save(book);
		bookDTO.setId(book.getId());
		return bookDTO;
	}

	public void checkISBNNumberAlreadyExist(BookDTO bookDTO) {
		Optional<Book> bookOpt = bookRepository.findFirstByIsbnNumber(bookDTO.getIsbnNumber());
		if (bookOpt.isPresent()) {
			if (!bookOpt.get().getTitle().equals(bookDTO.getTitle())) {
				log.info("Multiple books with the same ISBN number must have same Title");
				throw new BusinessException(String.format("Multiple books with the same ISBN number must have same title. There is already ISBN Number(%s) with title (%s).", bookDTO.getIsbnNumber(), bookDTO.getTitle()));
			}
			if (!bookOpt.get().getAuthor().equals(bookDTO.getAuthor())) {
				log.info("Multiple books with the same ISBN number must have same Author");
				throw new BusinessException(String.format("Multiple books with the same ISBN number must have same author. There is already ISBN Number(%s) with author (%s).", bookDTO.getIsbnNumber(), bookDTO.getAuthor()));
			}
		}

	}

	private Book generateBookModel(BookDTO bookDTO) {
		Book book = new Book();
		book.setIsbnNumber(bookDTO.getIsbnNumber());
		book.setTitle(bookDTO.getTitle());
		book.setAuthor(bookDTO.getAuthor());
		return book;
	}

	public Borrower checkAndGetBorrower(Long borrowerId) {
		Optional<Borrower> borrowerOptional = borrowerRepository.findById(borrowerId);
		if (!borrowerOptional.isPresent()) {
			log.debug("Invalid Borrower id : {} ", borrowerId);
			throw new ResourceNotFoundException("Invalid Borrower");
		}
		return borrowerOptional.get();
	}

	public Book checkAndGetBook(Long bookId) {
		Optional<Book> bookOptional = bookRepository.findById(bookId);
		if (!bookOptional.isPresent()) {
			log.debug("Invalid Book id : {} ", bookId);
			throw new ResourceNotFoundException("Invalid Book");
		}
		return bookOptional.get();
	}

	@Override
	public List<BookDTO> getAllBooks() {
		List<Book> bookList = bookRepository.findAll();
		return bookList.stream()
	            .map(book -> new BookDTO(book))
	            .collect(Collectors.toList());
	}

}
