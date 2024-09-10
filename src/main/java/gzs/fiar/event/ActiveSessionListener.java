package gzs.fiar.event;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
@Getter
public class ActiveSessionListener implements HttpSessionListener {

    private final Set<String> activeSessions = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void sessionCreated(HttpSessionEvent event) {

        activeSessions.add(event.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {

        activeSessions.remove(event.getSession().getId());
    }

}
