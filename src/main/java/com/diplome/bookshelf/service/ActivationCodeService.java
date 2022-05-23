package com.diplome.bookshelf.service;

public interface ActivationCodeService {

    void sendCode(String email);

    void acceptUser(Integer code, Long userId);

    void deleteCode();
}
