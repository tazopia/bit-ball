package spoon.game.service.sports;

import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.web.multipart.MultipartFile;
import spoon.common.net.HttpParsing;
import spoon.common.utils.StringUtils;
import spoon.config.domain.Config;
import spoon.game.domain.LeagueDto;
import spoon.game.entity.League;
import spoon.game.entity.QLeague;
import spoon.game.repository.LeagueRepository;
import spoon.support.web.FileUploadHandler;
import spoon.support.web.UploadType;

import javax.servlet.ServletContext;

@Slf4j
@AllArgsConstructor
@Service
public class LeagueServiceImpl implements LeagueService {

    private LeagueRepository leagueRepository;

    private FileUploadHandler fileUploadHandler;

    private ServletContext servletContext;

    private static QLeague q = QLeague.league;

    @Override
    public Iterable<League> getAll() {
        return leagueRepository.findAll();
    }

    @Override
    public Page<League> getPage(Pageable pageable, LeagueDto.Command command) {
        BooleanBuilder where = new BooleanBuilder();
        if (StringUtils.notEmpty(command.getLeague())) {
            where.and(q.leagueName.like("%" + command.getLeague() + "%")
                    .or(q.leagueKor.like("%" + command.getLeague() + "%")));
        }
        if (StringUtils.notEmpty(command.getSports())) {
            where.and(q.sports.eq(command.getSports()));
        }
        return leagueRepository.findAll(where, pageable);
    }

    @Transactional
    @Override
    public void addLeague(League league, String flag) {
        if (Config.getLeagueMap().containsKey(league.getKey())) return;
        league.setLeagueFlag(uploadFlag(flag));
        updateLeagueMap(league);
        try {
            leagueRepository.saveAndFlush(league);
        } catch (RuntimeException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
        }
    }

    @Transactional
    @Override
    public void addLeague(LeagueDto.Add add, MultipartFile file) {
        if (Config.getLeagueMap().containsKey(add.getKey())) return;

        String saveFileName = fileUploadHandler.saveFile(file, "home-" + System.currentTimeMillis(), UploadType.LEAGUE);
        League league = new League();
        league.setSports(add.getSports());
        league.setLeagueName(add.getLeague());
        league.setLeagueKor(add.getLeague());
        league.setLeagueFlag(saveFileName);
        league.setEnabled(add.isEnabled());
        leagueRepository.saveAndFlush(league);

        updateLeagueMap(league);
    }

    @Override
    public League findOne(Long id) {
        return leagueRepository.findOne(id);
    }

    @Transactional
    @Override
    public void updateLeague(LeagueDto.Update update, MultipartFile file) {
        League league = leagueRepository.findOne(update.getId());
        if (file != null && file.getSize() > 0) {
            String fullName = org.springframework.util.StringUtils.getFilename(file.getOriginalFilename());
            league.setLeagueFlag(fullName);
            fileUploadHandler.saveFile(file, fullName, UploadType.LEAGUE);
        }
        league.setLeagueKor(update.getLeagueKor());
        league.setSports(update.getSports());
        league.setEnabled(update.isEnabled());
        leagueRepository.saveAndFlush(league);
        updateLeagueMap(league);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        League league = leagueRepository.findOne(id);
        deleteLeagueMap(league);
        leagueRepository.delete(id);
    }

    @Transactional
    @Override
    public void updateFlag(League league, String flag) {
        league.setLeagueFlag(uploadFlag(flag));
        updateLeagueMap(league);
        try {
            leagueRepository.saveAndFlush(league);
        } catch (RuntimeException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
        }
    }

    private String uploadFlag(String flag) {
        String fileName = StringUtils.getFilename(flag);
        String flagPath = servletContext.getRealPath(servletContext.getContextPath()).replaceAll("\\\\", "/") + "images/league/" + fileName;
        boolean success = HttpParsing.saveImage(flag, flagPath);
        return success ? fileName : "league.png";
    }

    private void deleteLeagueMap(League league) {
        Config.getLeagueMap().remove(league.getKey());
    }

    private void updateLeagueMap(League league) {
        Config.getLeagueMap().put(league.getKey(), league);
    }
}
