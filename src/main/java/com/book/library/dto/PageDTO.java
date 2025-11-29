package com.book.library.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PageDTO<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3047921928329436893L;
	List<T> content = new ArrayList<T>();
	int page;
	int size;
	int numberofElements;
	long totalElements;
	int totalPages;
}
