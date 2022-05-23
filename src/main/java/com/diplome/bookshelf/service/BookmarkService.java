package com.diplome.bookshelf.service;

import com.diplome.bookshelf.model.entity.Bookmark;

import java.util.Optional;

public interface BookmarkService {

    Optional<Bookmark> getBookmark(Long userId, Long bookId);

    Bookmark save(Long pageId, Long userId, Long bookId);
}
