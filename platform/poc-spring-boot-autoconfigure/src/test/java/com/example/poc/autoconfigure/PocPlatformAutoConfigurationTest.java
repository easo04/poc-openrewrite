package com.example.poc.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class PocPlatformAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(PocPlatformAutoConfiguration.class));

    @Test
    void providesPlatformInfo() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(PlatformInfo.class);

            PlatformInfo platformInfo = context.getBean(PlatformInfo.class);
            assertThat(platformInfo.name()).isEqualTo("POC Spring Boot Platform");
            assertThat(platformInfo.version()).isEqualTo("1.0.0");
        });
    }
}
