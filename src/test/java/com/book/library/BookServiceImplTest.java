package com.book.library;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.book.library.dto.BookDTO;
import com.book.library.exception.BusinessException;
import com.book.library.exception.ResourceNotFoundException;
import com.book.library.model.Book;
import com.book.library.model.Borrower;
import com.book.library.repository.BookRepository;
import com.book.library.repository.BorrowerRepository;
import com.book.library.service.impl.BookServiceImpl;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private BookDTO bookDTO;
    private Book book;
    private Borrower borrower;

    @BeforeEach
    void setUp() {
        // Setup BookDTO
        bookDTO = new BookDTO();
        bookDTO.setIsbnNumber("978-3-16-148410-0");
        bookDTO.setTitle("Test Book");
        bookDTO.setAuthor("Test Author");

        // Setup Book
        book = new Book();
        book.setId(1L);
        book.setIsbnNumber("978-3-16-148410-0");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setCreatedDate(LocalDateTime.now());
        book.setUpdatedDate(LocalDateTime.now());

        // Setup Borrower
        borrower = new Borrower();
        borrower.setId(1L);
        borrower.setName("Test Borrower");
        borrower.setEmail("test@example.com");
    }

    @Test
    void register_Success_NewBook() {
        // Arrange
        when(bookRepository.findFirstByIsbnNumber(bookDTO.getIsbnNumber())).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book savedBook = invocation.getArgument(0);
            savedBook.setId(1L);
            return savedBook;
        });

        // Act
        BookDTO result = bookService.register(bookDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("978-3-16-148410-0", result.getIsbnNumber());
        assertEquals("Test Book", result.getTitle());
        assertEquals("Test Author", result.getAuthor());

        verify(bookRepository, times(1)).findFirstByIsbnNumber(bookDTO.getIsbnNumber());
        verify(bookRepository, times(1)).save(argThat(savedBook ->
                savedBook.getIsbnNumber().equals(bookDTO.getIsbnNumber()) &&
                savedBook.getTitle().equals(bookDTO.getTitle()) &&
                savedBook.getAuthor().equals(bookDTO.getAuthor()) &&
                savedBook.getCreatedDate() != null &&
                savedBook.getUpdatedDate() != null
        ));
    }

    @Test
    void register_Success_SameISBNWithSameTitleAndAuthor() {
        // Arrange
        Book existingBook = new Book();
        existingBook.setId(2L);
        existingBook.setIsbnNumber("978-3-16-148410-0");
        existingBook.setTitle("Test Book");
        existingBook.setAuthor("Test Author");

        when(bookRepository.findFirstByIsbnNumber(bookDTO.getIsbnNumber())).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book savedBook = invocation.getArgument(0);
            savedBook.setId(3L);
            return savedBook;
        });

        // Act
        BookDTO result = bookService.register(bookDTO);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("978-3-16-148410-0", result.getIsbnNumber());
        assertEquals("Test Book", result.getTitle());
        assertEquals("Test Author", result.getAuthor());

        verify(bookRepository, times(1)).findFirstByIsbnNumber(bookDTO.getIsbnNumber());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void register_ThrowsBusinessException_SameISBNDifferentTitle() {
        // Arrange
        Book existingBook = new Book();
        existingBook.setId(2L);
        existingBook.setIsbnNumber("978-3-16-148410-0");
        existingBook.setTitle("Different Title");
        existingBook.setAuthor("Test Author");

        when(bookRepository.findFirstByIsbnNumber(bookDTO.getIsbnNumber())).thenReturn(Optional.of(existingBook));

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> bookService.register(bookDTO)
        );

        assertTrue(exception.getMessage().contains("Multiple books with the same ISBN number must have same title"));
        assertTrue(exception.getMessage().contains("978-3-16-148410-0"));
        assertTrue(exception.getMessage().contains("Test Book"));

        verify(bookRepository, times(1)).findFirstByIsbnNumber(bookDTO.getIsbnNumber());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void register_ThrowsBusinessException_SameISBNDifferentAuthor() {
        // Arrange
        Book existingBook = new Book();
        existingBook.setId(2L);
        existingBook.setIsbnNumber("978-3-16-148410-0");
        existingBook.setTitle("Test Book");
        existingBook.setAuthor("Different Author");

        when(bookRepository.findFirstByIsbnNumber(bookDTO.getIsbnNumber())).thenReturn(Optional.of(existingBook));

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> bookService.register(bookDTO)
        );

        assertTrue(exception.getMessage().contains("Multiple books with the same ISBN number must have same author"));
        assertTrue(exception.getMessage().contains("978-3-16-148410-0"));
        assertTrue(exception.getMessage().contains("Test Author"));

        verify(bookRepository, times(1)).findFirstByIsbnNumber(bookDTO.getIsbnNumber());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void checkISBNNumberAlreadyExist_NoExistingBook_DoesNotThrowException() {
        // Arrange
        when(bookRepository.findFirstByIsbnNumber(bookDTO.getIsbnNumber())).thenReturn(Optional.empty());

        // Act & Assert
        assertDoesNotThrow(() -> bookService.checkISBNNumberAlreadyExist(bookDTO));

        verify(bookRepository, times(1)).findFirstByIsbnNumber(bookDTO.getIsbnNumber());
    }

    @Test
    void checkISBNNumberAlreadyExist_ExistingBookWithSameTitleAndAuthor_DoesNotThrowException() {
        // Arrange
        Book existingBook = new Book();
        existingBook.setIsbnNumber("978-3-16-148410-0");
        existingBook.setTitle("Test Book");
        existingBook.setAuthor("Test Author");

        when(bookRepository.findFirstByIsbnNumber(bookDTO.getIsbnNumber())).thenReturn(Optional.of(existingBook));

        // Act & Assert
        assertDoesNotThrow(() -> bookService.checkISBNNumberAlreadyExist(bookDTO));

        verify(bookRepository, times(1)).findFirstByIsbnNumber(bookDTO.getIsbnNumber());
    }

    @Test
    void checkISBNNumberAlreadyExist_DifferentTitle_ThrowsBusinessException() {
        // Arrange
        Book existingBook = new Book();
        existingBook.setIsbnNumber("978-3-16-148410-0");
        existingBook.setTitle("Different Title");
        existingBook.setAuthor("Test Author");

        when(bookRepository.findFirstByIsbnNumber(bookDTO.getIsbnNumber())).thenReturn(Optional.of(existingBook));

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> bookService.checkISBNNumberAlreadyExist(bookDTO)
        );

        assertTrue(exception.getMessage().contains("Multiple books with the same ISBN number must have same title"));
        verify(bookRepository, times(1)).findFirstByIsbnNumber(bookDTO.getIsbnNumber());
    }

    @Test
    void checkISBNNumberAlreadyExist_DifferentAuthor_ThrowsBusinessException() {
        // Arrange
        Book existingBook = new Book();
        existingBook.setIsbnNumber("978-3-16-148410-0");
        existingBook.setTitle("Test Book");
        existingBook.setAuthor("Different Author");

        when(bookRepository.findFirstByIsbnNumber(bookDTO.getIsbnNumber())).thenReturn(Optional.of(existingBook));

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> bookService.checkISBNNumberAlreadyExist(bookDTO)
        );

        assertTrue(exception.getMessage().contains("Multiple books with the same ISBN number must have same author"));
        verify(bookRepository, times(1)).findFirstByIsbnNumber(bookDTO.getIsbnNumber());
    }

    @Test
    void checkAndGetBorrower_ValidBorrowerId_ReturnsBorrower() {
        // Arrange
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(borrower));

        // Act
        Borrower result = bookService.checkAndGetBorrower(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Borrower", result.getName());
        assertEquals("test@example.com", result.getEmail());

        verify(borrowerRepository, times(1)).findById(1L);
    }

    @Test
    void checkAndGetBorrower_InvalidBorrowerId_ThrowsResourceNotFoundException() {
        // Arrange
        when(borrowerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.checkAndGetBorrower(999L)
        );

        assertEquals("Invalid Borrower", exception.getMessage());
        verify(borrowerRepository, times(1)).findById(999L);
    }

    @Test
    void checkAndGetBook_ValidBookId_ReturnsBook() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Act
        Book result = bookService.checkAndGetBook(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Book", result.getTitle());
        assertEquals("Test Author", result.getAuthor());
        assertEquals("978-3-16-148410-0", result.getIsbnNumber());

        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void checkAndGetBook_InvalidBookId_ThrowsResourceNotFoundException() {
        // Arrange
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.checkAndGetBook(999L)
        );

        assertEquals("Invalid Book", exception.getMessage());
        verify(bookRepository, times(1)).findById(999L);
    }

    @Test
    void getAllBooks_ReturnsListOfBooks() {
        // Arrange
        Book book1 = new Book();
        book1.setId(1L);
        book1.setIsbnNumber("978-3-16-148410-0");
        book1.setTitle("Book One");
        book1.setAuthor("Author One");

        Book book2 = new Book();
        book2.setId(2L);
        book2.setIsbnNumber("978-3-16-148410-1");
        book2.setTitle("Book Two");
        book2.setAuthor("Author Two");

        Book book3 = new Book();
        book3.setId(3L);
        book3.setIsbnNumber("978-3-16-148410-2");
        book3.setTitle("Book Three");
        book3.setAuthor("Author Three");

        List<Book> bookList = Arrays.asList(book1, book2, book3);
        when(bookRepository.findAll()).thenReturn(bookList);

        // Act
        List<BookDTO> result = bookService.getAllBooks();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals("Book One", result.get(0).getTitle());
        assertEquals("Author One", result.get(0).getAuthor());

        assertEquals(2L, result.get(1).getId());
        assertEquals("Book Two", result.get(1).getTitle());
        assertEquals("Author Two", result.get(1).getAuthor());

        assertEquals(3L, result.get(2).getId());
        assertEquals("Book Three", result.get(2).getTitle());
        assertEquals("Author Three", result.get(2).getAuthor());

        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getAllBooks_EmptyList_ReturnsEmptyList() {
        // Arrange
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<BookDTO> result = bookService.getAllBooks();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());

        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void register_VerifyTimestampsAreSet() {
        // Arrange
        when(bookRepository.findFirstByIsbnNumber(bookDTO.getIsbnNumber())).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book savedBook = invocation.getArgument(0);
            savedBook.setId(1L);
            return savedBook;
        });

        // Act
        BookDTO result = bookService.register(bookDTO);

        // Assert
        assertNotNull(result);
        verify(bookRepository, times(1)).save(argThat(savedBook ->
                savedBook.getCreatedDate() != null &&
                savedBook.getUpdatedDate() != null
        ));
    }

    @Test
    void checkISBNNumberAlreadyExist_WithNullTitle_ThrowsBusinessException() {
        // Arrange
        Book existingBook = new Book();
        existingBook.setIsbnNumber("978-3-16-148410-0");
        existingBook.setTitle("Some Title");
        existingBook.setAuthor("Test Author");

        BookDTO newBookDTO = new BookDTO();
        newBookDTO.setIsbnNumber("978-3-16-148410-0");
        newBookDTO.setTitle(null);
        newBookDTO.setAuthor("Test Author");

        when(bookRepository.findFirstByIsbnNumber(newBookDTO.getIsbnNumber())).thenReturn(Optional.of(existingBook));

        // Act & Assert
        assertThrows(Exception.class, () -> bookService.checkISBNNumberAlreadyExist(newBookDTO));
    }

    @Test
    void checkISBNNumberAlreadyExist_WithNullAuthor_ThrowsBusinessException() {
        // Arrange
        Book existingBook = new Book();
        existingBook.setIsbnNumber("978-3-16-148410-0");
        existingBook.setTitle("Test Book");
        existingBook.setAuthor("Some Author");

        BookDTO newBookDTO = new BookDTO();
        newBookDTO.setIsbnNumber("978-3-16-148410-0");
        newBookDTO.setTitle("Test Book");
        newBookDTO.setAuthor(null);

        when(bookRepository.findFirstByIsbnNumber(newBookDTO.getIsbnNumber())).thenReturn(Optional.of(existingBook));

        // Act & Assert
        assertThrows(Exception.class, () -> bookService.checkISBNNumberAlreadyExist(newBookDTO));
    }
}