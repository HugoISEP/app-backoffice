package com.mycompany.myapp.web.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/mobile")
public class MobileController {

    @Value("${mobile-app.version}")
    private String appVersion;

    @GetMapping("/version}")
    public String getAppVersion(){
        return appVersion;
    }
}
