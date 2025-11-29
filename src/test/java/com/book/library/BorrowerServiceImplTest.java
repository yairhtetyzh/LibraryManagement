package com.book.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.book.library.dto.BorrowerDTO;
import com.book.library.model.Borrower;
import com.book.library.repository.BorrowerRepository;
import com.book.library.service.impl.BorrowerServiceImpl;

@ExtendWith(MockitoExtension.class)
public class BorrowerServiceImplTest {

	@Mock
	private BorrowerRepository borrowerRepository;

	@InjectMocks
	private BorrowerServiceImpl borrowerService;

	private Borrower borrower;

	private BorrowerDTO borrowerDTO;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		setBorrower();
	}

	private void setBorrower() {
		borrower = new Borrower();
		borrower.setId(1L);
		borrower.setName("Stephen");
		borrower.setEmail("stephen@gmail.com");

		borrowerDTO = new BorrowerDTO();
		borrowerDTO.setName("Stephen");
		borrowerDTO.setEmail("stephen@gmail.com");
	}

	@Test
	void testRegisterBorrower() {
		when(borrowerRepository.save(any(Borrower.class))).thenReturn(borrower);

		BorrowerDTO result = borrowerService.register(borrowerDTO);

		assertNotNull(result);
		assertEquals("Stephen", result.getName());
		assertEquals(1L, result.getId());
	}
}
