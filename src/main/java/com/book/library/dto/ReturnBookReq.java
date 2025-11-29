package com.book.library.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ReturnBookReq implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 6142076139105107517L;
	
	@NotEmpty(message = "InvoiceNo must not be empty")
	private String invoiceNo;

}
