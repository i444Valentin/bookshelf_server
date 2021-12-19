package ru.codeinside.lisova.bookshelf.model.dto.response;

import lombok.*;
import ru.codeinside.lisova.bookshelf.model.dto.ShortDto;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDto {

    private Long id;

    private String title;

    private String content;

    private ShortDto user;

    private ShortDto shelf;

    private List<ShortDto> shares;
}
