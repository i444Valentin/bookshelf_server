package ru.codeinside.lisova.bookshelf.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.codeinside.lisova.bookshelf.enumerate.ActivationStatus;
import ru.codeinside.lisova.bookshelf.model.entity.ActivationCode;
import ru.codeinside.lisova.bookshelf.model.entity.User;
import ru.codeinside.lisova.bookshelf.repository.ActivationCodeRepository;
import ru.codeinside.lisova.bookshelf.repository.UserRepository;
import ru.codeinside.lisova.bookshelf.service.ActivationCodeService;
import ru.codeinside.lisova.bookshelf.service.EmailService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ActivationCodeServiceImpl implements ActivationCodeService {

    private final ActivationCodeRepository activationCodeRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Autowired
    public ActivationCodeServiceImpl(ActivationCodeRepository activationCodeRepository, UserRepository userRepository, EmailService emailService) {
        this.activationCodeRepository = activationCodeRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public void sendCode(String email) {
        Optional<ActivationCode> activationCode = activationCodeRepository.findByEmail(email);

        activationCode.ifPresentOrElse(code -> {
            code.setTimestamp(LocalDateTime.now());
            emailService.sendMessage(email, code.getCode());
        }, () -> {
            Integer code = (int) (1000 + Math.random() * 8999);
            activationCodeRepository.save(ActivationCode.builder()
                    .email(email)
                    .code(code)
                    .timestamp(LocalDateTime.now())
                    .build());

            emailService.sendMessage(email, code);
        });

    }

    @Override
    @Transactional
    public void acceptUser(Integer code, Long userId) {
        ActivationCode activationCode = activationCodeRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException(String.format("Пользователь не найден id = %d", userId)));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(String.format("Пользователь не найден id = %d", userId)));

        if (!user.getEmail().equals(activationCode.getEmail())) {
            throw new RuntimeException(
                    String.format("Код %d не соотвествует пользователю с id = %d", code, userId)
            );
        }

        user.setStatus(ActivationStatus.ACCEPTED);
        activationCodeRepository.deleteById(activationCode.getId());
    }

    @Override
    @Transactional
    public void deleteCode() {
        List<ActivationCode> codes = activationCodeRepository.findByTimestampBefore(LocalDateTime.now());

        System.out.printf("Код id = %s был удалён%n",
                codes.stream().peek(code -> activationCodeRepository.deleteById(code.getId()))
        );

//        for (ActivationCode code : codes) {
//            activationCodeRepository.deleteById(code.getId());
//            System.out.printf(("Код id = %d был удалён%n"), code.getId());
//        }
    }
}
