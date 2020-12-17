package spoon.game.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spoon.bet.service.BetGameService;
import spoon.bot.sports.service.ParsingGame;
import spoon.game.domain.GameDto;
import spoon.game.entity.Game;
import spoon.game.entity.GameLogger;
import spoon.game.entity.QGame;
import spoon.game.repository.GameRepository;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Service
public class GameUpdateServiceImpl implements GameUpdateService {

    private GameRepository gameRepository;

    private BetGameService betGameService;

    private ParsingGame bet365ParsingGame;

    private ParsingGame bestParsingGame;

    private GameLoggerService gameLoggerService;

    private static QGame q = QGame.game;

    @Transactional
    @Override
    public AjaxResult update(GameDto.Update update) {
        boolean isUpdate = false;
        boolean changeGameDate = false;
        Game game = gameRepository.findOne(update.getGameId());

        if (game.getGameDate().compareTo(update.getGameDate()) != 0) {
            game.setGameDate(update.getGameDate());
            changeGameDate = true;
            log.debug("경기시간 업데이트");
            isUpdate = true;
        }
        if (game.getOddsHome() != update.getOddsHome()) {
            game.setOddsHome(update.getOddsHome());
            log.debug("홈배당 업데이트");
            isUpdate = true;
        }
        if (game.getOddsDraw() != update.getOddsDraw()) {
            game.setOddsDraw(update.getOddsDraw());
            log.debug("무배당 업데이트");
            isUpdate = true;
        }
        if (game.getOddsAway() != update.getOddsAway()) {
            game.setOddsAway(update.getOddsAway());
            log.debug("원정배당 업데이트");
            isUpdate = true;
        }
        if (game.getHandicap() != update.getHandicap()) {
            game.setHandicap(update.getHandicap());
            log.debug("핸디캡 업데이트");
            isUpdate = true;
        }

        if (isUpdate) {
            game.setAutoUpdate(false);
            game.updateOddRate();
            gameRepository.saveAndFlush(game);
            gameLoggerService.addGameLogger(new GameLogger(game));

            // 시간이 변경 되었다면 베팅도 변경해 줘야 한다.
            if (changeGameDate && game.getAmountTotal() > 0) {
                betGameService.updateGameDate(game.getId(), game.getGameDate());
            }

            return new AjaxResult(true, "경기를 업데이트 하였습니다.");
        }

        return new AjaxResult(false, "업데이트 내용이 없습니다.");
    }

    @Transactional
    @Override
    public AjaxResult betEnabled(GameDto.BetEnabled betEnabled) {
        Game game = gameRepository.findOne(betEnabled.getGameId());
        boolean enabledBet = false;
        switch (betEnabled.getBetType()) {
            case "home":
                enabledBet = !game.isBetHome();
                game.setBetHome(enabledBet);
                break;
            case "draw":
                enabledBet = !game.isBetDraw();
                game.setBetDraw(enabledBet);
                break;
            case "away":
                enabledBet = !game.isBetAway();
                game.setBetAway(enabledBet);
                break;
        }
        game.setAutoUpdate(false);
        game.updateOddRate();
        gameRepository.saveAndFlush(game);
        gameLoggerService.addGameLogger(new GameLogger(game));

        AjaxResult result = new AjaxResult(true);
        result.setMessage("베팅을 " + (enabledBet ? "가능으로" : "불가능으로") + " 설정하였습니다.");
        result.setValue(enabledBet ? "Y" : "N");
        return result;
    }

    @Transactional
    @Override
    public AjaxResult autoUpdate(Long gameId) {
        Game game = gameRepository.findOne(gameId);
        game.setAutoUpdate(!game.isAutoUpdate());
        game.updateOddRate();
        gameRepository.saveAndFlush(game);
        gameLoggerService.addGameLogger(new GameLogger(game));

        AjaxResult result = new AjaxResult(true);
        result.setMessage("자동배당 업데이트를 " + (game.isAutoUpdate() ? "가능으로" : "불가능으로") + " 설정하였습니다.");
        result.setValue(game.isAutoUpdate() ? "Y" : "N");
        return result;
    }

    @Transactional
    @Override
    public AjaxResult gameEnabled(Long[] gameIds, boolean enabled) {
        Iterable<Game> list = gameRepository.findAll(q.id.in(gameIds));
        for (Game game : list) {
            game.setEnabled(enabled);
            game.updateOddRate();
            gameRepository.saveAndFlush(game);
            gameLoggerService.addGameLogger(new GameLogger(game));
        }
        return new AjaxResult(true);
    }

    @Transactional
    @Override
    public AjaxResult gameDeleted(Long[] gameIds, boolean enabled) {
        Iterable<Game> list = gameRepository.findAll(q.id.in(gameIds));
        for (Game game : list) {
            game.setDeleted(enabled);
            game.updateOddRate();
            gameRepository.saveAndFlush(game);
            gameLoggerService.addGameLogger(new GameLogger(game));
        }
        return new AjaxResult(true);
    }

    @Transactional
    @Override
    public AjaxResult reload() {
        String min365 = gameRepository.minUt("365");
        gameRepository.reload("365", min365);
        bet365ParsingGame.resetUt(min365);

        String minBest = gameRepository.minUt("Best");
        gameRepository.reload("Best", minBest);
        bestParsingGame.resetUt(minBest);

        return new AjaxResult(true, "다음 파싱에서 모든 배당을 새로 받아 옵니다.");
    }

}
