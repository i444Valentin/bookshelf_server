package ru.codeinside.lisova.bookshelf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.codeinside.lisova.bookshelf.model.dto.request.UserRequestDto;
import ru.codeinside.lisova.bookshelf.model.dto.response.UserResponseDto;
import ru.codeinside.lisova.bookshelf.service.UserService;

@RestController
@RequestMapping("users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Page<UserResponseDto> getAll(Pageable pageable) {
        return userService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public UserResponseDto getById(@PathVariable(name = "id") Long id) {
        return userService.getById(id);
    }

    @PostMapping
    public UserResponseDto registration(@RequestBody UserRequestDto userDto) {
        return userService.registration(userDto);
    }

    @PutMapping("/{id}")
    public UserResponseDto update(@RequestParam Long requestId,
                                  @PathVariable(name = "id") Long userId,
                                  @RequestBody UserRequestDto userDto) {
        return userService.update(requestId, userId, userDto);
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable(name = "id") Long id) {
        userService.delete(id);
    }
}
