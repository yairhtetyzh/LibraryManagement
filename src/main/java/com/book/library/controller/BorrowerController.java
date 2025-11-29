package com.book.library.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.book.library.dto.GlobalResponse;
import com.book.library.dto.BorrowerDTO;
import com.book.library.service.BorrowerService;


import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/v1/borrower")
@RequiredArgsConstructor
public class BorrowerController {

	private final Logger logger = LoggerFactory.getLogger(BorrowerController.class);

	private final BorrowerService borrowerService;

	@Operation(
		    summary = "Register a new borrower",
		    description = "Register a new borrower in the library system. Email must be unique."
		)
		@ApiResponses(value = {
		    @ApiResponse(
		        responseCode = "201",
		        description = "Borrower registered successfully",
		        content = @Content(
		            mediaType = "application/json",
		            schema = @Schema(implementation = com.book.library.dto.GlobalResponse.class)
		        )
		    ),
		    @ApiResponse(
		        responseCode = "400",
		        description = "Invalid input - validation failed or email already exists",
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
			@Parameter(description = "Borrower details to register", required = true)
			@Valid @RequestBody BorrowerDTO borrowerDTO) {
		logger.debug("Start register borrower : [{}] ", borrowerDTO);
		BorrowerDTO borrower = borrowerService.register(borrowerDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponse.success("Borrower registered successfully", borrower));
	}
}
