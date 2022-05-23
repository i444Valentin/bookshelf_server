package com.diplome.bookshelf.controller;

import com.diplome.bookshelf.model.dto.request.BookRequestDto;
import com.diplome.bookshelf.model.dto.response.BookResponseDto;
import com.diplome.bookshelf.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    @RequestMapping(path="/{bookId}/content",method=RequestMethod.GET)
    public ResponseEntity<ByteArrayResource> read(@PathVariable(name = "bookId") Long bookId,
                                                  @RequestParam(name = "page", required = false) Long page,
                                                  @RequestParam(name = "userId") Long userId) throws IOException {
        String file = bookService.getById(bookId).getContent();

        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(Path.of(file)));
//        if (page != null) {
//            bookService.read(bookId, page, userId);
//        } else {
//            bookService.read(bookId, userId);
//        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping
    public BookResponseDto create(@RequestBody BookRequestDto bookDto) {
        return bookService.create(bookDto);
    }

    @PutMapping("/{id}")
    public BookResponseDto update(@PathVariable(name = "id") Long id,
                                  @RequestParam Long userId,
                                  @RequestBody BookRequestDto bookDto) {
        return bookService.update(id, bookDto, userId);
    }

    @DeleteMapping("/{bookId}")
    public void delete(@PathVariable(name = "bookId") Long bookId,
                       @RequestParam Long userId) {
        bookService.delete(bookId, userId);
    }

    @PutMapping("/{bookId}/shelf/{shelfId}")
    public BookResponseDto changeShelf(@PathVariable(name = "bookId") Long bookId,
                                       @PathVariable(name = "shelfId", required = false) Long shelfId,
                                       @RequestParam Long userId) {
        return bookService.changeShelf(bookId, shelfId, userId);
    }
}
