package com.diplome.bookshelf.service;

import com.diplome.bookshelf.model.dto.request.UserRequestDto;
import com.diplome.bookshelf.model.dto.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<UserResponseDto> getAll(Pageable pageable);

    UserResponseDto getById(Long id);

    UserResponseDto registration(UserRequestDto userDto);

    UserResponseDto update(Long requestId, Long userId, UserRequestDto userDto);

    void delete(Long id);
}
