package com.olafros.exercise2.controller

import com.olafros.exercise2.model.BookDto
import com.olafros.exercise2.model.BookDtoList
import com.olafros.exercise2.model.CreateBookDto
import com.olafros.exercise2.model.UpdateBookDto
import com.olafros.exercise2.service.BookService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/books")
class BookController(
    val bookService: BookService,
) {


    @GetMapping("/{bookId}")
    fun getBookById(@PathVariable bookId: Long): ResponseEntity<BookDto> {
        return ResponseEntity.ok(bookService.getBookById(bookId))
    }

    @GetMapping
    fun getBooks(@RequestParam name: String?, @RequestParam author: String?): ResponseEntity<List<BookDtoList>> {
        return ResponseEntity.ok(bookService.getBooks(name, author))
    }

    @PostMapping
    fun createNewBook(@Valid @RequestBody newBook: CreateBookDto): ResponseEntity<BookDto> {
        return ResponseEntity.ok(bookService.createNewBook(newBook))
    }

    @PutMapping("/{bookId}")
    fun updateBookById(
        @PathVariable bookId: Long,
        @Valid @RequestBody updatedBook: UpdateBookDto
    ): ResponseEntity<*> {
        return ResponseEntity.ok(bookService.updateBookById(bookId, updatedBook))
    }

    @DeleteMapping("/{bookId}")
    fun deleteBookById(@PathVariable bookId: Long): ResponseEntity<*> {
        return ResponseEntity.ok(bookService.deleteBookById(bookId))
    }
}
