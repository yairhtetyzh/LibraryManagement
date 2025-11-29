package com.book.library.dto;

import java.io.Serializable;

import com.book.library.model.BorrowBookHistory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowBookResp implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3736701990692835508L;

	private String invoiceNo;
	
	private Long bookId;
	
	public BorrowBookResp(BorrowBookHistory model) {
		//this.bookId = model.getBookId();
	}
}
