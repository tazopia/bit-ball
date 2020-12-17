package spoon.config.service;

import spoon.config.domain.ConfigDto;
import spoon.game.domain.MenuCode;

public interface GameConfigService {

    ConfigDto.Game gameConfig(MenuCode menuCode);

}
