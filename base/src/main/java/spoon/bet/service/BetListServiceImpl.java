package spoon.bet.service;

import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import spoon.bet.domain.BetDto;
import spoon.bet.entity.Bet;
import spoon.bet.entity.BetItem;
import spoon.bet.entity.QBet;
import spoon.bet.entity.QBetItem;
import spoon.bet.repository.BetItemRepository;
import spoon.bet.repository.BetRepository;
import spoon.common.utils.DateUtils;
import spoon.common.utils.StringUtils;
import spoon.game.domain.GameCode;
import spoon.game.domain.MenuCode;
import spoon.member.domain.Role;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@AllArgsConstructor
@Service
public class BetListServiceImpl implements BetListService {

    private BetRepository betRepository;

    private BetItemRepository betItemRepository;

    private static QBet q = QBet.bet;

    @Override
    public Page<Bet> adminPage(BetDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        // 아이디 닉네임
        if (StringUtils.notEmpty(command.getUsername())) {
            if (command.isMatch()) { // 정확하게 일치
                builder.and(q.userid.eq(command.getUsername()).or(q.nickname.eq(command.getUsername())));
            } else {
                builder.and(q.userid.like("%" + command.getUsername() + "%").or(q.nickname.like("%" + command.getUsername() + "%")));
            }
        }

        if (StringUtils.notEmpty(command.getIp())) {
            builder.and(q.ip.like(command.getIp() + "%"));
        }

        // 메뉴타입
        if (command.getMenuCode() != null) {
            switch (command.getMenuCode()) {
                case SPORTS:
                    builder.and(q.menuCode.in(MenuCode.MATCH, MenuCode.HANDICAP, MenuCode.SPECIAL, MenuCode.LIVE, MenuCode.CROSS));
                    break;
                default:
                    builder.and(q.menuCode.eq(command.getMenuCode()));
            }
        }

        // 회원등급
        if (StringUtils.notEmpty(command.getRole())) {
            if ("USER".equals(command.getRole())) {
                builder.and(q.role.in(Role.USER, Role.AGENCY1, Role.AGENCY2, Role.AGENCY3, Role.AGENCY4));
            } else {
                builder.and(q.role.eq(Role.DUMMY));
            }
        }

        switch (command.getResult()) {
            case "ing": // 적중가능
                builder.and(q.closing.isFalse()).and(q.loseCount.eq(0)).and(q.cancel.isFalse());
                break;
            case "ready": // 대기
                builder.and(q.closing.isFalse()).and(q.cancel.isFalse());
                break;
            case "closing": // 종료
                builder.and(q.closing.isTrue()).and(q.cancel.isFalse());
                break;
            case "hit": // 적중
                builder.and(q.closing.isTrue()).and(q.hitMoney.gt(0)).and(q.cancel.isFalse());
                break;
            case "noHit": // 미적중
                builder.and(q.closing.isTrue()).and(q.hitMoney.eq(0L)).and(q.cancel.isFalse());
                break;
            case "cancel": // 취소
                builder.and(q.closing.isTrue()).and(q.cancel.isTrue());
                break;
        }

        // 베팅날짜
        if (StringUtils.notEmpty(command.getBetDate())) {
            builder.and(q.betDate.goe(DateUtils.start(command.getBetDate()))).and(q.betDate.lt(DateUtils.end(command.getBetDate())));
        }

        // 회원아이디
        if (StringUtils.notEmpty(command.getUserid())) {
            builder = builder.and(q.userid.eq(command.getUserid()));
        }

        PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), getBetOrder(command.getOrderBy()));

        return betRepository.findAll(builder, pageRequest);
    }

    @Override
    public Page<Bet> userPage(BetDto.UserCommand command, Pageable pageable) {
        return betRepository.findAll(q.userid.eq(command.getUserid())
                .and(q.betDate.goe(DateUtils.start(command.getSDate())))
                .and(q.betDate.lt(DateUtils.end(command.getEDate())))
                .and(q.deleted.isFalse()), pageable);
    }

    @Override
    public Page<Bet> sellerPage(BetDto.SellerCommand command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.role.in(Role.USER, Role.AGENCY1, Role.AGENCY2, Role.AGENCY3, Role.AGENCY4));

        switch (command.getRole()) {
            case "AGENCY4":
                builder.and(q.agency4.eq(command.getAgency()));
                break;
            case "AGENCY3":
                builder.and(q.agency3.eq(command.getAgency()));
                break;
            case "AGENCY2":
                builder.and(q.agency2.eq(command.getAgency()));
                break;
            case "AGENCY1":
                builder.and(q.agency1.eq(command.getAgency()));
                break;
        }

        if (StringUtils.notEmpty(command.getUsername())) {
            if (command.isMatch()) {
                builder.and(q.userid.eq(command.getUsername()).or(q.nickname.eq(command.getUsername())));
            } else {
                builder.and(q.userid.like("%" + command.getUsername() + "%").or(q.nickname.eq("%" + command.getUsername() + "%")));
            }
        }

        return betRepository.findAll(builder, pageable);
    }

    @Override
    public List<Bet> listByGame(Long gameId, String betTeam) {
        QBetItem q = QBetItem.betItem;
        Iterable<BetItem> items = betItemRepository.findAll(q.gameId.eq(gameId).and(q.gameCode.in(GameCode.MATCH, GameCode.HANDICAP, GameCode.OVER_UNDER)).and(q.role.in(Role.USER, Role.AGENCY1, Role.AGENCY2, Role.AGENCY3, Role.AGENCY4)).and(q.betTeam.eq(betTeam)));
        return StreamSupport.stream(items.spliterator(), false).map(BetItem::getBet).collect(Collectors.toList());
    }

    @Override
    public Iterable<BetItem> listByGame(MenuCode menuCode, String sdate) {
        QBetItem q = QBetItem.betItem;
        return betItemRepository.findAll(q.menuCode.eq(menuCode).and(q.groupId.eq(sdate)).and(q.role.in(Role.USER, Role.AGENCY1, Role.AGENCY2, Role.AGENCY3, Role.AGENCY4)));
    }

    @Override
    public Iterable<Bet> listByBoard(Long[] betIds) {
        return betRepository.findAll(q.id.in(betIds));
    }

    @Override
    public Iterable<Bet> listByBoard(String betId, String userid) {
        String[] ids = betId.split(",");
        Long[] betIds = new Long[ids.length];
        int index = 0;

        for (String id : ids) {
            betIds[index++] = Long.parseLong(id.trim());
        }

        return betRepository.findAll(q.id.in(betIds).and(q.userid.eq(userid)));
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
}
