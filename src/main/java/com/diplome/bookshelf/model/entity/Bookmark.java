package com.diplome.bookshelf.model.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookmark")
public class Bookmark {

    @EmbeddedId
    private UserBookId userBookId;

    @Column(name = "save_page")
    private Long savePage;

    @Embeddable
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserBookId implements Serializable {

        @Column(name = "user_id")
        private Long userId;

        @Column(name = "book_id")
        private Long bookId;
    }
}
