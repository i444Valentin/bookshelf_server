package com.diplome.bookshelf.model.dto.response;

import com.diplome.bookshelf.model.dto.ShortDto;
import lombok.*;
import com.diplome.bookshelf.enumerate.ActivationStatus;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long id;

    private String name;

    private String email;

    private String password;

    private ActivationStatus status;

    private List<ShortDto> shelves;

    private List<ShortDto> books;

    private List<ShareResponseDto> shares;

    private List<ShareResponseDto> myShares;
}
