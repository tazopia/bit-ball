package spoon.game.service.sports;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.game.domain.TeamDto;
import spoon.game.entity.Team;

public interface TeamService {

    Iterable<Team> getAll();
    
    Page<Team> getPage(Pageable pageable, TeamDto.Command command);

    void addTeam(Team team);

    void addTeam(TeamDto.Add add);

    void updateTeam(TeamDto.Update update);

    void updateTeam(TeamDto.PopupUpdate update);

    String delete(Long id);

    Team findById(Long id);

}
