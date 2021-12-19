package ru.codeinside.lisova.bookshelf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.codeinside.lisova.bookshelf.model.entity.ActivationCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivationCodeRepository extends JpaRepository<ActivationCode, Long> {

    Optional<ActivationCode> findByEmail(String email);

    Optional<ActivationCode> findByCode(Integer code);

    List<ActivationCode> findByTimestampBefore(LocalDateTime localDateTime);
}
