package com.diplome.bookshelf.repository;

import com.diplome.bookshelf.model.entity.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShareRepository extends JpaRepository<Share, Long> {

    List<Share> findByDateEndBefore(LocalDate date);

}
