package ru.codeinside.lisova.bookshelf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.codeinside.lisova.bookshelf.model.dto.request.BookRequestDto;
import ru.codeinside.lisova.bookshelf.model.dto.response.BookResponseDto;
import ru.codeinside.lisova.bookshelf.service.BookService;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public Page<BookResponseDto> getAll(Pageable pageable) {
        return bookService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public BookResponseDto getById(@PathVariable(name = "id") Long id) {
        return bookService.getById(id);
    }

    @PostMapping
    public BookResponseDto create(@RequestBody BookRequestDto bookDto) {
        return bookService.create(bookDto);
    }

    @PutMapping("/{id}")
    public BookResponseDto update(@PathVariable(name = "id") Long id,
                                  @RequestBody BookRequestDto bookDto,
                                  @RequestBody Long userId) {
        return bookService.update(id, bookDto, userId);
    }

    @DeleteMapping("/{bookId}")
    public void delete(@PathVariable(name = "bookId") Long bookId,
                       @RequestParam Long userId) {
        bookService.delete(bookId, userId);
    }

    @PutMapping("/{bookId}/shelf")
    public BookResponseDto changeShelf(@PathVariable(name = "bookId") Long bookId,
                                       @RequestParam(name = "shelfId", required = false) Long shelfId,
                                       @RequestParam Long userId) {
        return bookService.changeShelf(bookId, shelfId, userId);
    }
}
