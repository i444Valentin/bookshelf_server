package ru.codeinside.lisova.bookshelf.model.dto.response;

import lombok.*;
import ru.codeinside.lisova.bookshelf.model.entity.Bookmark;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkResponseDto {

    private Bookmark.UserBookId userBookId;

    private Long savePage;
}
