package com.diplome.bookshelf.repository;

import com.diplome.bookshelf.model.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByUserBookId_UserIdAndUserBookId_BookId(Long userId, Long bookId);

}
