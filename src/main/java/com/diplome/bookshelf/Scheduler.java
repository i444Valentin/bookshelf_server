package com.diplome.bookshelf;

import com.diplome.bookshelf.service.ActivationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.diplome.bookshelf.service.ShareService;

@Component
public class Scheduler {

    private final ShareService shareService;
    private final ActivationCodeService activationCodeService;

    @Autowired
    public Scheduler(ShareService shareService, ActivationCodeService activationCodeService) {
        this.shareService = shareService;
        this.activationCodeService = activationCodeService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteShare() {
        shareService.deleteShare();
    }

    @Scheduled(cron = "0 */5 * ? * *")
    public void deleteCode() {
        activationCodeService.deleteCode();
    }
}
