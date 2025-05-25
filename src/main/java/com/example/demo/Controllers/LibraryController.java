package com.example.demo.Controllers;


import com.example.demo.Models.*;
import com.example.demo.Repositories.BookRepository;
import com.example.demo.Repositories.BorrowingRepository;
import com.example.demo.Repositories.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/data")
public class LibraryController {
    private final BookRepository bookRepository;
    private final BorrowingRepository borrowingRepository;
    private final UserRepository userRepository;

    public LibraryController(BookRepository bookRepository, BorrowingRepository borrowingRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.borrowingRepository = borrowingRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/books")
    public List<Book> getBooks(
            @RequestParam(defaultValue = "title") String sortBy) {

        List<String> allowedFields = List.of("title", "author", "available");

        if (!allowedFields.contains(sortBy)) {
            sortBy = "title";
        }
        return bookRepository.findAll(Sort.by(Sort.Direction.ASC, sortBy));
    }


    @GetMapping("/users")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/available-books")
    public List<Book> getBorrowedBooks() {
        return bookRepository.findByAvailableTrue();
    }

    @GetMapping("/borrowings")
    public List<Borrowing> getBorrowings() {
        return borrowingRepository.findAll();
    }

    @GetMapping("/borrowings/{id}")
    public List<Borrowing> getBorrowings(@PathVariable Long id) {
        return borrowingRepository.findAllByUserId(id);
    }


    @PostMapping("/book")
    public void addBook(@RequestBody Book book) {
        bookRepository.save(book);
    }

    @PostMapping("/borrow")
    public ResponseEntity<String> borrowBook(@RequestBody BorrowRequest request) {
        Book book = bookRepository.findById(request.getBookId()).orElse(null);
        User user = userRepository.findById(request.getUserId()).orElse(null);

        if (book == null || user == null) {
            return ResponseEntity.badRequest().body("Invalid book or user ID");
        }

        if (!book.isAvailable()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Book is not available");
        }

        book.setAvailable(false);
        bookRepository.save(book);

        Borrowing borrowing = new Borrowing();
        borrowing.setBook(book);
        borrowing.setUser(user);
        borrowing.setBorrowDate(LocalDate.now());
        borrowing.setDueDate(request.getDueDate());

        borrowingRepository.save(borrowing);

        return ResponseEntity.ok("Book borrowed successfully");
    }

    @PostMapping("/add-user")
    public void addUser(@RequestBody User user) {
        userRepository.save(user);
    }

    @DeleteMapping("/borrowing/{userId}/{bookId}")
    public void deleteBorrowing(@PathVariable Long userId, @PathVariable Long bookId) {
        Borrowing borrowing = borrowingRepository.findByUserIdAndBookId(userId, bookId);

        if (borrowing != null) {
            Book book = borrowing.getBook();
            if (book != null) {
                book.setAvailable(true);
                bookRepository.save(book);
            }

            borrowingRepository.delete(borrowing);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Borrowing not found");
        }
    }


    @PutMapping("/borrowing/{userId}/{bookId}")
    public ResponseEntity<String> extendBorrowing(@PathVariable Long userId, @PathVariable Long bookId, @RequestBody LocalDate newDueDate) {
        Borrowing borrowing = borrowingRepository.findByUserIdAndBookId(userId, bookId);
        if (borrowing == null) {
            return ResponseEntity.notFound().build();
        }

        borrowing.setDueDate(newDueDate);
        borrowingRepository.save(borrowing);
        return ResponseEntity.ok("Termin zwrotu przedłużony");
    }

    @PutMapping("/book/{id}")
    public ResponseEntity<String> returnBook(@PathVariable Long id) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        book.setAvailable(true);
        bookRepository.save(book);
        return ResponseEntity.ok("xd");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User request) {
        User user = userRepository.getFirstByEmail(request.getEmail());

        if (user == null || !user.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }

        String token = JwtUtil.generateToken(user.getEmail(), user.getRole(), user.getId());

        return ResponseEntity.ok(Map.of("token", token));
    }


}
