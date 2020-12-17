package spoon.game.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spoon.game.entity.GameLogger;
import spoon.game.entity.QGameLogger;
import spoon.game.repository.GameLoggerRepository;

import java.util.Date;

@AllArgsConstructor
@Service
public class GameLoggerServiceImpl implements GameLoggerService {

    private GameLoggerRepository gameLoggerRepository;

    private static QGameLogger q = QGameLogger.gameLogger;

    @Transactional
    @Override
    public void addGameLogger(GameLogger logger) {
        gameLoggerRepository.saveAndFlush(logger);
    }

    @Transactional
    @Override
    public void deleteGameLogger(long gameId) {
        gameLoggerRepository.deleteBeforeGameId(gameId);
    }

    @Override
    public Iterable<GameLogger> getGameLogger(long gameId) {
        return gameLoggerRepository.findAll(q.gameId.eq(gameId));
    }

    @Override
    public boolean isChangeDate(long gameId, Date gameDate) {
        Date loggerDate = gameLoggerRepository.getLastLoggerGameDate(gameId);
        if (loggerDate == null) return false;

        return !gameDate.equals(loggerDate);
    }
}
