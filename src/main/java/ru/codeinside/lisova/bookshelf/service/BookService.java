package ru.codeinside.lisova.bookshelf.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.codeinside.lisova.bookshelf.model.dto.request.BookRequestDto;
import ru.codeinside.lisova.bookshelf.model.dto.response.BookResponseDto;

import java.io.File;

public interface BookService {

    Page<BookResponseDto> getAll(Pageable pageable);

    BookResponseDto getById(Long id);

    BookResponseDto create(BookRequestDto bookDto);

    void read(Long bookId, Long pageId, Long userId);

    void read(Long bookId, Long userId);

    BookResponseDto update(Long id, BookRequestDto bookDto, Long userId);

    void delete(Long bookId, Long userId);

    BookResponseDto changeShelf(Long bookId, Long userId, Long shelfId);

    void removeFromShelf(Long shelfId);
}
