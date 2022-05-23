package com.diplome.bookshelf.controller;

import com.diplome.bookshelf.service.ActivationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/activation-code")
public class ActivationCodeController {

    private final ActivationCodeService activationCodeService;

    @Autowired
    public ActivationCodeController(ActivationCodeService activationCodeService) {
        this.activationCodeService = activationCodeService;
    }

    @PutMapping("/{code}")
    public void acceptUser(@PathVariable(name = "code") Integer code, @RequestParam Long userId) {
        activationCodeService.acceptUser(code, userId);
    }

    @PostMapping
    public void sendCode(@RequestParam String email) {
        activationCodeService.sendCode(email);
    }
}
