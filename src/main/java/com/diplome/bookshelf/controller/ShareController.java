package com.diplome.bookshelf.controller;

import com.diplome.bookshelf.model.dto.request.ShareRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import com.diplome.bookshelf.model.dto.response.ShareResponseDto;
import com.diplome.bookshelf.service.ShareService;

@RestController
@RequestMapping("/shares")
public class ShareController {
    private final ShareService shareService;

    @Autowired
    public ShareController(ShareService shareService) {
        this.shareService = shareService;
    }

    @GetMapping
    public Page<ShareResponseDto> getAll(Pageable pageable) {
        return shareService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public ShareResponseDto getById(@PathVariable(name = "id") Long id) {
        return shareService.getById(id);
    }

    @PutMapping("/{id}")
    public ShareResponseDto update(@PathVariable(name = "id") Long id, @RequestBody ShareRequestDto shareDto) {
        return shareService.update(id, shareDto);
    }

    @PutMapping("/request")
    public void requestShare(@RequestParam Long requesterId, @RequestBody ShareRequestDto shareRequestDto) {
        shareService.requestShare(requesterId, shareRequestDto);
    }

    @PostMapping
    public ShareResponseDto shareBook(@RequestBody ShareRequestDto shareRequestDto) {
        return shareService.shareBook(shareRequestDto);
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable(name = "id") Long id) {
        shareService.delete(id);
    }
}
