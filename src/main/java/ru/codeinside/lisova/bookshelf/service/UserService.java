package ru.codeinside.lisova.bookshelf.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.codeinside.lisova.bookshelf.model.dto.request.UserRequestDto;
import ru.codeinside.lisova.bookshelf.model.dto.response.UserResponseDto;

public interface UserService {

    Page<UserResponseDto> getAll(Pageable pageable);

    UserResponseDto getById(Long id);

    UserResponseDto registration(UserRequestDto userDto);

    UserResponseDto update(Long requestId, Long userId, UserRequestDto userDto);

    void delete(Long id);
}
