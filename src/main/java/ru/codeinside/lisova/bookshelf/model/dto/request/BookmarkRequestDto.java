package ru.codeinside.lisova.bookshelf.model.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkRequestDto {

    private Long userBookId;

    private Long savePage;
}
