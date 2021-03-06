package com.diplome.bookshelf.service.impl;

import com.diplome.bookshelf.model.dto.ShortDto;
import com.diplome.bookshelf.model.dto.request.BookRequestDto;
import com.diplome.bookshelf.model.dto.response.BookResponseDto;
import com.diplome.bookshelf.model.entity.Book;
import com.diplome.bookshelf.model.entity.Share;
import com.diplome.bookshelf.model.entity.User;
import com.diplome.bookshelf.repository.UserRepository;
import com.diplome.bookshelf.service.BookService;
import com.diplome.bookshelf.service.BookmarkService;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.diplome.bookshelf.model.entity.Shelf;
import com.diplome.bookshelf.repository.BookRepository;
import com.diplome.bookshelf.repository.ShareRepository;

import javax.transaction.Transactional;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final ShareRepository shareRepository;
    private final UserRepository userRepository;
    private final BookmarkService bookmarkService;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, ShareRepository shareRepository,
                           UserRepository userRepository, BookmarkService bookmarkService) {
        this.bookRepository = bookRepository;
        this.shareRepository = shareRepository;
        this.userRepository = userRepository;
        this.bookmarkService = bookmarkService;
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
                .orElseThrow(() -> new RuntimeException("?????????? ???? ?????????????? id = " + id));
    }

    @Override
    @Transactional
    public BookResponseDto create(BookRequestDto bookDto) {
        userRepository.findById(bookDto.getUserId())
                .orElseThrow(() -> new RuntimeException("???????????????????????? ???? ???????????? id = " + bookDto.getUserId()));

        bookDto.setContent(splitBook(bookDto.getContent()) + ".pdf");
        return toDto(bookRepository.save(toEntity(bookDto)));
    }

    private String splitBook(String path) {
        try {
            File file = new File(path);
            PDDocument document = PDDocument.load(file);
            File directory = new File("src/main/resources/" + file.getName()
                    .replace(".pdf", ""));
            directory.mkdir();

            Splitter splitter = new Splitter();
            List<PDDocument> pages = splitter.split(document);
            Iterator<PDDocument> iterator = pages.listIterator();

            int i = 1;
            while (iterator.hasNext()) {
                PDDocument pd = iterator.next();
                pd.save(directory + "/" + i++ + ".pdf");
            }

            document.close();
            return String.valueOf(directory);
        } catch (IOException e) {
            throw new RuntimeException("???? ?????????????? ?????????????????? ???????? ");
        }
    }

    @Override
    public void read(Long bookId, Long pageId, Long userId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() ->
                new RuntimeException("?????????? ???? ?????????????? id = " + bookId)
        );

        if ((book.getUser().getId().equals(userId) ||
                book.getShares().stream()
                        .map(share -> share.getReceiver().getId())
                        .collect(Collectors.toList()).contains(userId))
        ) {
            bookmarkService.save(pageId, userId, bookId);
            openBookPage(bookId, pageId);
        } else {
            throw new RuntimeException(
                    String.format("???????????????????????? id = %d ???? ?????????? ?????????????? ?? ?????????? id = %d", userId, bookId)
            );
        }
    }

    @Override
    public void read(Long bookId, Long userId) {
        bookmarkService.getBookmark(userId, bookId).ifPresentOrElse(bookmark ->
                        read(bookId, bookmark.getSavePage(), userId),
                () -> read(bookId, 1L, userId));
    }

    private void openBookPage(Long bookId, Long pageId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("?????????? ???? ?????????????? id = " + bookId));

        try {
            Desktop.getDesktop().open(new File(book.getContent().replace(".pdf", "")
                    + "/" + pageId + ".pdf"));
        } catch (IOException e) {
            throw new RuntimeException("???? ?????????????? ?????????????????? ???????? ");
        }
    }

    @Override
    @Transactional
    public BookResponseDto update(Long bookId, BookRequestDto bookDto, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("?????????? ???? ?????????????? id = " + bookId));

        if (!book.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException(
                    String.format("???????????????????????? id = %d ???? ?????????? ?????????????? ?? ?????????? id = %d", userId, bookId)
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
                    String.format("???????????????????????? id = %d ???? ?????????? ?????????????? ?? ?????????? id = %d", userId, bookId)
            );
        }

        bookRepository.deleteById(book.getId());

        List<ShortDto> sharesShortDtoList = Optional.of(book).map(BookResponseDto::getShares)
                .orElse(new ArrayList<>());

        if (!sharesShortDtoList.isEmpty()) {
            List<Share> bookShares = sharesShortDtoList.stream().map(shortDto ->
                    Share.builder()
                            .id(shortDto.getId())
                            .book(Book.builder().id(book.getId()).build())
                            .build()).collect(Collectors.toList());
            shareRepository.deleteAll(bookShares);
        }
    }

    @Override
    @Transactional
    public BookResponseDto changeShelf(Long bookId, Long shelfId, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("?????????? ???? ?????????????? id = " + bookId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("???????????????????????? ???? ???????????? id = " + bookId));

        Optional.of(shelfId).ifPresentOrElse(shelf -> {
            boolean isNotFound = true;

            List<Shelf> shelves = Optional.of(user)
                    .map(User::getShelves)
                    .orElse(new ArrayList<>());

            if (shelves.stream().map(Shelf::getId).collect(Collectors.toList()).contains(shelfId)) {
                isNotFound = false;
            }

            if (!book.getUser().getId().equals(userId) || isNotFound) {
                throw new IllegalArgumentException(
                        String.format("???????????????????????? id = %d ???? ?????????? ?????????????? ?? ?????????? id = %d", userId, shelfId)
                );
            }

            book.setShelf(Shelf.builder()
                    .id(shelfId)
                    .build());
        }, () -> book.setShelf(null));

        return toDto(bookRepository.save(book));
    }

    @Override
    @Transactional
    public void removeFromShelf(Long shelfId) {
        List<Book> booksFromShelf = bookRepository.findByShelf_Id(shelfId);

        booksFromShelf.forEach(book -> book.setShelf(null));
    }

    private Book toEntity(BookRequestDto bookResponseDto) {
        return Book.builder()
                .title(bookResponseDto.getTitle())
                .content(bookResponseDto.getContent())
                .user(User.builder().id(bookResponseDto.getUserId()).build())
                .build();
    }

    private BookResponseDto toDto(Book book) {

        Shelf bookShelf = book.getShelf();
        ShortDto shelf = bookShelf != null ? ShortDto.builder()
                .id(bookShelf.getId())
                .name(bookShelf.getName())
                .build()
                : null;

        BookResponseDto.BookResponseDtoBuilder builder = BookResponseDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .content(book.getContent())
                .user(ShortDto.builder()
                        .id(book.getUser().getId())
                        .name(book.getUser().getName())
                        .build())
                .shelf(shelf);

        List<Share> shares = Optional.of(book)
                .map(Book::getShares)
                .orElse(new ArrayList<>());

        if (!shares.isEmpty()) {
            List<ShortDto> sharesShortDtoList = shares.stream().map(share ->
                    ShortDto.builder()
                            .id(share.getId())
                            .name("?????????????????????????????? ??????????: " + share.getBook().getTitle())
                            .build()).collect(Collectors.toList());

            builder.shares(sharesShortDtoList);
        }

        return builder.build();
    }
}
