package ru.codeinside.lisova.bookshelf.service;

import ru.codeinside.lisova.bookshelf.model.entity.Bookmark;

import java.util.Optional;

public interface BookmarkService {

    Optional<Bookmark> getBookmark(Long userId, Long bookId);

    Bookmark save(Long pageId, Long userId, Long bookId);
}
