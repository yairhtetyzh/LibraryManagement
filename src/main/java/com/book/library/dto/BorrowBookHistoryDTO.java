package com.book.library.dto;

import java.io.Serializable;

import com.book.library.constant.CommonConstant;
import com.book.library.model.BorrowBookHistory;
import com.book.library.utils.CommonUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BorrowBookHistoryDTO implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 7616339006208111523L;

	private Long id;

	private Long bookId;
	
	private String bookTitle;
	
	private String bookAuthor;

	private Long borrowerId;
	
	private String borrowerName;

	private boolean borrowStatus;
	
	private String borrowDate;
	
	private String returnDate;
	
	public BorrowBookHistoryDTO(BorrowBookHistory b) {
		this.id = b.getId();
		this.bookId = b.getBook().getId();
		this.bookTitle = b.getBook().getTitle();
		this.bookAuthor = b.getBook().getAuthor();
		this.borrowerId = b.getBorrower().getId();
		this.borrowerName = b.getBorrower().getName();
		this.borrowStatus = b.isBorrowStatus();
		this.borrowDate = b.getBorrowDate() == null ? null : CommonUtils.formatLocalDateTime(b.getBorrowDate(), CommonConstant.DATE_FORMAT_yyyymmdd_HHMMSS);
		this.returnDate = b.getReturnDate() == null ? null : CommonUtils.formatLocalDateTime(b.getReturnDate(), CommonConstant.DATE_FORMAT_yyyymmdd_HHMMSS);
	}
}
