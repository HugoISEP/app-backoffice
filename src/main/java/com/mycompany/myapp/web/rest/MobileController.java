package com.mycompany.myapp.web.rest;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.MobileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/mobile")
public class MobileController {

    @Value("${mobile-app.version}")
    private String appVersion;

    private final MobileService mobileService;

    public MobileController(MobileService mobileService) {
        this.mobileService = mobileService;
    }

    @GetMapping("/version")
    @ResponseStatus(HttpStatus.OK)
    public String getAppVersion(){
        return appVersion;
    }

    @PutMapping("/subscribe/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\") or hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public void subscribeToTopic(@PathVariable Long id) throws FirebaseMessagingException {
        log.debug("REST request to subscribeToTopic : {}", id);
        mobileService.subscribeFirebaseTopic(id);
    }

    @PutMapping("/unsubscribe/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\") or hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public void unsubscribeToTopic(@PathVariable Long id) throws FirebaseMessagingException {
        log.debug("REST request to unsubscribeToTopic : {}", id);
        mobileService.unsubscribeFirebaseTopic(id);
    }
}
