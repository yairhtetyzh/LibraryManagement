package com.book.library;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.book.library.dto.BorrowBookHistoryDTO;
import com.book.library.dto.BorrowBookReq;
import com.book.library.exception.BusinessException;
import com.book.library.exception.ResourceNotFoundException;
import com.book.library.model.Book;
import com.book.library.model.BorrowBookHistory;
import com.book.library.model.Borrower;
import com.book.library.repository.BookRepository;
import com.book.library.repository.BorrowBookHistoryRepository;
import com.book.library.repository.BorrowerRepository;
import com.book.library.service.impl.BorrowBookHistoryServiceImpl;

@ExtendWith(MockitoExtension.class)
class BorrowBookHistoryServiceImplTest {

    @Mock
    private BorrowBookHistoryRepository borrowBookHistoryRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private BorrowBookHistoryServiceImpl borrowBookHistoryService;

    private Book book;
    private Borrower borrower;
    private BorrowBookReq borrowBookReq;
    private BorrowBookHistory borrowBookHistory;

    @BeforeEach
    void setUp() {
        // Setup Book
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");

        // Setup Borrower
        borrower = new Borrower();
        borrower.setId(1L);
        borrower.setName("Test Borrower");
        borrower.setEmail("test@example.com");

        // Setup BorrowBookReq
        borrowBookReq = new BorrowBookReq();
        borrowBookReq.setBookId(1L);
        borrowBookReq.setBorrowerId(1L);

        // Setup BorrowBookHistory
        borrowBookHistory = new BorrowBookHistory();
        borrowBookHistory.setId(1L);
        borrowBookHistory.setBook(book);
        borrowBookHistory.setBorrower(borrower);
        borrowBookHistory.setBorrowStatus(Boolean.FALSE);
        borrowBookHistory.setBorrowDate(LocalDateTime.now());
        borrowBookHistory.setCreatedDate(LocalDateTime.now());
        borrowBookHistory.setUpdatedDate(LocalDateTime.now());
    }

    @Test
    void borrowBook_Success() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(borrower));
        when(borrowBookHistoryRepository.findByBookIdAndBorrowerIdAndBorrowStatus(1L, 1L, Boolean.FALSE))
                .thenReturn(Optional.empty());
        when(borrowBookHistoryRepository.save(any(BorrowBookHistory.class))).thenReturn(borrowBookHistory);

        // Act
        BorrowBookHistoryDTO result = borrowBookHistoryService.borrowBook(borrowBookReq);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getBookId());
        assertEquals(1L, result.getBorrowerId());
        assertEquals(Boolean.FALSE, result.isBorrowStatus());

        verify(bookRepository, times(1)).findById(1L);
        verify(borrowerRepository, times(1)).findById(1L);
        verify(borrowBookHistoryRepository, times(1))
                .findByBookIdAndBorrowerIdAndBorrowStatus(1L, 1L, Boolean.FALSE);
        verify(borrowBookHistoryRepository, times(1)).save(any(BorrowBookHistory.class));
    }

    @Test
    void borrowBook_InvalidBookId_ThrowsResourceNotFoundException() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> borrowBookHistoryService.borrowBook(borrowBookReq)
        );

        assertEquals("Invalid Book", exception.getMessage());
        verify(bookRepository, times(1)).findById(1L);
        verify(borrowerRepository, never()).findById(anyLong());
        verify(borrowBookHistoryRepository, never()).save(any());
    }

    @Test
    void borrowBook_InvalidBorrowerId_ThrowsResourceNotFoundException() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(borrowerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> borrowBookHistoryService.borrowBook(borrowBookReq)
        );

        assertEquals("Invalid Borrower", exception.getMessage());
        verify(bookRepository, times(1)).findById(1L);
        verify(borrowerRepository, times(1)).findById(1L);
        verify(borrowBookHistoryRepository, never()).save(any());
    }

    @Test
    void borrowBook_AlreadyBorrowed_ThrowsBusinessException() {
        // Arrange
        when(borrowBookHistoryRepository.findByBookIdAndBorrowerIdAndBorrowStatus(1L, 1L, Boolean.FALSE))
                .thenReturn(Optional.of(borrowBookHistory));

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> borrowBookHistoryService.borrowBook(borrowBookReq)
        );

        assertEquals("Borrower Already Borrowed the book.", exception.getMessage());
        verify(borrowBookHistoryRepository, times(1))
                .findByBookIdAndBorrowerIdAndBorrowStatus(1L, 1L, Boolean.FALSE);
        verify(borrowBookHistoryRepository, never()).save(any());
    }

    @Test
    void returnBorrowBook_Success() {
        // Arrange
        when(borrowBookHistoryRepository.findByBookIdAndBorrowerIdAndBorrowStatus(1L, 1L, Boolean.FALSE))
                .thenReturn(Optional.of(borrowBookHistory));
        when(borrowBookHistoryRepository.save(any(BorrowBookHistory.class))).thenReturn(borrowBookHistory);

        // Act
        BorrowBookHistoryDTO result = borrowBookHistoryService.returnBorrowBook(1L, 1L);

        // Assert
        assertNotNull(result);
        assertTrue(borrowBookHistory.isBorrowStatus());
        assertNotNull(borrowBookHistory.getReturnDate());
        assertNotNull(borrowBookHistory.getUpdatedDate());

        verify(borrowBookHistoryRepository, times(1))
                .findByBookIdAndBorrowerIdAndBorrowStatus(1L, 1L, Boolean.FALSE);
        verify(borrowBookHistoryRepository, times(1)).save(borrowBookHistory);
    }

    @Test
    void returnBorrowBook_BorrowRecordNotFound_ThrowsBusinessException() {
        // Arrange
        when(borrowBookHistoryRepository.findByBookIdAndBorrowerIdAndBorrowStatus(1L, 1L, Boolean.FALSE))
                .thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> borrowBookHistoryService.returnBorrowBook(1L, 1L)
        );

        assertTrue(exception.getMessage().contains("Borrow record not found"));
        assertTrue(exception.getMessage().contains("bookId=1"));
        assertTrue(exception.getMessage().contains("borrowerId=1"));

        verify(borrowBookHistoryRepository, times(1))
                .findByBookIdAndBorrowerIdAndBorrowStatus(1L, 1L, Boolean.FALSE);
        verify(borrowBookHistoryRepository, never()).save(any());
    }

    @Test
    void checkAndGetBorrower_ValidBorrowerId_ReturnsBorrower() {
        // Arrange
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(borrower));

        // Act
        Borrower result = borrowBookHistoryService.checkAndGetBorrower(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Borrower", result.getName());
        verify(borrowerRepository, times(1)).findById(1L);
    }

    @Test
    void checkAndGetBorrower_InvalidBorrowerId_ThrowsResourceNotFoundException() {
        // Arrange
        when(borrowerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> borrowBookHistoryService.checkAndGetBorrower(1L)
        );

        assertEquals("Invalid Borrower", exception.getMessage());
        verify(borrowerRepository, times(1)).findById(1L);
    }

    @Test
    void checkAndGetBook_ValidBookId_ReturnsBook() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Act
        Book result = borrowBookHistoryService.checkAndGetBook(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Book", result.getTitle());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void checkAndGetBook_InvalidBookId_ThrowsResourceNotFoundException() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> borrowBookHistoryService.checkAndGetBook(1L)
        );

        assertEquals("Invalid Book", exception.getMessage());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void borrowBook_VerifyBorrowDateAndTimestampsAreSet() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(borrower));
        when(borrowBookHistoryRepository.findByBookIdAndBorrowerIdAndBorrowStatus(1L, 1L, Boolean.FALSE))
                .thenReturn(Optional.empty());
        when(borrowBookHistoryRepository.save(any(BorrowBookHistory.class))).thenAnswer(invocation -> {
            BorrowBookHistory saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Act
        BorrowBookHistoryDTO result = borrowBookHistoryService.borrowBook(borrowBookReq);

        // Assert
        assertNotNull(result);
        verify(borrowBookHistoryRepository, times(1)).save(argThat(history ->
                history.getBorrowDate() != null &&
                history.getCreatedDate() != null &&
                history.getUpdatedDate() != null &&
                history.isBorrowStatus() == Boolean.FALSE
        ));
    }

    @Test
    void returnBorrowBook_VerifyReturnDateAndUpdatedDateAreSet() {
        // Arrange
        BorrowBookHistory historyToReturn = new BorrowBookHistory();
        historyToReturn.setId(1L);
        historyToReturn.setBook(book);
        historyToReturn.setBorrower(borrower);
        historyToReturn.setBorrowStatus(Boolean.FALSE);
        historyToReturn.setBorrowDate(LocalDateTime.now().minusDays(1));

        when(borrowBookHistoryRepository.findByBookIdAndBorrowerIdAndBorrowStatus(1L, 1L, Boolean.FALSE))
                .thenReturn(Optional.of(historyToReturn));
        when(borrowBookHistoryRepository.save(any(BorrowBookHistory.class))).thenReturn(historyToReturn);

        // Act
        BorrowBookHistoryDTO result = borrowBookHistoryService.returnBorrowBook(1L, 1L);

        // Assert
        assertNotNull(result);
        verify(borrowBookHistoryRepository, times(1)).save(argThat(history ->
                history.isBorrowStatus() == Boolean.TRUE &&
                history.getReturnDate() != null &&
                history.getUpdatedDate() != null
        ));
    }
}