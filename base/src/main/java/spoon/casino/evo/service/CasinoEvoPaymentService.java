package spoon.casino.evo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spoon.casino.evo.domain.CasinoEvoCmd;
import spoon.casino.evo.domain.CasinoEvoResult;
import spoon.casino.evo.entity.CasinoEvoExchange;
import spoon.casino.evo.entity.CasinoEvoUser;
import spoon.casino.evo.entity.QCasinoEvoExchange;
import spoon.casino.evo.entity.QCasinoEvoUser;
import spoon.casino.evo.repository.CasinoEvoExchangeRepository;
import spoon.casino.evo.repository.CasinoEvoUseridRepository;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.payment.domain.MoneyCode;
import spoon.payment.service.PaymentService;

@RequiredArgsConstructor
@Service
public class CasinoEvoPaymentService {

    private final PaymentService paymentService;

    private final MemberService memberService;

    private final CasinoEvoUseridRepository casinoEvoUseridRepository;

    private final CasinoEvoExchangeRepository casinoEvoExchangeRepository;

    private static final QCasinoEvoUser EVO_USER = QCasinoEvoUser.casinoEvoUser;

    private static final QCasinoEvoExchange EXCHANGE = QCasinoEvoExchange.casinoEvoExchange;

    @Transactional
    public CasinoEvoResult casinoBet(CasinoEvoCmd.Change change) {
        CasinoEvoUser user = casinoEvoUseridRepository.findOne(EVO_USER.casinoId.eq(change.getUserid()));
        Member member = memberService.getMember(user.getUserid());

        CasinoEvoExchange exchange = new CasinoEvoExchange(member);
        exchange.setCasinoId(change.getUserid());
        exchange.setAmount(-change.getAmount());
        exchange.setGameType(change.getGameType());
        exchange.setTrans(change.getTransaction());


        exchange = casinoEvoExchangeRepository.saveAndFlush(exchange);
        paymentService.addMoney(MoneyCode.CASINO_BET, exchange.getId(), user.getUserid(), -change.getAmount(), change.getUserid() + " 베팅 - transaction: " + change.getTransaction() + ", gameType: " + change.getGameType());
        CasinoEvoApiService.MONEY.put(member.getUserid(), member.getMoney() - change.getAmount());
        return CasinoEvoResult.success();
    }

    @Transactional
    public CasinoEvoResult casinoWin(CasinoEvoCmd.Change change) {
        CasinoEvoUser user = casinoEvoUseridRepository.findOne(EVO_USER.casinoId.eq(change.getUserid()));
        Member member = memberService.getMember(user.getUserid());

        CasinoEvoExchange exchange = new CasinoEvoExchange(member);
        exchange.setCasinoId(change.getUserid());
        exchange.setAmount(change.getAmount());
        exchange.setGameType(change.getGameType());
        exchange.setTrans(change.getTransaction());

        exchange = casinoEvoExchangeRepository.saveAndFlush(exchange);

        paymentService.addMoney(MoneyCode.CASINO_WIN, exchange.getId(), user.getUserid(), change.getAmount(), change.getUserid() + " 적중 - transaction: " + change.getTransaction() + ", gameType: " + change.getGameType());
        CasinoEvoApiService.MONEY.put(member.getUserid(), member.getMoney() + change.getAmount());
        return CasinoEvoResult.success();
    }

    @Transactional
    public CasinoEvoResult casinoRefund(CasinoEvoCmd.Change change) {
        CasinoEvoUser user = casinoEvoUseridRepository.findOne(EVO_USER.casinoId.eq(change.getUserid()));
        Member member = memberService.getMember(user.getUserid());

        CasinoEvoExchange exchange = new CasinoEvoExchange(member);
        exchange.setCasinoId(change.getUserid());
        exchange.setAmount(change.getAmount());
        exchange.setGameType(change.getGameType());
        exchange.setTrans(change.getTransaction());

        exchange = casinoEvoExchangeRepository.saveAndFlush(exchange);

        paymentService.addMoney(MoneyCode.CASINO_REFUND, exchange.getId(), user.getUserid(), change.getAmount(), change.getUserid() + " 적중 - transaction: " + change.getTransaction() + ", gameType: " + change.getGameType());
        CasinoEvoApiService.MONEY.put(member.getUserid(), member.getMoney() + change.getAmount());
        return CasinoEvoResult.success();
    }

    @Transactional
    public CasinoEvoResult casinoCancel(CasinoEvoCmd.Cancel cancel) {
        CasinoEvoExchange exchange = casinoEvoExchangeRepository.findOne(EXCHANGE.trans.eq(cancel.getTransaction()));

        if (exchange == null) {
            return CasinoEvoResult.error("내역을 찾을 수 없습니다.");
        }
        exchange.setCancel(true);

        Member member = memberService.getMember(exchange.getUserid());
        paymentService.addMoney(MoneyCode.CASINO_CANCEL, exchange.getId(), exchange.getUserid(), exchange.getAmount(), exchange.getUserid() + " 적중 - transaction: " + cancel.getTransaction() + ", gameType: " + exchange.getGameType());
        CasinoEvoApiService.MONEY.put(member.getUserid(), member.getMoney() + exchange.getAmount());
        return CasinoEvoResult.success();
    }
}
