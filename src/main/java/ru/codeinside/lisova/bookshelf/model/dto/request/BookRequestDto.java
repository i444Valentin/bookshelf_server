package ru.codeinside.lisova.bookshelf.model.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDto {

    private String title;

    private String content;

    private Long userId;
}
