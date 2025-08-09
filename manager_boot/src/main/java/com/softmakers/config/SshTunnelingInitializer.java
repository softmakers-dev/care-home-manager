package com.softmakers.config;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import jakarta.annotation.PreDestroy;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Slf4j
@Component
@ConfigurationProperties(prefix = "ssh")
@Getter
@Setter
public class SshTunnelingInitializer {

    @NotNull
    private String remote_jump_host;   // SSH server hostname or IP
    @NotNull
    private String user;             // SSH username
    @NotNull
    private int ssh_port;             // SSH port, typically 22
    @NotNull
    private String private_key_path;       // Path to PEM private key
    @NotNull
    private int database_port;        // Remote database port, e.g. 3306 for MySQL
    @NotNull
    private String database_url;
    private int localPort = 0;       // Local port assigned (0 = auto)

    private Session session;

    /**
     * Establishes SSH session and creates local port forwarding for the database.
     * @return the assigned local port number on which DB is forwarded
     */
    public int openSshTunnel() {
        try {
            JSch jsch = new JSch();
            jsch.addIdentity(private_key_path);

            session = jsch.getSession(user, remote_jump_host, ssh_port);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no"); // Disable host key checking for convenience; consider security implications
            session.setConfig(config);

            session.connect();

            // Setup port forwarding: localPort 0 means JSch picks an available port
            localPort = session.setPortForwardingL(0, database_url, database_port);

            log.info("SSH tunnel established. Forwarded local port {} to {}:{}", localPort, database_port);

            return localPort;
        } catch (Exception e) {
            log.error("Failed to establish SSH tunnel", e);
            throw new RuntimeException("Could not open SSH tunnel", e);
        }
    }

    @PreDestroy
    public void closeSshTunnel() {
        if (session != null && session.isConnected()) {
            session.disconnect();
            log.info("SSH tunnel closed");
        }
    }
}
