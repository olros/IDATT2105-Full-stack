package com.olafros.exercise2.controller

import com.olafros.exercise2.model.*
import com.olafros.exercise2.repository.AddressRepository
import com.olafros.exercise2.repository.AuthorRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/authors")
class AuthorController(
    val authorRepository: AuthorRepository,
    val addressRepository: AddressRepository,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(AuthorController::class.java)
    }

    @GetMapping("/{authorId}")
    fun getAuthorById(@PathVariable authorId: Long): ResponseEntity<*> {
        val author = authorRepository.findAuthorById(authorId)
        return if (author != null) {
            ResponseEntity.ok(author.toAuthorDto())
        } else {
            logger.warn("Could not find author by id: $authorId")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(ErrorResponse("Could not find the author"))
        }
    }

    @GetMapping
    fun getAuthors(@RequestParam name: String?): ResponseEntity<*> {
        val authors = if (name == null) authorRepository.findAll() else {
            logger.info("Search for author by name: $name")
            authorRepository.findAuthorsByNameContains(name)
        }
        return ResponseEntity.ok(authors.map { author -> author.toAuthorDtoList() })
    }

    @PostMapping
    fun createNewAuthor(@Valid @RequestBody newAuthor: CreateAuthorDto): ResponseEntity<*> {
        val newAddress = Address(
            0,
            newAuthor.address.street,
            newAuthor.address.post_nr,
            newAuthor.address.city,
            newAuthor.address.country,
            null
        )
        val address = addressRepository.save(newAddress)
        val author = Author(0, newAuthor.name, address, mutableListOf())
        return ResponseEntity.ok().body(authorRepository.save(author).toAuthorDto())
    }

    @PutMapping("/{authorId}")
    fun updateAuthorById(
        @PathVariable authorId: Long,
        @Valid @RequestBody updatedAuthor: UpdateAuthorDto
    ): ResponseEntity<*> {
        val author = authorRepository.findAuthorById(authorId)
        return if (author != null) {
            val address = if (updatedAuthor.address != null) Address(
                0,
                updatedAuthor.address.street,
                updatedAuthor.address.post_nr,
                updatedAuthor.address.city,
                updatedAuthor.address.country,
                author
            ) else author.address
            val newAuthor = author.copy(
                name = updatedAuthor.name ?: author.name,
                address = address,
            )
            ResponseEntity.ok().body(authorRepository.save(newAuthor).toAuthorDto())
        } else {
            logger.warn("Could not find author by id: $authorId")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(ErrorResponse("Could not find the team to update"))
        }
    }

    @DeleteMapping("/{authorId}")
    fun deleteAuthorById(@PathVariable authorId: Long): ResponseEntity<*> {
        val author = authorRepository.findAuthorById(authorId)
        return if (author != null) {
            authorRepository.delete(author)
            ResponseEntity.ok<Any>(SuccessResponse("Author successfully deleted"))
        } else {
            logger.warn("Could not find author by id: $authorId")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(ErrorResponse("Could not find the author to delete"))
        }
    }
}
