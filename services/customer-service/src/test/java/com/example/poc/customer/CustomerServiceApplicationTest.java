package com.example.poc.customer;

import com.example.poc.autoconfigure.PlatformInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CustomerServiceApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void loadsApplicationContext() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    void providesPlatformInfo() {
        PlatformInfo platformInfo = applicationContext.getBean(PlatformInfo.class);

        assertThat(platformInfo.name()).isEqualTo("POC Spring Boot Platform");
        assertThat(platformInfo.version()).isEqualTo("1.0.0");
    }
}
