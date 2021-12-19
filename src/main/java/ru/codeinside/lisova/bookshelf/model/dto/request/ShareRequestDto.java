package ru.codeinside.lisova.bookshelf.model.dto.request;

import lombok.*;
import ru.codeinside.lisova.bookshelf.enumerate.UseType;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ShareRequestDto {

    private UseType type;

    private LocalDate dateEnd;

    private Long bookId;

    private Long receivingId;

    private Long ownerId;
}
