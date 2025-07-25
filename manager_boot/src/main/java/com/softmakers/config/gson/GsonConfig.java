package com.softmakers.config.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Timestamp;

@Configuration
public class GsonConfig {

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter())
                .create();
    }
}
