package com.book.library.service;
import java.util.List;

import com.book.library.dto.BookDTO;

public interface BookService {
	
     public BookDTO register(BookDTO bookDTO);

	 public List<BookDTO> getAllBooks();
	
}
