package com.diplome.bookshelf.model.dto.response;

import lombok.*;
import com.diplome.bookshelf.model.dto.ShortDto;

import java.util.List;

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
