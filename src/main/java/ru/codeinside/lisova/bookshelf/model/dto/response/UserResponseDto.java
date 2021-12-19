package ru.codeinside.lisova.bookshelf.model.dto.response;

import lombok.*;
import ru.codeinside.lisova.bookshelf.enumerate.ActivationStatus;
import ru.codeinside.lisova.bookshelf.model.dto.ShortDto;
import ru.codeinside.lisova.bookshelf.model.entity.Share;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long id;

    private String name;

    private String email;

    private String password;

    private ActivationStatus status;

    private List<ShortDto> shelves;

    private List<ShortDto> books;

    private List<ShareResponseDto> shares;

    private List<ShareResponseDto> myShares;
}
