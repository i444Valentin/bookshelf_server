package com.diplome.bookshelf.service;

import com.diplome.bookshelf.model.dto.request.ShareRequestDto;
import com.diplome.bookshelf.model.dto.response.ShareResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShareService {

    Page<ShareResponseDto> getAll(Pageable pageable);

    ShareResponseDto getById(Long id);

    ShareResponseDto update(Long id, ShareRequestDto shareDto);

    void requestShare(Long requesterId, ShareRequestDto shareRequestDto);

    ShareResponseDto shareBook(ShareRequestDto shareRequestDto);

    void delete(Long id);

    void deleteShare();
}
