package spoon.bot.sports.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spoon.game.service.GameDeleteService;

@Slf4j
@AllArgsConstructor
@Component
public class GameDeleteTask {

    private GameDeleteService gameDeleteService;

    @Scheduled(cron = "43 30 2 * * ?")
    public void gameDelete() {
        gameDeleteService.delete(7);
    }

}
