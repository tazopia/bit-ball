package spoon.member.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.member.domain.LoginDto;
import spoon.member.entity.LoginHistory;

public interface LoginHistoryService {

    void addHistory();

    Page<LoginHistory> list(LoginDto.Command command, Pageable pageable);

}
