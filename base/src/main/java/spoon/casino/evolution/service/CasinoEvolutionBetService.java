package spoon.casino.evolution.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spoon.casino.evolution.domain.CasinoEvolutionBetDto;
import spoon.casino.evolution.entity.CasinoEvolutionBet;
import spoon.casino.evolution.entity.CasinoEvolutionId;
import spoon.casino.evolution.entity.QCasinoEvolutionId;
import spoon.casino.evolution.repository.CasinoEvolutionDao;
import spoon.casino.evolution.repository.CasinoEvolutionIdRepository;
import spoon.common.utils.StringUtils;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CasinoEvolutionBetService {

    private final CasinoEvolutionDao casinoEvolutionDao;

    private final MemberService memberService;

    private final CasinoEvolutionIdRepository casinoEvolutionIdRepository;

    private final ApplicationEventPublisher eventPublisher;

    private static final Map<String, String> CASINO_ID = new HashMap<>();

    private static final QCasinoEvolutionId CASINO_EVOLUTION_ID = QCasinoEvolutionId.casinoEvolutionId;

    @PostConstruct
    public void init() {
        casinoEvolutionIdRepository.findAll().forEach(x -> CASINO_ID.put(x.getCasinoid(), x.getUserid()));
    }

    @Transactional
    public void bet(CasinoEvolutionBetDto.Bet transaction) {
        boolean exists = casinoEvolutionDao.exists(transaction.getId());
        if (exists) return;

        String userid = this.findUserid(transaction.getUser().getUsername());
        if (StringUtils.empty(userid)) return;

        Member member = memberService.getMember(userid);
        if (member == null) return;

        CasinoEvolutionBet bet = CasinoEvolutionBet.bet(transaction, member);

        casinoEvolutionDao.save(bet);
        eventPublisher.publishEvent(bet);
    }

    @Transactional
    public void win(CasinoEvolutionBetDto.Bet transaction) {
        //casinoEvolutionDao.getOne(transaction.getId()).ifPresent(bet -> bet.win(transaction));
        String userid = this.findUserid(transaction.getUser().getUsername());
        if (StringUtils.empty(userid)) return;

        Member member = memberService.getMember(userid);
        if (member == null) return;

        CasinoEvolutionBet bet = CasinoEvolutionBet.win(transaction, member);

        casinoEvolutionDao.save(bet);
    }

    private String findUserid(String username) {
        if (CASINO_ID.containsKey(username)) return CASINO_ID.get(username);

        CasinoEvolutionId id = casinoEvolutionIdRepository.findOne(CASINO_EVOLUTION_ID.casinoid.eq(username));
        if (id == null) return "";

        return id.getUserid();
    }
}
