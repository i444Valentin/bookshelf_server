package ru.codeinside.lisova.bookshelf.model.dto.response;

import lombok.*;
import ru.codeinside.lisova.bookshelf.model.dto.ShortDto;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShelfResponseDto {

    private Long id;

    private String name;

    private ShortDto user;

    private List<ShortDto> books;
}
