package com.example.poc.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class PocPlatformAutoConfiguration {

    @Bean
    PlatformInfo platformInfo() {
        return new PlatformInfo("POC Spring Boot Platform", "1.0.0");
    }
}
