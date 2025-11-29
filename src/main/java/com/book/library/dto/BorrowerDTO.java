package com.book.library.dto;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Schema(description = "Borrower information")
public class BorrowerDTO implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -221792008195529127L;

	@Schema(hidden = true)
	private Long id;

	@Schema(description = "Full name of the borrower", example = "John Doe")
	@NotEmpty(message = "Name cannot be empty")
	private String name;

	@Schema(description = "Email address of the borrower", example = "john.doe@example.com")
	@NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
	private String email;
}
