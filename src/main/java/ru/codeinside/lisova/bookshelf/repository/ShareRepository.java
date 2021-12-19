package ru.codeinside.lisova.bookshelf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.codeinside.lisova.bookshelf.model.entity.Share;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShareRepository extends JpaRepository<Share, Long> {

    List<Share> findByDateEndBefore(LocalDate date);

}
