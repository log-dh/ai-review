package com.example.aireview.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({FileStorageProperties.class, OpenAiProperties.class})
public class AppConfig {
}
