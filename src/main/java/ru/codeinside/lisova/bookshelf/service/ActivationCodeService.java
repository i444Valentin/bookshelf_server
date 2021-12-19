package ru.codeinside.lisova.bookshelf.service;

public interface ActivationCodeService {

    void sendCode(String email);

    void acceptUser(Integer code, Long userId);

    void deleteCode();
}
