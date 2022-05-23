package com.diplome.bookshelf.model.dto.response;

import com.diplome.bookshelf.model.dto.ShortDto;
import lombok.*;

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
