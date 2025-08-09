package com.softmakers.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Slf4j
@ConfigurationProperties(prefix = "spring.datasource")
@Configuration
@RequiredArgsConstructor
public class SshDataSourceConfig {

    private final SshTunnelingInitializer sshTunnelingInitializer;

    @Bean("dataSource")
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {

        int forwardedPort = sshTunnelingInitializer.openSshTunnel();
        String forwardedUrl = properties.getUrl().replace("[forwardedPort]", String.valueOf(forwardedPort));
        log.info("ssh_url: {}", forwardedUrl);

        return DataSourceBuilder.create()
                .url(forwardedUrl)                         // e.g., jdbc:mariadb://localhost:<ssh_tunnel_local_port>/dbname
                .username(properties.getUsername())
                .password(properties.getPassword())
                .driverClassName(properties.getDriverClassName())
                .build();
    }
}
