package gzs.fiar.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Value("${server.base-url}")
    private String serverBaseUrl;

    @Value("${server.port}")
    private int serverPort;

    @Bean
    public String serverAddress() {

        return serverBaseUrl + ":" + serverPort;
    }
}
