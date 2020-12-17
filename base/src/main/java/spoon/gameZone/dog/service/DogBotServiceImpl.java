package spoon.gameZone.dog.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.common.utils.ErrorUtils;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.dog.Dog;
import spoon.gameZone.dog.DogDto;
import spoon.gameZone.dog.DogRepository;
import spoon.gameZone.dog.QDog;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class DogBotServiceImpl implements DogBotService {

    private DogGameService dogGameService;

    private DogRepository dogRepository;

    private static QDog q = QDog.dog;

    @Override
    public boolean notExist(Date gameDate) {
        return dogRepository.count(q.sdate.eq(DateUtils.format(gameDate, "yyyyMMddHHmm"))) == 0;
    }

    @Transactional
    @Override
    public void addGame(Dog dog) {
        dogRepository.saveAndFlush(dog);
    }

    @Transactional
    @Override
    public boolean closingGame(Dog result) {
        Dog dog = dogRepository.findOne(q.sdate.eq(result.getSdate()));
        if (dog == null) {
            return true;
        }

        try {
            DogDto.Score score = new DogDto.Score();
            score.setWinNumber(result.getWinNumber());
            dog.updateScore(score);

            dogRepository.saveAndFlush(dog);
            dogGameService.closingBetting(dog);
        } catch (RuntimeException e) {
            log.error("개경주 {}회차 결과 업데이트에 실패하였습니다. - {}", dog.getSdate(), e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public void checkResult() {
        long cnt = dogRepository.count(q.gameDate.before(DateUtils.beforeMinutes(3)).and(q.closing.isFalse()));
        ZoneConfig.getDog().setResult(cnt);
    }

    @Transactional
    @Override
    public void deleteGame(int days) {
        dogRepository.deleteGame(DateUtils.beforeDays(days));
    }

    @Override
    public String getLastGame(String edate) {
        return dogRepository.getLastSdate(edate);
    }
}
