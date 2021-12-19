package ru.codeinside.lisova.bookshelf.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.codeinside.lisova.bookshelf.enumerate.ActivationStatus;
import ru.codeinside.lisova.bookshelf.model.dto.ShortDto;
import ru.codeinside.lisova.bookshelf.model.dto.request.UserRequestDto;
import ru.codeinside.lisova.bookshelf.model.dto.response.ShareResponseDto;
import ru.codeinside.lisova.bookshelf.model.dto.response.UserResponseDto;
import ru.codeinside.lisova.bookshelf.model.entity.Book;
import ru.codeinside.lisova.bookshelf.model.entity.Share;
import ru.codeinside.lisova.bookshelf.model.entity.Shelf;
import ru.codeinside.lisova.bookshelf.model.entity.User;
import ru.codeinside.lisova.bookshelf.repository.BookRepository;
import ru.codeinside.lisova.bookshelf.repository.ShareRepository;
import ru.codeinside.lisova.bookshelf.repository.ShelfRepository;
import ru.codeinside.lisova.bookshelf.repository.UserRepository;
import ru.codeinside.lisova.bookshelf.service.ActivationCodeService;
import ru.codeinside.lisova.bookshelf.service.UserService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                .orElseThrow(() -> new RuntimeException(String.format("Пользователь не найден id = %d", id)));
    }

    @Override
    @Transactional
    public UserResponseDto registration(UserRequestDto userDto) {
        if (userDto.getEmail().isEmpty() || userDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Незаполнены поля ввода логина или пароля");
        }

        userRepository.findByEmail(userDto.getEmail())
                .ifPresent(user -> {
                    throw new RuntimeException(String.format("Пользователь с email = %s  уже есть в системе", userDto.getEmail()));
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
                .orElseThrow(() -> new RuntimeException(String.format("Пользователь не найден id = %d", userId)));

        if (!requestId.equals(user.getId())) {
            throw new IllegalArgumentException(
                    String.format("Пользователь id = %d не имеет доступа к пользователю id = %d", requestId, userId)
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

        List<ShareResponseDto> sharesShortDtoList = Optional.ofNullable(user.getMyShares())
                .orElseThrow(() -> new RuntimeException("Share у пользователя не найдны"));

        List<Share> myShares = new ArrayList<>();

        if (sharesShortDtoList != null) {
            for (ShareResponseDto shareDto : sharesShortDtoList) {
                myShares.add(Share.builder()
                        .id(shareDto.getId())
                        .dateEnd(shareDto.getDateEnd())
                        .type(shareDto.getType())
                        .book(Book.builder()
                                .id(shareDto.getBook().getId())
                                .title(shareDto.getBook().getName())
                                .build())
                        .receiving(User.builder()
                                .id(shareDto.getReceiving().getId())
                                .name(shareDto.getReceiving().getName())
                                .build())
                        .owner(User.builder()
                                .id(shareDto.getOwner().getId())
                                .name(shareDto.getOwner().getName())
                                .build())
                        .build());
            }
        }

        List<ShortDto> booksShortDtoList = Optional.ofNullable(user.getBooks())
                .orElseThrow(() -> new RuntimeException("Книги у пользователя не найдены"));
        List<Book> userBooks = new ArrayList<>();

        if (booksShortDtoList != null) {
            for (ShortDto shortDto : booksShortDtoList) {
                userBooks.add(Book.builder()
                        .id(shortDto.getId())
                        .title(shortDto.getName())
                        .build());
            }
        }

        List<ShortDto> shelvesShortDtoList = Optional.ofNullable(user.getShelves())
                .orElseThrow(() -> new RuntimeException("Полки у пользователя не найдены"));
        List<Shelf> userShelves = new ArrayList<>();

        if (shelvesShortDtoList != null) {
            for (ShortDto shortDto : shelvesShortDtoList) {
                userShelves.add(Shelf.builder()
                        .id(shortDto.getId())
                        .name(shortDto.getName())
                        .build());
            }
        }

        shareRepository.deleteAll(myShares);
        bookRepository.deleteAll(userBooks);
        shelfRepository.deleteAll(userShelves);
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

        List<Shelf> shelves = Optional.ofNullable(user.getShelves())
                .orElseThrow(() -> new RuntimeException("Полки у пользователя не найдены"));

        List<ShortDto> shelvesShortDtoList = new ArrayList<>();

        if (shelves != null) {
            for (Shelf shelf : shelves) {
                shelvesShortDtoList.add(ShortDto.builder()
                        .id(shelf.getId())
                        .name(shelf.getName())
                        .build());
            }
        }

        List<Book> books = Optional.ofNullable(user.getBooks())
                .orElseThrow(() -> new RuntimeException("Книги у пользователя не найдены"));
        List<ShortDto> booksShortDtoList = new ArrayList<>();

        if (books != null) {
            for (Book book : books) {
                booksShortDtoList.add(ShortDto.builder()
                        .id(book.getId())
                        .name(book.getTitle())
                        .build());
            }
        }

        List<Share> shares = Optional.ofNullable(user.getShares())
                .orElseThrow(() -> new RuntimeException("Share у пользователя не найдны"));

        List<ShareResponseDto> sharesShortDtoList = new ArrayList<>();

        if (shares != null) {
            for (Share share : shares) {
                sharesShortDtoList.add(ShareResponseDto.builder()
                        .id(share.getId())
                        .dateEnd(share.getDateEnd())
                        .type(share.getType())
                        .book(ShortDto.builder()
                                .id(share.getBook().getId())
                                .name(share.getBook().getTitle())
                                .build())
                        .receiving(ShortDto.builder()
                                .id(share.getReceiving().getId())
                                .name(share.getReceiving().getName())
                                .build())
                        .owner(ShortDto.builder()
                                .id(share.getOwner().getId())
                                .name(share.getOwner().getName())
                                .build())
                        .build());
            }
        }

        List<Share> myShares = Optional.ofNullable(user.getMyShares())
                .orElseThrow(() -> new RuntimeException("Собственные Share у пользователя не найдны"));
        List<ShareResponseDto> mySharesShortDtoList = new ArrayList<>();

        if (myShares != null) {
            for (Share share : myShares) {
                mySharesShortDtoList.add(ShareResponseDto.builder()
                        .id(share.getId())
                        .dateEnd(share.getDateEnd())
                        .type(share.getType())
                        .book(ShortDto.builder()
                                .id(share.getBook().getId())
                                .name(share.getBook().getTitle())
                                .build())
                        .receiving(ShortDto.builder()
                                .id(share.getReceiving().getId())
                                .name(share.getReceiving().getName())
                                .build())
                        .owner(ShortDto.builder()
                                .id(share.getOwner().getId())
                                .name(share.getOwner().getName())
                                .build())
                        .build());
            }
        }

        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .status(user.getStatus())
                .shelves(shelvesShortDtoList)
                .books(booksShortDtoList)
                .shares(sharesShortDtoList)
                .myShares(mySharesShortDtoList)
                .build();
    }
}
