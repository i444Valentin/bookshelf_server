package ru.codeinside.lisova.bookshelf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.codeinside.lisova.bookshelf.model.entity.Bookmark;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByUserBookId_UserIdAndUserBookId_BookId(Long userId, Long bookId);

}
