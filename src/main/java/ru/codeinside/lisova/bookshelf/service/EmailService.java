package ru.codeinside.lisova.bookshelf.service;

import ru.codeinside.lisova.bookshelf.enumerate.UseType;

import java.time.LocalDate;

public interface EmailService {

    void sendMessage(String email, Integer code);

    void sendShareRequest(String ownerEmail, String receivingEmail, String book, UseType type, LocalDate date);
}
