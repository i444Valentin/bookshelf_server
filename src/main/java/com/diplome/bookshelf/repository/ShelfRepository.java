package com.diplome.bookshelf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.diplome.bookshelf.model.entity.Shelf;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Long> {

}
