package ru.codeinside.lisova.bookshelf.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShortDto {

    private Long id;

    private String name;

    public static ShortDtoBuilder builder() {
        return new ShortDtoBuilder();
    }

    public static class ShortDtoBuilder {

        private Long id;

        private String name;

        public ShortDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ShortDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ShortDto build() {
            return new ShortDto(id, name);
        }
    }
}
