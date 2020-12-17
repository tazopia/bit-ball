package spoon.game.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.bet.service.BetClosingService;
import spoon.bet.service.BetGameService;
import spoon.common.utils.ErrorUtils;
import spoon.config.domain.Config;
import spoon.game.domain.GameDto;
import spoon.game.entity.Game;
import spoon.game.entity.GameLogger;
import spoon.game.entity.QGame;
import spoon.game.repository.GameRepository;
import spoon.mapper.GameMapper;

import java.util.Date;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class GameBotServiceImpl implements GameBotService {

    private GameLoggerService gameLoggerService;

    private BetClosingService betClosingService;

    private BetGameService betGameService;

    private GameRepository gameRepository;

    private GameMapper gameMapper;

    private static QGame q = QGame.game;

    @Transactional
    @Override
    public boolean addGame(Game game) {
        try {
            if (!isExist(game.getSiteCode(), game.getSiteId())) {
                game.updateOddRate();
                gameRepository.saveAndFlush(game);
                gameLoggerService.addGameLogger(new GameLogger(game, "AUTO_BOT", "오토봇 작업"));
                return true;
            }
            return false;
        } catch (RuntimeException e) {
            log.error("{} 봇 게임 등록 에러 - {}", game.getSiteCode(), e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
        }
        return false;
    }

    @Transactional
    @Override
    public boolean updateGame(Game game) {
        try {
            int upCount = game.getUpCount();
            game.updateOddRate();
            Game result = gameRepository.saveAndFlush(game);

            if (result.getUpCount() > upCount) { // 업카운트가 증가한다.
                gameLoggerService.addGameLogger(new GameLogger(game, "AUTO_BOT", "오토봇 작업"));

                // 베팅이 없다면 해당사항이 없다.
                if (game.getAmountTotal() == 0) return true;

                if (gameLoggerService.isChangeDate(game.getId(), game.getGameDate())) {
                    // 해당 베팅의 날짜를 바꿔주자
                    betGameService.updateGameDate(game.getId(), game.getGameDate());
                }

                return true;
            }
        } catch (RuntimeException e) {
            log.error("{} 봇 게임 변경 에러 - {}", game.getSiteCode(), e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return false;
    }

    @Transactional
    @Override
    public boolean gameScore(Long gameId, Integer scoreHome, Integer scoreAway, boolean cancel, String ut) {
        Game game = gameRepository.findOne(gameId);

        // 이미 처리된 경기인지 확인
        if (game == null || game.isClosing()) {
            return false;
        }

        try {
            // 자동 종료이거나 베팅한 유저가 없다면
            if (Config.getGameConfig().isAutoClosing() || betClosingService.notBetting(game.getId())) { // 자동 종료 베팅 크로징 처리 하면 된다.
                game.setClosing(true);
                betClosingService.closingGameBetting(gameId, scoreHome, scoreAway, cancel);
            }

            game.updateScore(scoreHome, scoreAway, cancel);
            game.updateOddRate();
            game.setUt(ut);
            gameRepository.saveAndFlush(game);

        } catch (RuntimeException e) {
            log.error("{} - (siteId: {}) 봇에서 경기를 종료하지 못하였습니다. - {}", game.getSiteCode(), game.getSiteId(), e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public boolean isExist(String siteCode, String siteId) {
        return gameRepository.count(q.siteCode.eq(siteCode).and(q.siteId.eq(siteId))) > 0;
    }

    @Override
    public Game getGame(String siteCode, String siteId) {
        return gameRepository.findOne(q.siteCode.eq(siteCode).and(q.siteId.eq(siteId)));
    }

    @Override
    public String getMaxUt(String siteCode, boolean closing) {
        return gameRepository.findMaxUt(siteCode, closing);
    }

    @Override
    public String findGroupId(String siteId, String siteCode, Date gameDate, String league, String teamHome, String teamAway) {
        String groupId = gameMapper.findGroupId(siteCode, gameDate, league, teamHome, teamAway);
        return groupId == null ? siteId : groupId;
    }

    @Override
    public List<GameDto.Bot> initOdds(String siteCode) {
        return gameMapper.initOdds(siteCode);
    }

    @Override
    public List<GameDto.Bot> initScore(String siteCode) {
        return gameMapper.initScore(siteCode);
    }


}
