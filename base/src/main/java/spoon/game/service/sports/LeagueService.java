package spoon.game.service.sports;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import spoon.game.domain.LeagueDto;
import spoon.game.entity.League;

public interface LeagueService {

    Iterable<League> getAll();

    Page<League> getPage(Pageable pageable, LeagueDto.Command command);

    void addLeague(League league, String flag);

    void addLeague(LeagueDto.Add add, MultipartFile file);

    League findOne(Long id);

    void updateLeague(LeagueDto.Update update, MultipartFile file);

    void delete(Long id);

    void updateFlag(League league, String flag);

}
