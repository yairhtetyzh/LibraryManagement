package com.book.library.dto;

import java.io.Serializable;

import com.book.library.model.Book;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -169283352337062718L;

	@Schema(hidden = true)
	private Long id;

	@Schema(description = "ISBN number of the book", example = "978-3-16-148410-0")
	@NotEmpty(message = "ISBN Number must not be empty")
	private String isbnNumber;

	@Schema(description = "Title of the book", example = "Effective Java")
	@NotEmpty(message = "Title must not be empty")
	private String title;

	@Schema(description = "Author of the book", example = "Joshua Bloch")
	@NotEmpty(message = "Author must not be empty")
	private String author;
	
	public BookDTO(Book b) {
		this.id = b.getId();
		this.isbnNumber = b.getIsbnNumber();
		this.title = b.getTitle();
		this.author = b.getAuthor();
	}
}
