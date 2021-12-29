package ru.codeinside.lisova.bookshelf.model.entity;

import lombok.*;
import ru.codeinside.lisova.bookshelf.enumerate.UseType;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shares")
public class Share {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_end")
    private LocalDate dateEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private UseType type;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "receiving_id") //TODO rename
    private User receiving;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}
