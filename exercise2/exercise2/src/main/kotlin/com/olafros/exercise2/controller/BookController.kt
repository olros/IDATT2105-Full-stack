package com.olafros.exercise2.controller

import com.olafros.exercise2.model.*
import com.olafros.exercise2.repository.BookRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/books")
class BookController(
    val bookRepository: BookRepository,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(BookController::class.java)
    }

    @GetMapping("/{bookId}")
    fun getBookById(@PathVariable bookId: Long): ResponseEntity<*> {
        val book = bookRepository.findBookByIsbn(bookId)
        return if (book != null) {
            ResponseEntity.ok(book.toBookDto())
        } else {
            logger.warn("Could not find book by id: $bookId")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(ErrorResponse("Could not find the book"))
        }
    }

    @GetMapping
    fun getBooks(@RequestParam name: String?, @RequestParam author: String?): ResponseEntity<*> {
        val books = when {
            (name != null && author != null) -> {
                logger.info("Search for book by name: $name and author: $author")
                bookRepository.findBooksByNameContainsAndAuthors_NameContains(
                    name,
                    author
                )
            }
            (name != null) -> {
                logger.info("Search for book by name: $name")
                bookRepository.findBooksByNameContains(name)
            }
            (author != null) -> {
                logger.info("Search for book by author: $author")
                bookRepository.findByAuthors_NameContains(author)
            }
            else -> bookRepository.findAll()
        }
        return ResponseEntity.ok(books.map { book -> book.toBookDtoList() })
    }

    @PostMapping
    fun createNewBook(@Valid @RequestBody newBook: CreateBookDto): ResponseEntity<*> {
        val book = Book(newBook.isbn, newBook.name, newBook.year, newBook.publisher)
        return ResponseEntity.ok().body(bookRepository.save(book).toBookDto())
    }

    @PutMapping("/{bookId}")
    fun updateBookById(
        @PathVariable bookId: Long,
        @Valid @RequestBody updatedBook: UpdateBookDto
    ): ResponseEntity<*> {
        val book = bookRepository.findBookByIsbn(bookId)
        return if (book != null) {
            val newBook = book.copy(
                name = updatedBook.name ?: book.name,
                year = updatedBook.year ?: book.year,
                publisher = updatedBook.publisher ?: book.publisher,
            )
            ResponseEntity.ok().body(bookRepository.save(newBook).toBookDto())
        } else {
            logger.warn("Could not find book by id: $bookId")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(ErrorResponse("Could not find the book to update"))
        }
    }

    @DeleteMapping("/{bookId}")
    fun deleteBookById(@PathVariable bookId: Long): ResponseEntity<*> {
        val book = bookRepository.findBookByIsbn(bookId)
        return if (book != null) {
            bookRepository.delete(book)
            ResponseEntity.ok<Any>(SuccessResponse("Book successfully deleted"))
        } else {
            logger.warn("Could not find book by id: $bookId")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(ErrorResponse("Could not find the book to delete"))
        }
    }
}
