package ru.codeinside.lisova.bookshelf.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.codeinside.lisova.bookshelf.model.dto.request.ShareRequestDto;
import ru.codeinside.lisova.bookshelf.model.dto.response.ShareResponseDto;

import java.time.LocalDate;

public interface ShareService {

    Page<ShareResponseDto> getAll(Pageable pageable);

    ShareResponseDto getById(Long id);

    ShareResponseDto update(Long id, ShareRequestDto shareDto);

    void requestShare(Long requesterId, ShareRequestDto shareRequestDto);

    ShareResponseDto shareBook(ShareRequestDto shareRequestDto);

    void delete(Long id);

    void deleteShare();
}
