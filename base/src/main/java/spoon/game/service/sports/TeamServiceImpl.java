package spoon.game.service.sports;

import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.StringUtils;
import spoon.config.domain.Config;
import spoon.game.domain.TeamDto;
import spoon.game.entity.QTeam;
import spoon.game.entity.Team;
import spoon.game.repository.TeamRepository;

@Slf4j
@AllArgsConstructor
@Service
public class TeamServiceImpl implements TeamService {

    private TeamRepository teamRepository;

    private static QTeam q = QTeam.team;

    @Override
    public Iterable<Team> getAll() {
        return teamRepository.findAll();
    }

    @Override
    public Page<Team> getPage(Pageable pageable, TeamDto.Command command) {
        BooleanBuilder where = new BooleanBuilder();
        if (StringUtils.notEmpty(command.getSports())) {
            where.and(q.sports.eq(command.getSports()));
        }
        if (StringUtils.notEmpty(command.getTeam())) {
            where.and(q.teamName.like("%" + command.getTeam() + "%"));
        }
        return teamRepository.findAll(where, pageable);
    }

    @Transactional
    @Override
    public void addTeam(Team team) {
        if (Config.getTeamMap().containsKey(team.getKey())) return;
        updateTeamMap(team);
        try {
            teamRepository.saveAndFlush(team);
        } catch (RuntimeException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
        }
    }

    @Transactional
    @Override
    public void addTeam(TeamDto.Add add) {
        if (Config.getTeamMap().containsKey(add.getKey())) return;
        Team team = new Team(add.getSports(), add.getTeamName());
        updateTeamMap(team);
        team.setTeamKor(add.getTeamKor());
        teamRepository.saveAndFlush(team);
    }

    @Transactional
    @Override
    public void updateTeam(TeamDto.Update update) {
        Team team = teamRepository.findOne(update.getId());
        team.setTeamKor(update.getTeam());
        teamRepository.saveAndFlush(team);
        updateTeamMap(team);
    }

    @Transactional
    @Override
    public void updateTeam(TeamDto.PopupUpdate update) {
        Team team = teamRepository.findOne(update.getId());
        team.setTeamKor(update.getTeamKor());
        teamRepository.saveAndFlush(team);
        updateTeamMap(team);
    }

    @Transactional
    @Override
    public String delete(Long id) {
        Team team = teamRepository.findOne(id);
        teamRepository.delete(team);
        deleteTeamMap(team);
        return team.getTeamKor();
    }

    @Override
    public Team findById(Long id) {
        return teamRepository.findOne(id);
    }

    private void deleteTeamMap(Team team) {
        Config.getTeamMap().remove(team.getKey());
    }

    private Team updateTeamMap(Team team) {
        return Config.getTeamMap().put(team.getKey(), team);
    }
}
