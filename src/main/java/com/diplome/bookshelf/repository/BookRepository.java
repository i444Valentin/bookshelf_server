package com.diplome.bookshelf.repository;

import com.diplome.bookshelf.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByShelf_Id(Long shelfId);

    Optional<Book> findByIdAndUser_Id(Long bookId, Long userId);

}
