package com.olafros.exercise2.controller

import com.olafros.exercise2.model.CreateBookAuthorDto
import com.olafros.exercise2.model.ErrorResponse
import com.olafros.exercise2.model.toAuthorDtoList
import com.olafros.exercise2.repository.AuthorRepository
import com.olafros.exercise2.repository.BookRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/books/{bookId}/authors")
class BookAuthorController(
    val authorRepository: AuthorRepository,
    val bookRepository: BookRepository,
) {
    @GetMapping
    fun getBookAuthors(@PathVariable bookId: Long): ResponseEntity<*> {
        val book = bookRepository.findBookByIsbn(bookId)
        return if (book != null) {
            ResponseEntity.ok(book.authors.map { author -> author.toAuthorDtoList() })
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(ErrorResponse("Could not find the book"))
        }
    }

    @PostMapping
    fun createNewBookAuthor(
        @PathVariable bookId: Long,
        @Valid @RequestBody newAuthor: CreateBookAuthorDto
    ): ResponseEntity<*> {
        val book = bookRepository.findBookByIsbn(bookId)
        val author = authorRepository.findAuthorById(newAuthor.authorId)
        return when {
            (book == null) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(ErrorResponse("Could not find the book"))
            (author == null) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(ErrorResponse("Could not find the author"))
            (book.authors.any { bookAuthor -> bookAuthor.id == author.id }) ->
                ResponseEntity.status(HttpStatus.CONFLICT).body<Any>(ErrorResponse("The auther is already auther"))
            else -> {
                book.authors.add(author)
                bookRepository.save(book)
                ResponseEntity.ok().body(book.authors.map { author -> author.toAuthorDtoList() })
            }
        }
    }

    @DeleteMapping("/{authorId}")
    fun deleteBookAuthorById(@PathVariable bookId: Long, @PathVariable authorId: Long): ResponseEntity<*> {
        val book = bookRepository.findBookByIsbn(bookId)
        return if (book != null) {
            val author = book.authors.find { bookAuthor -> bookAuthor.id == authorId }
            if (author != null) {
                book.authors.remove(author)
                bookRepository.save(book)
                ResponseEntity.ok().body(book.authors.map { author -> author.toAuthorDtoList() })
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(ErrorResponse("Could not find the author to remove"))
            }
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(ErrorResponse("Could not find the book"))
        }
    }
}
