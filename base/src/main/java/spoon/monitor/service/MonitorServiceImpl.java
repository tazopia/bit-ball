package spoon.monitor.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.banking.domain.BankingCode;
import spoon.banking.entity.QBanking;
import spoon.banking.repository.BankingRepository;
import spoon.bet.entity.QBet;
import spoon.bet.repository.BetRepository;
import spoon.board.entity.QBoard;
import spoon.board.repository.BoardRepository;
import spoon.common.utils.DateUtils;
import spoon.config.domain.Config;
import spoon.customer.entity.QQna;
import spoon.customer.repository.QnaRepository;
import spoon.game.domain.MenuCode;
import spoon.game.entity.QGame;
import spoon.game.repository.GameRepository;
import spoon.mapper.MonitorMapper;
import spoon.member.domain.Role;
import spoon.member.entity.QMember;
import spoon.member.repository.MemberRepository;
import spoon.monitor.domain.Monitor;
import spoon.monitor.domain.MonitorDto;

import java.util.Date;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class MonitorServiceImpl implements MonitorService {

    private BankingRepository bankingRepository;

    private QnaRepository qnaRepository;

    private BetRepository betRepository;

    private MemberRepository memberRepository;

    private BoardRepository boardRepository;

    private GameRepository gameRepository;

    private MonitorMapper monitorMapper;

    private static Monitor monitor = new Monitor();


    @Override
    public Monitor getMonitor() {
        return monitor;
    }

    @Override
    public void initMonitor() {
        checkDeposit();
        checkWithdraw();
        checkQna();
        checkMember();
        checkBlack();
        checkBoard();
        checkSports();
    }

    @Override
    public void checkDeposit() {
        try {
            QBanking q = QBanking.banking;
            long cnt = bankingRepository.count(q.bankingCode.eq(BankingCode.IN).and(q.closing.isFalse()));
            long alarm = bankingRepository.count(q.bankingCode.eq(BankingCode.IN).and(q.closing.isFalse()).and(q.alarm.isTrue()));
            monitor.setDeposit(cnt);
            monitor.setAlarmDeposit(alarm);
        } catch (RuntimeException e) {
            log.error("충전 모니터링 에러", e);
        }
    }

    @Override
    public void checkWithdraw() {
        try {
            QBanking q = QBanking.banking;
            long cnt = bankingRepository.count(q.bankingCode.eq(BankingCode.OUT).and(q.closing.isFalse()));
            long alarm = bankingRepository.count(q.bankingCode.eq(BankingCode.OUT).and(q.closing.isFalse()).and(q.alarm.isTrue()));
            monitor.setWithdraw(cnt);
            monitor.setAlarmWithdraw(alarm);
        } catch (RuntimeException e) {
            log.error("환전 모니터링 에러", e);
        }
    }

    @Override
    public void checkQna() {
        try {
            QQna q = QQna.qna;
            long cnt = qnaRepository.count(q.re.isFalse());
            long alarm = qnaRepository.count(q.re.isFalse().and(q.alarm.isTrue()));
            monitor.setQna(cnt);
            monitor.setAlarmQna(alarm);
        } catch (RuntimeException e) {
            log.error("QNA 모니터링 에러", e);
        }
    }

    @Override
    public void checkBlack() {
        try {
            QBet q = QBet.bet;
            long cnt = betRepository.count(q.closing.isFalse().and(q.cancel.isFalse()).and(q.black.isTrue()));
            monitor.setBlack(cnt);
        } catch (RuntimeException e) {
            log.error("블랙회원 모니터링 에러", e);
        }
    }

    @Override
    public void checkMember() {
        try {
            QMember q = QMember.member;
            long cnt = memberRepository.count(q.role.eq(Role.USER).and(q.secession.isFalse()).and(q.enabled.isFalse()));
            monitor.setMember(cnt);
        } catch (RuntimeException e) {
            log.error("회원가입 모니터링 에러", e);
        }
    }

    @Override
    public void checkBoard() {
        try {
            QBoard q = QBoard.board;
            long cnt = boardRepository.count(q.alarm.isTrue());
            monitor.setBoard(cnt);
        } catch (RuntimeException e) {
            log.error("게시판 모니터링 에러", e);
        }
    }

    @Override
    public void checkSports() {
        try {
            QGame q = QGame.game;

            long cross = gameRepository.count(q.closing.isFalse().and(q.menuCode.in(MenuCode.MATCH, MenuCode.HANDICAP, MenuCode.CROSS)).and(q.gameDate.lt(new Date())).and(q.scoreHome.isNotNull()).and(q.amountTotal.ne(0L)));
            monitor.setCross(cross);

            long special = gameRepository.count(q.closing.isFalse().and(q.menuCode.eq(MenuCode.SPECIAL)).and(q.gameDate.lt(new Date())).and(q.scoreHome.isNotNull()).and(q.amountTotal.ne(0L)));
            monitor.setSpecial(special);

            long live = gameRepository.count(q.closing.isFalse().and(q.menuCode.eq(MenuCode.LIVE)).and(q.gameDate.lt(new Date())).and(q.scoreHome.isNotNull()).and(q.amountTotal.ne(0L)));
            monitor.setLive(live);
        } catch (RuntimeException e) {
            log.error("스포츠 모니터링 에러", e);
        }
    }

    @Async
    @Override
    public void checkAmount() {
        String start = DateUtils.format(new Date(), "yyyy-MM-dd");
        String end = DateUtils.format(DateUtils.beforeDays(-1), "yyyy-MM-dd");

        // 머니 포인트
        MonitorDto.Amount amount = monitorMapper.getAmount();
        monitor.setMoney(amount.getMoney());
        monitor.setPoint(amount.getPoint());

        // 입금 출금
        MonitorDto.Bank bank = monitorMapper.getBank(start, end);
        monitor.setIn(bank.getInAmount());
        monitor.setOut(bank.getOutAmount());

        // 진행금액
        List<MonitorDto.Bet> betList = monitorMapper.getBet();
        long sports = 0, ladder = 0, dari = 0, snail = 0, newSnail = 0, power = 0, powerLadder = 0,
                kenoLadder = 0, aladdin = 0, lowhi = 0, oddeven = 0, baccarat = 0, soccer = 0, dog = 0, luck = 0,
                crownOddeven = 0, crownBaccarat = 0, crownSutda = 0;
        long keno = 0;
        long bitcoin1 = 0, bitcoin3 = 0, bitcoin5 = 0;

        for (MonitorDto.Bet bet : betList) {
            switch (bet.getMenuCode()) {
                case MATCH:
                case HANDICAP:
                case SPECIAL:
                case LIVE:
                case CROSS:
                    sports += bet.getBetMoney();
                    break;
                case LADDER:
                    ladder += bet.getBetMoney();
                    break;
                case DARI:
                    dari += bet.getBetMoney();
                    break;
                case SNAIL:
                    snail += bet.getBetMoney();
                    break;
                case NEW_SNAIL:
                    newSnail += bet.getBetMoney();
                    break;
                case POWER:
                    power += bet.getBetMoney();
                    break;
                case POWER_LADDER:
                    powerLadder += bet.getBetMoney();
                    break;
                case KENO_LADDER:
                    kenoLadder += bet.getBetMoney();
                    break;
                case KENO:
                    keno += bet.getBetMoney();
                    break;
                case ALADDIN:
                    aladdin += bet.getBetMoney();
                    break;
                case LOWHI:
                    lowhi += bet.getBetMoney();
                    break;
                case ODDEVEN:
                    oddeven += bet.getBetMoney();
                    break;
                case BACCARAT:
                    baccarat += bet.getBetMoney();
                    break;
                case SOCCER:
                    soccer += bet.getBetMoney();
                    break;
                case DOG:
                    dog += bet.getBetMoney();
                    break;
                case LUCK:
                    luck += bet.getBetMoney();
                    break;
                case CROWN_ODDEVEN:
                    crownOddeven += bet.getBetMoney();
                    break;
                case CROWN_BACCARAT:
                    crownBaccarat += bet.getBetMoney();
                    break;
                case CROWN_SUTDA:
                    crownSutda += bet.getBetMoney();
                    break;
                case BITCOIN1:
                    bitcoin1 += bet.getBetMoney();
                    break;
                case BITCOIN3:
                    bitcoin3 += bet.getBetMoney();
                    break;
                case BITCOIN5:
                    bitcoin5 += bet.getBetMoney();
                    break;
            }
        }
        monitor.setSports(sports);
        monitor.setLadder(ladder);
        monitor.setDari(dari);
        monitor.setSnail(snail);
        monitor.setNewSnail(newSnail);
        monitor.setPower(power);
        monitor.setPowerLadder(powerLadder);
        monitor.setKeno(keno);
        monitor.setKenoLadder(kenoLadder);
        monitor.setAladdin(aladdin);
        monitor.setLowhi(lowhi);
        monitor.setOddeven(oddeven);
        monitor.setBaccarat(baccarat);
        monitor.setSoccer(soccer);
        monitor.setDog(dog);
        monitor.setLuck(luck);

        monitor.setCrownOddeven(crownOddeven);
        monitor.setCrownBaccarat(crownBaccarat);
        monitor.setCrownSutda(crownSutda);

        monitor.setBitcoin1(bitcoin1);
        monitor.setBitcoin3(bitcoin3);
        monitor.setBitcoin5(bitcoin5);

        // 종료금액
        List<MonitorDto.Bet> betEndList = monitorMapper.getBetEnd(start, end);
        sports = 0;
        ladder = 0;
        dari = 0;
        snail = 0;
        newSnail = 0;
        power = 0;
        powerLadder = 0;
        kenoLadder = 0;
        aladdin = 0;
        lowhi = 0;
        oddeven = 0;
        baccarat = 0;
        soccer = 0;
        dog = 0;
        luck = 0;
        crownOddeven = 0;
        crownBaccarat = 0;
        crownSutda = 0;
        keno = 0;

        for (MonitorDto.Bet betEnd : betEndList) {
            switch (betEnd.getMenuCode()) {
                case MATCH:
                case HANDICAP:
                case SPECIAL:
                case LIVE:
                case CROSS:
                    sports += betEnd.getBetMoney() - betEnd.getHitMoney();
                    break;
                case LADDER:
                    ladder += betEnd.getBetMoney() - betEnd.getHitMoney();
                    break;
                case DARI:
                    dari += betEnd.getBetMoney() - betEnd.getHitMoney();
                    break;
                case SNAIL:
                    snail += betEnd.getBetMoney() - betEnd.getHitMoney();
                    break;
                case NEW_SNAIL:
                    newSnail += betEnd.getBetMoney() - betEnd.getHitMoney();
                    break;
                case POWER:
                    power += betEnd.getBetMoney() - betEnd.getHitMoney();
                    break;
                case POWER_LADDER:
                    powerLadder += betEnd.getBetMoney() - betEnd.getHitMoney();
                    break;
                case KENO:
                    keno += betEnd.getBetMoney() - betEnd.getHitMoney();
                    break;
                case KENO_LADDER:
                    kenoLadder += betEnd.getBetMoney() - betEnd.getHitMoney();
                    break;
                case ALADDIN:
                    aladdin += betEnd.getBetMoney() - betEnd.getHitMoney();
                    break;
                case LOWHI:
                    lowhi += betEnd.getBetMoney() - betEnd.getHitMoney();
                    break;
                case ODDEVEN:
                    oddeven += betEnd.getBetMoney() - betEnd.getHitMoney();
                    break;
                case BACCARAT:
                    baccarat += betEnd.getBetMoney() - betEnd.getHitMoney();
                    break;
                case SOCCER:
                    soccer += betEnd.getBetMoney() - betEnd.getHitMoney();
                    break;
                case DOG:
                    dog += betEnd.getBetMoney() - betEnd.getHitMoney();
                    break;
                case LUCK:
                    luck += betEnd.getBetMoney() - betEnd.getHitMoney();
                    break;
                case CROWN_ODDEVEN:
                    crownOddeven += betEnd.getBetMoney() - betEnd.getHitMoney();
                    break;
                case CROWN_BACCARAT:
                    crownBaccarat += betEnd.getBetMoney() - betEnd.getHitMoney();
                    break;
                case CROWN_SUTDA:
                    crownSutda += betEnd.getBetMoney() - betEnd.getHitMoney();
                    break;
                case BITCOIN1:
                    bitcoin1 += betEnd.getBetMoney() - betEnd.getHitMoney();
                    break;
                case BITCOIN3:
                    bitcoin3 += betEnd.getBetMoney() - betEnd.getHitMoney();
                    break;
                case BITCOIN5:
                    bitcoin5 += betEnd.getBetMoney() - betEnd.getHitMoney();
                    break;
            }
        }
        monitor.setSportsEnd(sports);
        monitor.setLadderEnd(ladder);
        monitor.setDariEnd(dari);
        monitor.setSnailEnd(snail);
        monitor.setNewSnailEnd(newSnail);
        monitor.setPowerEnd(power);
        monitor.setPowerLadderEnd(powerLadder);
        monitor.setKenoEnd(keno);
        monitor.setKenoLadderEnd(kenoLadder);
        monitor.setAladdinEnd(aladdin);
        monitor.setLowhiEnd(lowhi);
        monitor.setOddevenEnd(oddeven);
        monitor.setBaccaratEnd(baccarat);
        monitor.setSoccerEnd(soccer);
        monitor.setDogEnd(dog);
        monitor.setLuckEnd(luck);

        monitor.setCrownOddevenEnd(crownOddeven);
        monitor.setCrownBaccaratEnd(crownBaccarat);
        monitor.setCrownSutdaEnd(crownSutda);

        monitor.setBitcoin1End(bitcoin1);
        monitor.setBitcoin3End(bitcoin3);
        monitor.setBitcoin5End(bitcoin5);

        // 인플레이
        if (Config.getSysConfig().getSports().isCanInplay()) {
            monitor.setInplay(monitorMapper.getInplay());
            monitor.setInplayEnd(monitorMapper.getInplayEnd(start, end));
        }

        // 선카지노
        monitor.setSun(monitorMapper.getSun(start, end) * -1);
        monitor.setSunEnd(monitorMapper.getSunEnd(start, end));

    }

}
