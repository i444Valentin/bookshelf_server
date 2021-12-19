package ru.codeinside.lisova.bookshelf.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.codeinside.lisova.bookshelf.enumerate.UseType;
import ru.codeinside.lisova.bookshelf.service.EmailService;

import java.time.LocalDate;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void sendMessage(String email, Integer code) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("Код активации");
        message.setText("Ваш код: " + code);

        emailSender.send(message);
    }

    @Override
    public void sendShareRequest(String ownerEmail, String receivingEmail, String book, UseType type, LocalDate date) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(ownerEmail);
        message.setSubject("Запрос на деление книгой");

        if (type == UseType.TEMPORARY) {
            message.setText(
                    String.format("Пользователь email = %s хочет взять вашу книгу %s на срок до %s",
                            receivingEmail, book, date)
            );
        } else {
            message.setText(
                    String.format("Пользователь email = %s хочет взять вашу книгу %s навсегда",
                            receivingEmail, book)
            );
        }

        emailSender.send(message);
    }
}
