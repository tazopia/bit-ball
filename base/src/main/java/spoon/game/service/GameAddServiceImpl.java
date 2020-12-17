package spoon.game.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spoon.common.utils.DateUtils;
import spoon.common.utils.StringUtils;
import spoon.game.domain.GameCode;
import spoon.game.domain.MenuCode;
import spoon.game.entity.Game;
import spoon.game.entity.League;
import spoon.game.entity.Team;
import spoon.game.service.sports.LeagueService;
import spoon.game.service.sports.TeamService;

import java.util.Arrays;
import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class GameAddServiceImpl implements GameAddService {

    private GameService gameService;

    private LeagueService leagueService;

    private TeamService teamService;

    @Override
    public long addGame(String gameText) {
        String[] lines = gameText.split(System.getProperty("line.separator"));
        long cnt = 0;

        for (int i = 0; i < lines.length; i++) {
            log.info(lines[i]);
            if (StringUtils.empty(lines[i])) continue;
            String[] fields = Arrays.copyOf(lines[i].split("\\t"), 13);

            try {
                Game game = makeGame(fields, i);
                gameService.addGame(game);

                League league = game.getLeagueBean();
                if (league.getId() == null || league.getId() == 0) {
                    leagueService.addLeague(league, "/images/league/league-default.png");
                }

                Team home = game.getTeamHomeBean();
                if (home.getId() == null || home.getId() == 0) {
                    teamService.addTeam(home);
                }

                Team away = game.getTeamAwayBean();
                if (away.getId() == null || away.getId() == 0) {
                    teamService.addTeam(away);
                }

                cnt++;
            } catch (RuntimeException e) {
                log.error("게임 등록시 에러가 발생하였습니다.", e);
            }
        }

        return cnt;
    }

    private Game makeGame(String[] fields, int cnt) {
        Game game = new Game();
        game.setSiteCode("본사");
        game.setSiteId(String.valueOf(System.currentTimeMillis()) + cnt);
        game.setGameDate(DateUtils.parse(fields[2].trim() + " " + fields[3].trim(), "yyyy-MM-dd HH:mm"));
        game.setSports(fields[4].trim());

        switch (fields[0].trim()) {
            case "승무패":
                game.setMenuCode(MenuCode.MATCH);
                break;
            case "핸디캡":
                game.setMenuCode(MenuCode.HANDICAP);
                break;
            case "스페셜":
                game.setMenuCode(MenuCode.SPECIAL);
                break;
            case "라이브":
                game.setMenuCode(MenuCode.LIVE);
                break;
            default:
                throw new RuntimeException("게임 수동등록에 메뉴코드가 일치하지 않습니다.");
        }

        switch (fields[1].trim()) {
            case "승무패":
                game.setGameCode(GameCode.MATCH);
                game.setSort(1);
                break;
            case "핸디캡":
                game.setGameCode(GameCode.HANDICAP);
                game.setSort(1000);
                break;
            case "오버언더":
                game.setGameCode(GameCode.OVER_UNDER);
                game.setSort(2000);
                break;
            default:
                throw new RuntimeException("게임 수동등록에 게임코드가 일치하지 않습니다.");
        }
        game.setLeague(fields[5].trim());
        game.setTeamHome(fields[6].trim());
        game.setTeamAway(fields[7].trim());

        if (game.getGameCode() == GameCode.MATCH) { // 승무패
            game.setOddsDraw(Double.parseDouble(fields[9].trim()));
            game.setHandicap(game.getOddsDraw());
            if (game.getOddsDraw() == 0) {
                game.setBetDraw(false);
            } else {
                game.setBetDraw(true);
            }
        } else { // 나머지 게임들
            game.setOddsDraw(0D);
            game.setBetDraw(false);
            game.setHandicap(Double.parseDouble(fields[9].trim()));
        }

        if (StringUtils.notEmpty(fields[11]) && StringUtils.notEmpty(fields[12])) {
            game.setSpecial(fields[11].trim() + "|" + fields[12].trim());
        } else {
            game.setSpecial("");
        }

        game.setOddsHome(Double.parseDouble(fields[8].trim()));
        game.setBetHome(true);
        game.setOddsAway(Double.parseDouble(fields[10].trim()));
        game.setBetAway(true);
        game.setAutoUpdate(false);
        game.setUt(String.valueOf(new Date().getTime()));

        // 그룹아이디 검색
        String groupId = gameService.getGroupId(game);
        if (groupId == null) groupId = game.getSiteId();
        game.setGroupId(groupId);

        return game;
    }
}
