package com.book.library.service.impl;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.book.library.dto.BorrowerDTO;
import com.book.library.exception.ResourceAlreadyExistsException;
import com.book.library.exception.ResourceNotFoundException;
import com.book.library.model.Borrower;
import com.book.library.repository.BorrowerRepository;
import com.book.library.service.BorrowerService;

@Service
public class BorrowerServiceImpl implements BorrowerService{
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	BorrowerRepository borrowerRepository;

	@Override
	@Transactional
	public BorrowerDTO register(BorrowerDTO borrowerDTO) {
		validateRequest(borrowerDTO);
		Borrower borrower = prepareToModel(borrowerDTO);
		borrower = borrowerRepository.save(borrower);
		borrowerDTO.setId(borrower.getId());
		return borrowerDTO;
	}

	private void validateRequest(BorrowerDTO borrowerDTO) {
		if (borrowerRepository.findByEmail(borrowerDTO.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Borrower with email " + borrowerDTO.getEmail() + " already exists");
        }
	}
	
	public Borrower getBorrowerById(Long id) {
        return borrowerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id: " + id));
    }

	private Borrower prepareToModel(BorrowerDTO borrowerDTO) {
		Borrower borrower = new Borrower();
		borrower.setName(borrowerDTO.getName());
		borrower.setEmail(borrowerDTO.getEmail());
		borrower.setCreatedDate(LocalDateTime.now());
		borrower.setUpdatedDate(LocalDateTime.now());
		return borrower;
	}

}
