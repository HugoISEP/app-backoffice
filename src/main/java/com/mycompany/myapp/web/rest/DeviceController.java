package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.DeviceService;
import lombok.RequiredArgsConstructor;
import com.mycompany.myapp.service.UserService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Hidden
@RestController
@RequestMapping("api/device")
@RequiredArgsConstructor
public class DeviceController {

    @Value("${mobile-app.version}")
    private String appVersion;

    private final DeviceService deviceService;

    @GetMapping(value = "/version", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> getAppVersion(){
        return Collections.singletonMap("version", appVersion);
    }

    @PutMapping("/subscribe/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\") or hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public void subscribeToTopic(@PathVariable Long id) {
        log.debug("REST request to subscribeToTopic : {}", id);
        deviceService.subscribeUserToATopic(id);
    }

    @PutMapping("/unsubscribe/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\") or hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public void unsubscribeToTopic(@PathVariable Long id) {
        log.debug("REST request to unsubscribeToTopic : {}", id);
        deviceService.unsubscribeUserFromATopic(id);
    }
}
