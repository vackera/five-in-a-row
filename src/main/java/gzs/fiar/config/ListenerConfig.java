package gzs.fiar.config;

import gzs.fiar.event.ActiveSessionListener;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ListenerConfig {

    @Bean
    public ServletListenerRegistrationBean<ActiveSessionListener> sessionListener() {

        return new ServletListenerRegistrationBean<>(new ActiveSessionListener());
    }
}