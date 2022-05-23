package com.diplome.bookshelf.service.impl;

import com.diplome.bookshelf.model.entity.Bookmark;
import com.diplome.bookshelf.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.diplome.bookshelf.repository.BookmarkRepository;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    @Autowired
    public BookmarkServiceImpl(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    @Override
    @Transactional
    public Optional<Bookmark> getBookmark(Long userId, Long bookId) {
        return bookmarkRepository.findByUserBookId_UserIdAndUserBookId_BookId(userId, bookId);
    }

    @Override
    @Transactional
    public Bookmark save(Long pageId, Long userId, Long bookId) {
        return bookmarkRepository.save(
                Bookmark.builder()
                        .savePage(pageId)
                        .userBookId(Bookmark.UserBookId.builder()
                                .userId(userId)
                                .bookId(bookId)
                                .build())
                        .build());
    }
}
