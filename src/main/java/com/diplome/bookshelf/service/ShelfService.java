package com.diplome.bookshelf.service;

import com.diplome.bookshelf.model.dto.request.ShelfRequestDto;
import com.diplome.bookshelf.model.dto.response.ShelfResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShelfService {

    Page<ShelfResponseDto> getAll(Pageable pageable);

    ShelfResponseDto getById(Long id);

    ShelfResponseDto create(ShelfRequestDto shelfDto);

    ShelfResponseDto update(Long id, ShelfRequestDto shelfDto, Long userId);

    void delete(Long id, Long userId);
}
