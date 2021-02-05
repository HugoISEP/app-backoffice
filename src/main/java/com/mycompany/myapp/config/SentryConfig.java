package com.mycompany.myapp.config;

import io.sentry.spring.EnableSentry;
import org.springframework.context.annotation.Configuration;

@EnableSentry(sendDefaultPii = true)
@Configuration
public class SentryConfig {
}
