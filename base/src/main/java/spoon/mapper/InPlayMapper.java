package spoon.mapper;

import spoon.inPlay.odds.domain.InPlayDto;

import java.util.List;

public interface InPlayMapper {

    List<InPlayDto.GameData> getActiveList();

    List<InPlayDto.AdminGameData> getAdminList();

}
