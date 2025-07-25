package com.softmakers.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "com.softmakers.manager_store.jpo")
@EnableJpaRepositories(basePackages = "com.softmakers.manager_store.repository")
public class JpaConfig {
}
