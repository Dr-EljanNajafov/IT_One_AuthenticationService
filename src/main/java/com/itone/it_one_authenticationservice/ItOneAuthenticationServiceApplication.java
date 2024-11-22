package com.itone.it_one_authenticationservice;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ItOneAuthenticationServiceApplication {

    public static void main(String[] args) {
            new SpringApplicationBuilder(ItOneAuthenticationServiceApplication.class)
                    .bannerMode(Banner.Mode.OFF)
                    .run(args);
    }
}
