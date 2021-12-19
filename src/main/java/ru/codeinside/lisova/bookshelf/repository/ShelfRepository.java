package ru.codeinside.lisova.bookshelf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.codeinside.lisova.bookshelf.model.entity.Shelf;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Long> {

}
