package spoon.login.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import spoon.login.entity.Login;
import spoon.login.repository.LoginRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class LoginEvent {

    private final LoginRepository loginRepository;

    @EventListener
    public void loginEvent(Login login) {
        try {
            loginRepository.save(login);
        } catch (DataAccessException e) {
            log.error("로그인을 기록하지 못하였습니다.", e);
        }
    }

}
