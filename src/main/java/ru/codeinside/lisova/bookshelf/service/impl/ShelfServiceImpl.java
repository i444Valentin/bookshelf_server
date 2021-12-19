package ru.codeinside.lisova.bookshelf.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.codeinside.lisova.bookshelf.model.dto.ShortDto;
import ru.codeinside.lisova.bookshelf.model.dto.request.ShelfRequestDto;
import ru.codeinside.lisova.bookshelf.model.dto.response.ShelfResponseDto;
import ru.codeinside.lisova.bookshelf.model.entity.Book;
import ru.codeinside.lisova.bookshelf.model.entity.Shelf;
import ru.codeinside.lisova.bookshelf.model.entity.User;
import ru.codeinside.lisova.bookshelf.repository.ShelfRepository;
import ru.codeinside.lisova.bookshelf.service.BookService;
import ru.codeinside.lisova.bookshelf.service.ShelfService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShelfServiceImpl implements ShelfService {

    private final ShelfRepository shelfRepository;
    private final BookService bookService;

    @Autowired
    public ShelfServiceImpl(ShelfRepository shelfRepository, BookService bookService) {
        this.shelfRepository = shelfRepository;
        this.bookService = bookService;
    }

    @Override
    @Transactional
    public Page<ShelfResponseDto> getAll(Pageable pageable) {
        return shelfRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    @Transactional
    public ShelfResponseDto getById(Long id) {
        return shelfRepository.findById(id).map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Полка не найдена id = " + id));
    }

    @Override
    @Transactional
    public ShelfResponseDto create(ShelfRequestDto shelfDto) {
        return toDto(shelfRepository.save(toEntity(shelfDto)));
    }

    @Override
    @Transactional
    public ShelfResponseDto update(Long shelfId, ShelfRequestDto shelfDto, Long userId) {
        Shelf shelf = shelfRepository.findById(shelfId)
                .orElseThrow(() -> new RuntimeException(String.format("Полка не найдена id = %d", shelfId)));
        if (!shelf.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException(
                    String.format("Пользователь id = %d не имеет доступа к полке id = %d", userId, shelfId)
            );
        }
        shelf.setName(shelfDto.getName());
        return toDto(shelfRepository.save(shelf));
    }

    @Override
    @Transactional
    public void delete(Long shelfId, Long userId) {
        var shelf = getById(shelfId);
        if (!shelf.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException(
                    String.format("Пользователь id = %d не имеет доступа к полке id = %d", userId, shelfId)
            );
        }
        
        bookService.removeFromShelf(shelfId);
        shelfRepository.deleteById(shelf.getId());
    }

    private Shelf toEntity(ShelfRequestDto shelfRequestDto) {
        return Shelf.builder()
                .name(shelfRequestDto.getName())
                .user(User.builder().id(shelfRequestDto.getUserId()).build())
                .build();
    }

    private ShelfResponseDto toDto(Shelf shelf) {

        List<Book> books = Optional.ofNullable(shelf).map(Shelf::getBooks)
                .orElseThrow(() -> new RuntimeException("Книги на полке не найдены"));

        List<ShortDto> bookShortDtoList = new ArrayList<>();

        if (books != null) {

            for (Book book : books) {
                bookShortDtoList.add(
                        ShortDto.builder()
                                .id(book.getId())
                                .name(book.getTitle())
                                .build()
                );
            }
        }

        return ShelfResponseDto.builder()
                .id(shelf.getId())
                .name(shelf.getName())
                .user(ShortDto.builder()
                        .id(shelf.getUser().getId())
                        .name(shelf.getUser().getName())
                        .build())
                .books(bookShortDtoList)
                .build();
    }
}
