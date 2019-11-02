package com.lnu.foundation;

import com.lnu.foundation.config.RepositoryRestConfig;
import com.lnu.foundation.config.SecurityConfig;
import com.lnu.foundation.config.WebConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
public class Assignment3Application {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Assignment3Application.class, WebConfig.class, SecurityConfig.class, RepositoryRestConfig.class).run(args);
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
