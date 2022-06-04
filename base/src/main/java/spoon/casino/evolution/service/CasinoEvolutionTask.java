package spoon.casino.evolution.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CasinoEvolutionTask {

    private final CasinoEvolutionTransactionService casinoEvolutionTransactionService;

    @Scheduled(fixedDelay = 30 * 1000)
    public void getCasinoBet() {
        casinoEvolutionTransactionService.getHistory();
    }

    @Scheduled(fixedDelay = 30 * 1000, initialDelay = 20 * 1000)
    public void getCasinoBalance() {
        casinoEvolutionTransactionService.getAmount();
    }
}
