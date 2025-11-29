package com.book.library.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class BorrowBookReq implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 4741822025515028786L;

	@NotNull(message = "Book Id must not be empty")
	private Long bookId;
	
	@NotNull(message = "Borrower Id must not be empty")
	private Long borrowerId;
}
