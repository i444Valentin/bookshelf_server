package com.diplome.bookshelf.service.impl;

import com.diplome.bookshelf.model.dto.ShortDto;
import com.diplome.bookshelf.model.dto.request.UserRequestDto;
import com.diplome.bookshelf.model.dto.response.ShareResponseDto;
import com.diplome.bookshelf.model.dto.response.UserResponseDto;
import com.diplome.bookshelf.model.entity.Book;
import com.diplome.bookshelf.model.entity.Share;
import com.diplome.bookshelf.model.entity.Shelf;
import com.diplome.bookshelf.model.entity.User;
import com.diplome.bookshelf.repository.ShelfRepository;
import com.diplome.bookshelf.repository.UserRepository;
import com.diplome.bookshelf.service.ActivationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.diplome.bookshelf.enumerate.ActivationStatus;
import com.diplome.bookshelf.repository.BookRepository;
import com.diplome.bookshelf.repository.ShareRepository;
import com.diplome.bookshelf.service.UserService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ShelfRepository shelfRepository;
    private final ShareRepository shareRepository;
    private final ActivationCodeService activationCodeService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BookRepository bookRepository,
                           ShelfRepository shelfRepository, ShareRepository shareRepository,
                           ActivationCodeService activationCodeService) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.shelfRepository = shelfRepository;
        this.shareRepository = shareRepository;
        this.activationCodeService = activationCodeService;
    }

    @Override
    @Transactional
    public Page<UserResponseDto> getAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    @Transactional
    public UserResponseDto getById(Long id) {
        return userRepository.findById(id).map(this::toDto)
                .orElseThrow(() -> new RuntimeException(String.format("???????????????????????? ???? ???????????? id = %d", id)));
    }

    @Override
    @Transactional
    public UserResponseDto registration(UserRequestDto userDto) {
        if (userDto.getEmail().isEmpty() || userDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("?????????????????????? ???????? ?????????? ???????????? ?????? ????????????");
        }

        userRepository.findByEmail(userDto.getEmail())
                .ifPresent(user -> {
                    throw new RuntimeException(
                            String.format("???????????????????????? ?? email = %s  ?????? ???????? ?? ??????????????", userDto.getEmail())
                    );
                });

        User user = toEntity(userDto);
        user.setStatus(ActivationStatus.NO_ACCEPTED);
        User savedUser = userRepository.save(user);

        activationCodeService.sendCode(userDto.getEmail());

        return toDto(savedUser);
    }

    @Override
    @Transactional
    public UserResponseDto update(Long requestId, Long userId, UserRequestDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(String.format("???????????????????????? ???? ???????????? id = %d", userId)));

        if (!requestId.equals(user.getId())) {
            throw new IllegalArgumentException(
                    String.format("???????????????????????? id = %d ???? ?????????? ?????????????? ?? ???????????????????????? id = %d", requestId, userId)
            );
        }

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        return toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        var user = getById(id);

        List<ShareResponseDto> sharesShortDtoList = Optional.of(user).map(UserResponseDto::getMyShares)
                .orElse(new ArrayList<>());

        if (!sharesShortDtoList.isEmpty()) {
            List<Share> myShares = sharesShortDtoList.stream().map(shareResponseDto ->
                    Share.builder()
                            .id(shareResponseDto.getId())
                            .dateEnd(shareResponseDto.getDateEnd())
                            .type(shareResponseDto.getType())
                            .book(Book.builder()
                                    .id(shareResponseDto.getBook().getId())
                                    .title(shareResponseDto.getBook().getName())
                                    .build())
                            .receiver(User.builder()
                                    .id(shareResponseDto.getReceiving().getId())
                                    .name(shareResponseDto.getReceiving().getName())
                                    .build())
                            .owner(User.builder()
                                    .id(shareResponseDto.getOwner().getId())
                                    .name(shareResponseDto.getOwner().getName())
                                    .build())
                            .build()).collect(Collectors.toList());

            shareRepository.deleteAll(myShares);
        }


        List<ShortDto> booksShortDtoList = Optional.of(user).map(UserResponseDto::getBooks)
                .orElse(new ArrayList<>());

        if (!booksShortDtoList.isEmpty()) {
            List<Book> userBooks = booksShortDtoList.stream().map(shortDto ->
                    Book.builder()
                            .id(shortDto.getId())
                            .title(shortDto.getName())
                            .build()).collect(Collectors.toList());

            bookRepository.deleteAll(userBooks);
        }


        List<ShortDto> shelvesShortDtoList = Optional.of(user).map(UserResponseDto::getShelves)
                .orElse(new ArrayList<>());

        if (!shelvesShortDtoList.isEmpty()) {
            List<Shelf> userShelves = shelvesShortDtoList.stream().map(shortDto ->
                    Shelf.builder()
                            .id(shortDto.getId())
                            .name(shortDto.getName())
                            .build()).collect(Collectors.toList());

            shelfRepository.deleteAll(userShelves);
        }

        userRepository.deleteById(user.getId());
    }

    private User toEntity(UserRequestDto userRequestDto) {
        return User.builder()
                .name(userRequestDto.getName())
                .email(userRequestDto.getEmail())
                .password(userRequestDto.getPassword())
                .build();
    }

    private UserResponseDto toDto(User user) {
        UserResponseDto.UserResponseDtoBuilder builder = UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .status(user.getStatus());

        List<Shelf> shelves = Optional.of(user)
                .map(User::getShelves)
                .orElse(new ArrayList<>());
        if (!shelves.isEmpty()) {
            List<ShortDto> shelvesShortDtoList = shelves.stream().map(shelf ->
                    ShortDto.builder()
                            .id(shelf.getId())
                            .name(shelf.getName())
                            .build()).collect(Collectors.toList());

            builder.shelves(shelvesShortDtoList);
        }

        List<Book> books = Optional.of(user)
                .map(User::getBooks)
                .orElse(new ArrayList<>());
        if (!books.isEmpty()) {
            List<ShortDto> booksShortDtoList = books.stream().map(book ->
                    ShortDto.builder()
                            .id(book.getId())
                            .name(book.getTitle())
                            .build()).collect(Collectors.toList());

            builder.books(booksShortDtoList);
        }

        List<Share> shares = Optional.of(user).map(User::getShares)
                .orElse(new ArrayList<>());
        if (!shares.isEmpty()) {
            List<ShareResponseDto> sharesShortDtoList = shares.stream().map(share ->
                    ShareResponseDto.builder()
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
                            .build()).collect(Collectors.toList());

            builder.shares(sharesShortDtoList);
        }

        List<Share> myShares = Optional.of(user).map(User::getMyShares)
                .orElse(new ArrayList<>());
        if (!myShares.isEmpty()) {
            List<ShareResponseDto> mySharesShortDtoList = myShares.stream().map(share ->
                    ShareResponseDto.builder()
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
                            .build()).collect(Collectors.toList());

            builder.myShares(mySharesShortDtoList);
        }

        return builder.build();
    }
}
