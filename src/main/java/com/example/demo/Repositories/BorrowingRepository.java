package com.example.demo.Repositories;

import com.example.demo.Models.Book;
import com.example.demo.Models.Borrowing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    List<Borrowing> findAllByUserId(Long userId);

    Borrowing findByUserIdAndBookId(Long userId, Long bookId);
}
