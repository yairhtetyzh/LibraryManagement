package com.book.library.service;

import com.book.library.dto.BorrowBookHistoryDTO;
import com.book.library.dto.BorrowBookReq;

public interface BorrowBookHistoryService {
	
	public BorrowBookHistoryDTO borrowBook(BorrowBookReq req);

	public BorrowBookHistoryDTO returnBorrowBook(Long bookId, Long borrowerId);

}
