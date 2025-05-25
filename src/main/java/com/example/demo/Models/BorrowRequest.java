package com.example.demo.Models;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BorrowRequest {
    private Long bookId;
    private Long userId;
    private LocalDate dueDate;
}
