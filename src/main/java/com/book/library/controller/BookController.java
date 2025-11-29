package com.book.library.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book.library.dto.GlobalResponse;
import com.book.library.dto.BookDTO;
import com.book.library.dto.BorrowBookHistoryDTO;
import com.book.library.dto.BorrowBookReq;
import com.book.library.service.BookService;
import com.book.library.service.BorrowBookHistoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/book")
@RequiredArgsConstructor
@Tag(name = "Book Management", description = "APIs for managing books, borrowing, and returning operations")
public class BookController {

	private final Logger logger = LoggerFactory.getLogger(BookController.class);

	private final BookService bookService;
	
	private final BorrowBookHistoryService borrowBookHistoryService;

	@Operation(
		    summary = "Register a new book",
		    description = "Register a new book in the library system. ISBN number must be unique or match existing book with same title and author."
		)
		@ApiResponses(value = {
				@ApiResponse(
		        responseCode = "201",
		        description = "Book registered successfully",
		        content = @Content(
		            mediaType = "application/json",
		            schema = @Schema(implementation = GlobalResponse.class)
		        )
		    ),
				@ApiResponse(
		        responseCode = "400",
		        description = "Invalid input - validation failed or ISBN conflict",
		        content = @Content(mediaType = "application/json")
		    ),
				@ApiResponse(
		        responseCode = "500",
		        description = "Internal server error",
		        content = @Content(mediaType = "application/json")
		    )
		})
	@RequestMapping(value = "register", method = RequestMethod.POST)
	public ResponseEntity<?> register(
			@Parameter(description = "Book details to register", required = true)
			@Valid @RequestBody BookDTO bookDTO) {
		BookDTO book = bookService.register(bookDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponse.success("Book registered successfully", book));
	}
	
	
	@Operation(
			summary = "Get all books",
			description = "Retrieve a list of all books available in the library system"
		)
		@ApiResponses(value = {
			@ApiResponse(
				responseCode = "200",
				description = "Books retrieved successfully",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = GlobalResponse.class)
				)
			),
			@ApiResponse(
				responseCode = "500",
				description = "Internal server error",
				content = @Content(mediaType = "application/json")
			)
		})
	@RequestMapping(value = "getall", method = RequestMethod.GET)
    public ResponseEntity<?> getAllBooks() {
        List<BookDTO> books = bookService.getAllBooks();
        return ResponseEntity.ok(GlobalResponse.success("Books retrieved successfully", books));
    }
	
	@Operation(
			summary = "Borrow a book",
			description = "Allow a borrower to borrow a book from the library. The book must not be already borrowed by the same borrower."
		)
		@ApiResponses(value = {
			@ApiResponse(
				responseCode = "200",
				description = "Book borrowed successfully",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = GlobalResponse.class)
				)
			),
			@ApiResponse(
				responseCode = "400",
				description = "Bad request - Book already borrowed or invalid book/borrower",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "500",
				description = "Internal server error",
				content = @Content(mediaType = "application/json")
			)
		})
	@RequestMapping(value = "borrow", method = RequestMethod.POST)
	public ResponseEntity<?> borrow( 
			@Parameter(description = "Borrow request containing book ID and borrower ID", required = true)
			@Valid @RequestBody BorrowBookReq req) {
		logger.debug("Start borrow book request : [{}] ", req);
		BorrowBookHistoryDTO borrowBookHistoryDTO = borrowBookHistoryService.borrowBook(req);
		return ResponseEntity.ok(GlobalResponse.success("Book borrowed successfully", borrowBookHistoryDTO));
	}
	
	@Operation(
			summary = "Return a borrowed book",
			description = "Process the return of a borrowed book by a borrower. Updates the borrow status and records the return date."
		)
		@ApiResponses(value = {
			@ApiResponse(
				responseCode = "200",
				description = "Book returned successfully",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = GlobalResponse.class)
				)
			),
			@ApiResponse(
				responseCode = "500",
				description = "Internal server error",
				content = @Content(mediaType = "application/json")
			)
		})
	@RequestMapping(value = "/{bookId}/return", method = RequestMethod.POST)
	public ResponseEntity<?> returnBook(
			@Parameter(description = "ID of the book to be returned", required = true, example = "1")
			@PathVariable Long bookId,
			@Parameter(description = "ID of the borrower returning the book", required = true, example = "1")
			@RequestParam(required = true) Long borrowerId) {
		
		BorrowBookHistoryDTO borrowBookHistoryDTO = borrowBookHistoryService.returnBorrowBook(bookId, borrowerId);
        return ResponseEntity.ok(GlobalResponse.success("Book returned successfully", borrowBookHistoryDTO));
	}
}
