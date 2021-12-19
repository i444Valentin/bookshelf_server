package ru.codeinside.lisova.bookshelf.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.codeinside.lisova.bookshelf.model.dto.ShortDto;
import ru.codeinside.lisova.bookshelf.model.dto.request.BookRequestDto;
import ru.codeinside.lisova.bookshelf.model.dto.response.BookResponseDto;
import ru.codeinside.lisova.bookshelf.model.entity.Book;
import ru.codeinside.lisova.bookshelf.model.entity.Share;
import ru.codeinside.lisova.bookshelf.model.entity.Shelf;
import ru.codeinside.lisova.bookshelf.model.entity.User;
import ru.codeinside.lisova.bookshelf.repository.BookRepository;
import ru.codeinside.lisova.bookshelf.repository.ShareRepository;
import ru.codeinside.lisova.bookshelf.repository.UserRepository;
import ru.codeinside.lisova.bookshelf.service.BookService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final ShareRepository shareRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, ShareRepository shareRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.shareRepository = shareRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Page<BookResponseDto> getAll(Pageable pageable) {
        return bookRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    @Transactional
    public BookResponseDto getById(Long id) {
        return bookRepository.findById(id).map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Книга не найдена id = " + id));
    }

    @Override
    @Transactional
    public BookResponseDto create(BookRequestDto bookDto) {
        return toDto(bookRepository.save(toEntity(bookDto)));
    }

    @Override
    @Transactional
    public BookResponseDto update(Long bookId, BookRequestDto bookDto, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Книга не найдена id = " + bookId));

        if (!book.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException(
                    String.format("Пользователь id = %d не имеет доступа к книге id = %d", userId, bookId)
            );
        }

        book.setTitle(bookDto.getTitle());
        book.setContent(bookDto.getContent());
        return toDto(bookRepository.save(book));
    }


    @Override
    @Transactional
    public void delete(Long bookId, Long userId) {
        var book = getById(bookId);

        if (!book.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException(
                    String.format("Пользователь id = %d не имеет доступа к книге id = %d", userId, bookId)
            );
        }

        List<ShortDto> sharesShortDtoList = Optional.ofNullable(book.getShares())
                .orElseThrow(() -> new RuntimeException(String.format("Shares не найдены у книги id = %d", bookId)));

        List<Share> bookShares = new ArrayList<>();

//        if (sharesShortDtoList != null) {
        for (ShortDto shortDto : sharesShortDtoList) {
            bookShares.add(Share.builder()
                    .id(shortDto.getId())
                    .book(Book.builder().id(book.getId()).build())
                    .build());
        }
//        }

        bookRepository.deleteById(book.getId());
        shareRepository.deleteAll(bookShares);

    }

    @Override
    @Transactional
    public BookResponseDto changeShelf(Long bookId, Long shelfId, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Книга не найдена id = " + bookId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден id = " + bookId));

//        Optional.ofNullable(shelfId).orElseThrow(
//                () -> new RuntimeException("Полка не найдена id = " + shelfId)
//        );

        if (shelfId != null) {
            boolean isNotFound = true;
            for (Shelf shelf : user.getShelves()) {
                if (shelf.getId().equals(shelfId)) {
                    isNotFound = false;
                    break;
                }
            }

            if (!book.getUser().getId().equals(userId) || isNotFound) {
                throw new IllegalArgumentException(
                        String.format("Пользователь id = %d не имеет доступа к полке id = %d", userId, shelfId)
                );
            }

            book.setShelf(Shelf.builder()
                    .id(shelfId)
                    .build());
        } else {
            book.setShelf(null);
        }

        return toDto(bookRepository.save(book));
    }

    @Override
    @Transactional
    public void removeFromShelf(Long shelfId) {
        List<Book> booksFromShelf = bookRepository.findByShelf_Id(shelfId);

        booksFromShelf.forEach(book -> book.setShelf(null));

//        for (Book book : booksFromShelf) {
//            book.setShelf(null);
//        }
    }

    private Book toEntity(BookRequestDto bookResponseDto) {
        return Book.builder()
                .title(bookResponseDto.getTitle())
                .content(bookResponseDto.getContent())
                .user(User.builder().id(bookResponseDto.getUserId()).build())
                .build();
    }

    private BookResponseDto toDto(Book book) {

        List<Share> shares = Optional.ofNullable(book).map(Book::getShares)
                .orElseThrow(() -> new RuntimeException("Share у книги не найдены"));

        List<ShortDto> sharesShortDtoList = new ArrayList<>();

        if (shares != null) {
            for (Share share : shares) {
                sharesShortDtoList.add(ShortDto.builder()
                        .id(share.getId())
                        .name("Предоставленная книга: " + share.getBook().getTitle())
                        .build());
            }
        }

        Shelf bookShelf = book.getShelf();
        ShortDto shelf = bookShelf != null ? ShortDto.builder()
                .id(bookShelf.getId())
                .name(bookShelf.getName())
                .build()
                : null;

        return BookResponseDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .content(book.getContent())
                .user(ShortDto.builder()
                        .id(book.getUser().getId())
                        .name(book.getUser().getName())
                        .build())
                .shelf(shelf)
                .shares(sharesShortDtoList)
                .build();
    }
}
