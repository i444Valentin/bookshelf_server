package com.diplome.bookshelf.service.impl;

import com.diplome.bookshelf.model.dto.ShortDto;
import com.diplome.bookshelf.model.dto.request.ShareRequestDto;
import com.diplome.bookshelf.model.entity.Book;
import com.diplome.bookshelf.model.entity.Share;
import com.diplome.bookshelf.repository.UserRepository;
import com.diplome.bookshelf.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.diplome.bookshelf.enumerate.UseType;
import com.diplome.bookshelf.model.dto.response.ShareResponseDto;
import com.diplome.bookshelf.model.entity.User;
import com.diplome.bookshelf.repository.BookRepository;
import com.diplome.bookshelf.repository.ShareRepository;
import com.diplome.bookshelf.service.ShareService;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShareServiceImpl implements ShareService {

    private final ShareRepository shareRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final EmailService emailService;

    @Autowired
    public ShareServiceImpl(ShareRepository shareRepository, UserRepository userRepository,
                            BookRepository bookRepository, EmailService emailService) {
        this.shareRepository = shareRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public Page<ShareResponseDto> getAll(Pageable pageable) {
        return shareRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    @Transactional
    public ShareResponseDto getById(Long id) {
        return shareRepository.findById(id).map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Share ???? ???????????? id = " + id));
    }

    @Override
    @Transactional
    public ShareResponseDto update(Long id, ShareRequestDto shareDto) {
        Share share = shareRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Share ???? ???????????? id = " + id));

        if (shareDto.getType() == UseType.PERMANENT) {
            share.setType(UseType.PERMANENT);
            share.setDateEnd(null);
        } else {
            share.setType(UseType.TEMPORARY);

            Optional.ofNullable(shareDto.getDateEnd())
                    .orElseThrow(() -> new RuntimeException("?????????????????????? ???????? ?????? ???????? TEMPORARY"));

            share.setDateEnd(shareDto.getDateEnd());
        }

        return toDto(shareRepository.save(share));
    }

    @Override
    @Transactional
    public void requestShare(Long requesterId, ShareRequestDto shareRequestDto) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException(String.format("???????????????????????? ???? ???????????? id = %d", requesterId)));

        User owner = userRepository.findById(shareRequestDto.getOwnerId())
                .orElseThrow(() -> new RuntimeException(
                        String.format("???????????????? ???? ???????????? id = %d", shareRequestDto.getOwnerId()))
                );

        Book book = bookRepository.findByIdAndUser_Id(shareRequestDto.getBookId(), owner.getId())
                .orElseThrow(() -> new RuntimeException(
                        String.format("?????????? id = %d ???? ?????????????? ?????? ???? ?????????????????????? ?????????????????????? id = %d",
                                shareRequestDto.getBookId(), owner.getId()))
                );

        Optional.of(shareRequestDto).filter(dto ->
                        dto.getType().equals(UseType.TEMPORARY) && Objects.nonNull(dto.getDateEnd()))
                .orElseThrow(() -> new RuntimeException("?????????????????????? ???????? ?????? ???????? TEMPORARY"));

        emailService.sendShareRequest(owner.getEmail(), requester.getEmail(),
                book.getTitle(), shareRequestDto.getType(), shareRequestDto.getDateEnd());
    }


    @Override
    @Transactional
    public ShareResponseDto shareBook(ShareRequestDto shareRequestDto) {
        Share share = toEntity(shareRequestDto);

        if (shareRequestDto.getType() == UseType.PERMANENT) {
            share.setDateEnd(null);
        } else {
            Optional.ofNullable(shareRequestDto.getDateEnd())
                    .orElseThrow(() -> new RuntimeException("?????????????????????? ???????? ?????? ???????? TEMPORARY"));

            share.setDateEnd(shareRequestDto.getDateEnd());
        }
        return toDto(shareRepository.save(share));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        var share = getById(id);
        shareRepository.deleteById(share.getId());
    }

    @Override
    @Transactional
    public void deleteShare() {
        List<Share> shares = shareRepository.findByDateEndBefore(LocalDate.now());

        System.out.printf("???????????? share ?????????? ???????????? %s",
                shares.stream()
                        .map(Share::getId)
                        .collect(Collectors.toList())
        );

        shares.forEach(shareRepository::delete);
    }

    private Share toEntity(ShareRequestDto shareRequestDto) {
        return Share.builder()
                .dateEnd(shareRequestDto.getDateEnd())
                .type(shareRequestDto.getType())
                .book(Book.builder().id(shareRequestDto.getBookId()).build())
                .receiver(User.builder().id(shareRequestDto.getReceivingId()).build())
                .owner(User.builder().id(shareRequestDto.getOwnerId()).build())
                .build();
    }

    private ShareResponseDto toDto(Share share) {
        return ShareResponseDto.builder()
                .id(share.getId())
                .dateEnd(share.getDateEnd())
                .type(share.getType())
                .book(ShortDto.builder()
                        .id(share.getBook().getId())
                        .name(share.getBook().getTitle())
                        .build())
                .receiving(ShortDto.builder()
                        .id(share.getReceiver().getId())
                        .name(share.getReceiver().getName())
                        .build())
                .owner(ShortDto.builder()
                        .id(share.getOwner().getId())
                        .name(share.getOwner().getName())
                        .build())
                .build();
    }
}
