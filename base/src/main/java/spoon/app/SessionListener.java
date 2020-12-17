package spoon.app;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@Slf4j
public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        se.getSession().setMaxInactiveInterval(30 * 60);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {

    }
}
