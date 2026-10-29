package com.yuva.geopulse;

import com.yuva.geopulse.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class GeopulseApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeopulseApplication.class, args);
    }
}
