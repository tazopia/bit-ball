package spoon.inPlay.bet.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spoon.bet.domain.BetDto;
import spoon.common.utils.DateUtils;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.StringUtils;
import spoon.inPlay.bet.domain.InPlayBetDto;
import spoon.inPlay.bet.entity.InPlayBet;
import spoon.inPlay.bet.entity.QInPlayBet;
import spoon.inPlay.bet.repository.InPlayBetRepository;
import spoon.inPlay.config.domain.InPlayConfig;
import spoon.inPlay.odds.domain.LiveScore;
import spoon.inPlay.odds.entity.InPlayOddsDone;
import spoon.inPlay.odds.repository.InPlayOddsDoneRepository;
import spoon.member.domain.Role;
import spoon.member.service.MemberService;
import spoon.payment.domain.MoneyCode;
import spoon.payment.service.PaymentService;
import spoon.support.web.AjaxResult;

@Slf4j
@RequiredArgsConstructor
@Service
public class InPlayBetService {

    private final InPlayBetRepository inPlayBetRepository;

    private final InPlayOddsDoneRepository inPlayOddsDoneRepository;

    private final PaymentService paymentService;

    private final MemberService memberService;

    private static final QInPlayBet BET = QInPlayBet.inPlayBet;

    @Transactional
    public void score() {
        Iterable<InPlayBet> list = inPlayBetRepository.findAll(BET.closing.isFalse().and(BET.cancel.isFalse()));
        // 1. done 의 id 로 베팅을 가져온다.

        for (InPlayBet bet : list) {
            InPlayOddsDone done = inPlayOddsDoneRepository.findOne(bet.getOddsId());
            if (done == null) continue;

            bet.updateScore(done);

            if (bet.getRole() != Role.USER) continue; // 더미는 해 줄것이 없다.

            if (bet.getHitMoney() > 0) {
                String memo = bet.getTeamHome() + " VS " + bet.getTeamAway() + " " + bet.getOname() + " " + bet.getName() + " " + bet.getLine() + "(" + bet.getBetOdds() + " * " + bet.getBetMoney() + ")";
                paymentWin(bet.getId(), bet.getUserid(), bet.getHitMoney(), memo);
            }
        }

        // 2. 베팅의 처리를 완료한다.

        // 3. 당첨금을 완료한다.
    }

    @Transactional
    public boolean addGameBetting(InPlayBet bet) {
        try {
            long betMoney = bet.getBetMoney();
            String memo = bet.getTeamHome() + " VS " + bet.getTeamAway() + " " + bet.getOname() + " " + bet.getName() + " " + bet.getLine() + "(" + bet.getBetOdds() + " * " + bet.getBetMoney() + ")";

            // 먼저 betting을 저장하여 betId를 만든다.
            inPlayBetRepository.saveAndFlush(bet);

            // 게시판 회원이 아닐 경우만
            if (bet.getRole() == Role.USER) {
                paymentService.addMoney(MoneyCode.BET_INPLAY, bet.getId(), bet.getUserid(), -betMoney, memo);
            }
        } catch (Exception e) {
            log.error("인플레이 베팅에 실패하였습니다.", e);
            return false;
        }

        return true;
    }

    private void paymentWin(Long betId, String userid, long hitMoney, String memo) {
        if (paymentService.notPaidMoney(MoneyCode.WIN_INPLAY, betId, userid)) {
            paymentService.addMoney(MoneyCode.WIN_INPLAY, betId, userid, hitMoney, memo);
        }
    }

    /**
     * 10초 단위로 betting을 체크해 취소인지 가려낸다.
     */
    @Transactional
    public void checkBetList() {
        // 1분 20초 이전 베팅중 enabled = false 인것만 가져온다.
        Iterable<InPlayBet> list = inPlayBetRepository.findAll(BET.enabled.isFalse().and(BET.betDate.before(DateUtils.beforeSeconds(80))));

        for (InPlayBet bet : list) {
            String score = InPlayConfig.getScore().get(bet.getFixtureId());
            if (score == null) {
                cancelBet(bet);
                continue;
            }

            // 스코어가 바뀐것을 검사한다.
            LiveScore liveScore = JsonUtils.toModel(score, LiveScore.class);

            // 현재 스코어가 없다.
            if (liveScore == null || liveScore.getScoreBoard() == null) {
                cancelBet(bet);
                continue;
            }

            String currnetScore = liveScore.getScoreBoard().getResults().get(0).getValue() + "-" + liveScore.getScoreBoard().getResults().get(1).getValue();

            // 스코어가 다르다 취소처리
            if (!bet.getScore().equals(currnetScore)) {
                cancelBet(bet);
                continue;
            }

            // 정상처리
            bet.accept();
        }
    }

    private void cancelBet(InPlayBet bet) {
        bet.notAccept();
        inPlayBetRepository.saveAndFlush(bet);
        paymentService.rollbackMoney(MoneyCode.BET_INPLAY, bet.getId(), bet.getUserid());
    }

    public Page<InPlayBet> page(Pageable pageable, InPlayBetDto.Command command) {
        BooleanBuilder builder = new BooleanBuilder();

        // 아이디 닉네임
        if (StringUtils.notEmpty(command.getUsername())) {
            if (command.isMatch()) { // 정확하게 일치
                builder.and(BET.userid.eq(command.getUsername()).or(BET.nickname.eq(command.getUsername())));
            } else {
                builder.and(BET.userid.contains(command.getUsername()).or(BET.nickname.contains(command.getUsername())));
            }
        }

        if (StringUtils.notEmpty(command.getIp())) {
            builder.and(BET.ip.startsWith(command.getIp()));
        }

        // 회원등급
        if (StringUtils.notEmpty(command.getRole())) {
            if ("USER".equals(command.getRole())) {
                builder.and(BET.role.eq(Role.USER));
            } else {
                builder.and(BET.role.eq(Role.DUMMY));
            }
        }

        switch (command.getResult()) {
            case "disabled": // 베팅접수
                builder.and(BET.enabled.isFalse());
                break;
            case "enabled": // 배팅확정
                builder.and(BET.enabled.isTrue()).and(BET.cancel.isFalse());
                break;
            case "closing": // 종료
                builder.and(BET.closing.isTrue()).and(BET.cancel.isFalse()).and(BET.enabled.isTrue());
                break;
            case "hit": // 적중
                builder.and(BET.closing.isTrue()).and(BET.hitMoney.gt(0)).and(BET.cancel.isFalse()).and(BET.enabled.isTrue());
                break;
            case "noHit": // 미적중
                builder.and(BET.closing.isTrue()).and(BET.hitMoney.eq(0L)).and(BET.cancel.isFalse()).and(BET.enabled.isTrue());
                break;
            case "cancel": // 취소
                builder.and(BET.closing.isTrue()).and(BET.cancel.isTrue()).and(BET.enabled.isTrue());
                break;
            case "":
                builder.and(BET.cancel.isFalse()).and(BET.closing.isFalse());
                break;
        }

        // 베팅날짜
        if (StringUtils.notEmpty(command.getBetDate())) {
            builder.and(BET.betDate.goe(DateUtils.start(command.getBetDate()))).and(BET.betDate.lt(DateUtils.end(command.getBetDate())));
        }

        // 회원아이디
        if (StringUtils.notEmpty(command.getUserid())) {
            builder = builder.and(BET.userid.eq(command.getUserid()));
        }

        PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), getBetOrder(command.getOrderBy()));

        return inPlayBetRepository.findAll(builder, pageRequest);
    }

    private Sort getBetOrder(String orderBy) {
        switch (orderBy) {
            case "betMoney.asc":
                return new Sort("betMoney");
            case "betMoney.desc":
                return new Sort(Sort.Direction.DESC, "betMoney");
            case "hitMoney.asc":
                return new Sort("hitMoney");
            case "hitMoney.desc":
                return new Sort(Sort.Direction.DESC, "hitMoney");
            case "expMoney.asc":
                return new Sort("expMoney");
            case "expMoney.desc":
                return new Sort(Sort.Direction.DESC, "expMoney");
            default:
                return new Sort(Sort.Direction.DESC, "betDate");
        }
    }

    @Transactional
    public AjaxResult goHit(long id) {
        InPlayBet bet = inPlayBetRepository.findOne(id);
        if (bet == null) return new AjaxResult(false, "적중처리에 실패하였습니다.");
        paymentService.rollbackMoney(MoneyCode.WIN_INPLAY, bet.getId(), bet.getUserid());
        // 정상처리
        bet.goHit();
        if (bet.getRole() == Role.USER) {
            String memo = "인플레이 " + bet.getTeamHome() + " VS" + bet.getTeamAway() + " " + bet.getOname() + " " + bet.getName() + " " + bet.getLine() + " : " + bet.getResult();
            paymentWin(bet.getId(), bet.getUserid(), bet.getHitMoney(), memo);
        }
        return new AjaxResult(true, "적중 처리 하였습니다.");
    }

    @Transactional
    public AjaxResult goLose(long id) {
        InPlayBet bet = inPlayBetRepository.findOne(id);
        if (bet == null) return new AjaxResult(false, "미적중 처리에 실패하였습니다.");
        paymentService.rollbackMoney(MoneyCode.WIN_INPLAY, bet.getId(), bet.getUserid());
        // 정상처리
        bet.goLose();
        return new AjaxResult(true, "미적중 처리를 완료 하였습니다.");
    }

    @Transactional
    public AjaxResult goException(long id) {
        InPlayBet bet = inPlayBetRepository.findOne(id);
        if (bet == null) return new AjaxResult(false, "적중특례처리에 실패하였습니다.");
        paymentService.rollbackMoney(MoneyCode.WIN_INPLAY, bet.getId(), bet.getUserid());
        // 정상처리
        bet.goException();

        if (bet.getRole() == Role.USER) {
            String memo = "인플레이 " + bet.getTeamHome() + " VS" + bet.getTeamAway() + " " + bet.getOname() + " " + bet.getName() + " " + bet.getLine() + " : " + bet.getResult();
            paymentWin(bet.getId(), bet.getUserid(), bet.getHitMoney(), memo);
        }

        return new AjaxResult(true, "적중특례 처리를 완료 하였습니다.");
    }

    @Transactional
    public AjaxResult goCancel(long id) {
        InPlayBet bet = inPlayBetRepository.findOne(id);
        if (bet == null) return new AjaxResult(false, "적중특례처리에 실패하였습니다.");
        paymentService.rollbackMoney(MoneyCode.WIN_INPLAY, bet.getId(), bet.getUserid());
        // 정상처리
        bet.goCancel();

        if (bet.getRole() == Role.USER) {
            String memo = "인플레이 " + bet.getTeamHome() + " VS" + bet.getTeamAway() + " " + bet.getOname() + " " + bet.getName() + " " + bet.getLine() + " : " + bet.getResult();
            paymentWin(bet.getId(), bet.getUserid(), bet.getHitMoney(), memo);
        }

        return new AjaxResult(true, "적중특례 처리를 완료 하였습니다.");
    }


    public Page<InPlayBet> userPage(BetDto.UserCommand command, Pageable pageable) {
        return inPlayBetRepository.findAll(BET.userid.eq(command.getUserid())
                .and(BET.betDate.goe(DateUtils.start(command.getSDate())))
                .and(BET.betDate.lt(DateUtils.end(command.getEDate())))
                .and(BET.deleted.isFalse()), pageable);
    }

    @Transactional
    public boolean deleteUser(long id) {
        InPlayBet bet = inPlayBetRepository.findOne(id);
        if (bet == null) return false;

        bet.userDelete();
        return true;
    }

    public Page<InPlayBet> sellerPage(BetDto.SellerCommand command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(BET.role.eq(Role.USER));

        switch (command.getRole()) {
            case "AGENCY4":
                builder.and(BET.agency4.eq(command.getAgency()));
                break;
            case "AGENCY3":
                builder.and(BET.agency3.eq(command.getAgency()));
                break;
            case "AGENCY2":
                builder.and(BET.agency2.eq(command.getAgency()));
                break;
            case "AGENCY1":
                builder.and(BET.agency1.eq(command.getAgency()));
                break;
        }

        // 아이디 닉네임
        if (StringUtils.notEmpty(command.getUsername())) {
            if (command.isMatch()) { // 정확하게 일치
                builder.and(BET.userid.eq(command.getUsername()).or(BET.nickname.eq(command.getUsername())));
            } else {
                builder.and(BET.userid.contains(command.getUsername()).or(BET.nickname.contains(command.getUsername())));
            }
        }

        return inPlayBetRepository.findAll(builder, pageable);
    }
}
