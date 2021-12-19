package ru.codeinside.lisova.bookshelf.model.dto.response;

import lombok.*;
import ru.codeinside.lisova.bookshelf.enumerate.UseType;
import ru.codeinside.lisova.bookshelf.model.dto.ShortDto;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareResponseDto {

    private Long id;

    private LocalDate dateEnd;

    private UseType type;

    private ShortDto book;

    private ShortDto receiving;

    private ShortDto owner;
}
