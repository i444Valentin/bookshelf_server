package ru.codeinside.lisova.bookshelf.model.entity;

import lombok.*;
import ru.codeinside.lisova.bookshelf.enumerate.ActivationStatus;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ActivationStatus status;

    @OneToMany(mappedBy = "user")
    private List<Shelf> shelves = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Book> books = new ArrayList<>();

    @OneToMany(mappedBy = "receiving")
    private List<Share> shares = new ArrayList<>();//те, которые дали (чужие книги)

    @OneToMany(mappedBy = "owner")
    private List<Share> myShares = new ArrayList<>();//те, которые сам дал (мои книги)
}
