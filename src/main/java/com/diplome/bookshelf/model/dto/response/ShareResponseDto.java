package com.diplome.bookshelf.model.dto.response;

import com.diplome.bookshelf.model.dto.ShortDto;
import lombok.*;
import com.diplome.bookshelf.enumerate.UseType;

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
