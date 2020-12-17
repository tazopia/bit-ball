package spoon.game.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.bet.service.BetClosingService;
import spoon.game.domain.GameDto;
import spoon.game.domain.MenuCode;
import spoon.game.entity.Game;
import spoon.game.entity.GameLogger;
import spoon.game.repository.GameRepository;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Service
public class GameServiceImpl implements GameService {

    private GameRepository gameRepository;

    private GameLoggerService gameLoggerService;

    private BetClosingService betClosingService;

    @Transactional
    @Override
    public AjaxResult gameScore(GameDto.Result result) {
        Game game = gameRepository.findOne(result.getId());

        if (game == null) {
            return new AjaxResult(false, "게임이 존재하지 않습니다.");
        }

        try {
            // 이미 처리된 경기인지 확인
            if (game.isClosing()) {
                boolean notChange = game.getScoreHome().equals(result.getScoreHome())
                        && game.getScoreAway().equals(result.getScoreAway())
                        && game.isCancel() == result.isCancel();
                if (notChange) {
                    return new AjaxResult(false, "결과가 변경되지 않았습니다.");
                }

                betClosingService.rollbackBetting(game.getId());
            }

            // 취소경기일 경우 스코어를 넣지 않아도 되도록
            if (result.isCancel()) {
                result.setScoreHome(0);
                result.setScoreAway(0);
            }

            game.updateScore(result.getScoreHome(), result.getScoreAway(), result.isCancel());
            game.setClosing(true);
            game.updateOddRate();
            gameRepository.saveAndFlush(game);
            gameLoggerService.addGameLogger(new GameLogger(game));
            betClosingService.closingGameBetting(result.getId(), result.getScoreHome(), result.getScoreAway(), result.isCancel());
        } catch (RuntimeException e) {
            log.error("현재 경기의 스코어를 기록하지 못하였습니다.", e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new AjaxResult(false, "현재 경기의 스코어를 기록하지 못하였습니다.");
        }
        return new AjaxResult(true, game.getTeamHome() + " VS " + game.getTeamAway() + " 결과처리를 완료 하였습니다.");
    }

    @Transactional
    @Override
    public AjaxResult gameReset(Long id) {
        Game game = gameRepository.findOne(id);

        if (game == null) {
            return new AjaxResult(false, "게임이 존재하지 않습니다.");
        }

        if (!game.isClosing()) {
            return new AjaxResult(false, "이미 정산 취소된 경기 입니다.");
        }

        try {
            betClosingService.rollbackBetting(game.getId());
            game.setScoreHome(null);
            game.setScoreAway(null);
            game.setClosing(false);
            game.setCancel(false);
            game.updateOddRate();
            gameRepository.saveAndFlush(game);
            gameLoggerService.addGameLogger(new GameLogger(game));
        } catch (RuntimeException e) {
            log.error("현재 경기의 리셋하지 못하였습니다.", e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new AjaxResult(false, "현재 경기의 리셋하지 못하였습니다.");
        }

        return new AjaxResult(true, game.getTeamHome() + " VS " + game.getTeamAway() + " 결과처리를 리셋 하였습니다.");
    }

    @Override
    public Game getGame(Long gameId) {
        return gameRepository.findOne(gameId);
    }

    @Transactional
    @Override
    public void addGame(Game game) {
        game.updateOddRate();
        gameRepository.saveAndFlush(game);
        gameLoggerService.addGameLogger(new GameLogger(game));
    }

    @Override
    public String getGroupId(Game game) {
        if (game.getMenuCode() == MenuCode.SPECIAL) {
            return gameRepository.findGroupIdSpecial(game.getSports(), game.getGameDate(), game.getLeague(), game.getTeamHome(), game.getTeamAway());
        } else {
            return gameRepository.findGroupId(game.getSports(), game.getGameDate(), game.getLeague(), game.getTeamHome() + "%", game.getTeamAway() + "%");
        }
    }
}
