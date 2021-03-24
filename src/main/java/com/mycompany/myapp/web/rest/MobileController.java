package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.MobileService;
import com.mycompany.myapp.service.UserService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/mobile")
public class MobileController {

    @Value("${mobile-app.version}")
    private String appVersion;

    private final MobileService mobileService;

    private final UserService userService;

    public MobileController(MobileService mobileService, UserService userService) {
        this.mobileService = mobileService;
        this.userService = userService;
    }

    @GetMapping("/version")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> getAppVersion(){
        return Collections.singletonMap("version", appVersion);
    }

    @PutMapping("/subscribe/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\") or hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public void subscribeToTopic(@PathVariable Long id) {
        log.debug("REST request to subscribeToTopic : {}", id);
        User currentUser = userService.getUserWithAuthorities()
            .orElseThrow(() -> new BadRequestAlertException("User not found", "USER", "wrong id"));
        mobileService.subscribeUserToATopic(currentUser, id);
    }

    @PutMapping("/unsubscribe/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\") or hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public void unsubscribeToTopic(@PathVariable Long id) {
        log.debug("REST request to unsubscribeToTopic : {}", id);
        User currentUser = userService.getUserWithAuthorities()
            .orElseThrow(() -> new BadRequestAlertException("User not found", "USER", "wrong id"));
        mobileService.unsubscribeUserToATopic(currentUser, id);
    }
}
