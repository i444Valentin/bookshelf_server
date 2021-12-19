package ru.codeinside.lisova.bookshelf.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.codeinside.lisova.bookshelf.model.dto.request.ShelfRequestDto;
import ru.codeinside.lisova.bookshelf.model.dto.response.ShelfResponseDto;

public interface ShelfService {

    Page<ShelfResponseDto> getAll(Pageable pageable);

    ShelfResponseDto getById(Long id);

    ShelfResponseDto create(ShelfRequestDto shelfDto);

    ShelfResponseDto update(Long id, ShelfRequestDto shelfDto, Long userId);

    void delete(Long id, Long userId);
}
