package ru.codeinside.lisova.bookshelf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.codeinside.lisova.bookshelf.model.dto.request.ShelfRequestDto;
import ru.codeinside.lisova.bookshelf.model.dto.response.ShelfResponseDto;
import ru.codeinside.lisova.bookshelf.service.ShelfService;

@RestController
@RequestMapping("shelves")
public class ShelfController {
    private final ShelfService shelfService;

    @Autowired
    public ShelfController(ShelfService shelfService) {
        this.shelfService = shelfService;
    }

    @GetMapping
    public Page<ShelfResponseDto> getAll(Pageable pageable) {
        return shelfService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public ShelfResponseDto getById(@PathVariable(name = "id") Long id) {
        return shelfService.getById(id);
    }

    @PostMapping
    public ShelfResponseDto create(@RequestBody ShelfRequestDto shelfDto) {
        return shelfService.create(shelfDto);
    }

    @PutMapping("/{id}")
    public ShelfResponseDto update(@PathVariable(name = "id") Long id,
                                   @RequestBody ShelfRequestDto shelfDto,
                                   @RequestParam Long userId) {
        return shelfService.update(id, shelfDto, userId);
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable(name = "id") Long shelfId,
                       @RequestParam Long userId) {
        shelfService.delete(shelfId, userId);
    }
}
