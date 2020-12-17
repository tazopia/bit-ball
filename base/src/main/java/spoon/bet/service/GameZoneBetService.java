package spoon.bet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spoon.bet.domain.ZoneBetDto;
import spoon.bet.repository.GameZoneBetDao;
import spoon.common.utils.WebUtils;
import spoon.game.domain.MenuCode;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GameZoneBetService {

    private final GameZoneBetDao gameZoneBetDao;

    public List<ZoneBetDto> zoneBetting(MenuCode menu) {
        String userid = WebUtils.userid();

        return gameZoneBetDao.zoneBetting(userid, menu).stream()
                .map(ZoneBetDto::new).collect(Collectors.toList());
    }
}
