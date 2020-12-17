package spoon.event.service;

import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.banking.service.BankingService;
import spoon.common.utils.*;
import spoon.config.domain.Config;
import spoon.config.domain.EventLottoConfig;
import spoon.config.entity.JsonConfig;
import spoon.config.repository.JsonRepository;
import spoon.event.domain.LottoDto;
import spoon.event.entity.LottoPayment;
import spoon.event.entity.QLottoPayment;
import spoon.event.repository.LottoPaymentRepository;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.payment.domain.PointCode;
import spoon.payment.service.PaymentService;
import spoon.support.web.AjaxResult;

import java.text.NumberFormat;
import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class LottoEventServiceImpl implements LottoEventService {

    private JsonRepository jsonRepository;

    private LottoPaymentRepository lottoPaymentRepository;

    private BankingService bankingService;

    private MemberService memberService;

    private PaymentService paymentService;

    @Transactional
    @Override
    public boolean updateConfig(EventLottoConfig eventLottoConfig) {
        JsonConfig jsonConfig = new JsonConfig("event_lotto");
        jsonConfig.setJson(JsonUtils.toString(eventLottoConfig));
        jsonRepository.saveAndFlush(jsonConfig);
        Config.setEventLottoConfig(eventLottoConfig);

        return true;
    }

    @Transactional
    @Override
    public AjaxResult lotto() {
        AjaxResult result = new AjaxResult();

        String userid = WebUtils.userid();

        // 오늘 입금 내역이 있는지 조사한다.
        long total = bankingService.getTodayDeposit(userid);
        if (total < Config.getEventLottoConfig().getDeposit()) {
            result.setMessage("이벤트 참여 최소 충전금은 " + NumberFormat.getIntegerInstance().format(Config.getEventLottoConfig().getDeposit()) + "원 입니다.\n\n충전 후 이벤트에 참여하여 주세요.");
            return result;
        }

        // 오늘 로또를 했는지 조사한다.
        if (isPayment(userid)) {
            result.setMessage("이미 이벤트에 참여 하였습니다.\n\n즉석복권 이벤트는 하루 한번만 가능 합니다.");
            return result;
        }

        // 당첨금을 구한다.
        try {
            double randNumber = Math.random();
            double idx = (int) (randNumber * 100) + 1;
            int rank = 0;
            int i = 0;
            long money = 0;
            int sum = 0;
            int max = (int) total * Config.getEventLottoConfig().getMax() / 100;
            int index = 0;

            for (; i < Config.getEventLottoConfig().getMoney().length; i++) {
                if (Config.getEventLottoConfig().getMoney()[i] <= max || i == 9) {
                    sum += Config.getEventLottoConfig().getRate()[i];
                } else {
                    index++;
                }
            }

            for (; index < Config.getEventLottoConfig().getRate().length; index++) {
                idx -= (Config.getEventLottoConfig().getRate()[index] * 100D) / sum;
                //log.error("idx : {}", idx);
                if (idx < 0 || index == 9) {
                    money = Config.getEventLottoConfig().getMoney()[index];
                    rank = index;
                    break;
                }
            }
            rank++;

            Member member = memberService.getMember(userid);
            String today = DateUtils.format(new Date(), "yyyy-MM-dd");

            LottoPayment lottoPayment = new LottoPayment();
            lottoPayment.setUserid(member.getUserid());
            lottoPayment.setNickname(member.getNickname());
            lottoPayment.setAgency1(member.getAgency1());
            lottoPayment.setAgency2(member.getAgency2());
            lottoPayment.setAgency3(member.getAgency3());
            lottoPayment.setAgency4(member.getAgency4());
            lottoPayment.setAccount(member.getAccount());
            lottoPayment.setBank(member.getBank());
            lottoPayment.setDepositor(member.getDepositor());
            lottoPayment.setMemo("즉석복권 이벤트 " + rank + "등 " + money + "원에 당첨");
            lottoPayment.setAmount(money);
            lottoPayment.setSdate(today);
            lottoPayment.setRegDate(new Date());

            lottoPaymentRepository.saveAndFlush(lottoPayment);
            paymentService.addPoint(PointCode.EVENT, lottoPayment.getId(), lottoPayment.getUserid(), lottoPayment.getAmount(), lottoPayment.getMemo());
            result.setSuccess(true);
            result.setValue(String.valueOf(money));
            result.setMessage(lottoPayment.getMemo() + " 되었습니다.");
        } catch (RuntimeException e) {
            log.error("즉석복권 이벤트 지급에 실패하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            result.setMessage("즉석복권 이벤트 참여에 실패 하였습니다.\n\n잠시 후 다시 이용하시기 바랍니다.");
        }

        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<LottoPayment> paymentPage(LottoDto.Command command, Pageable pageable) {
        QLottoPayment q = QLottoPayment.lottoPayment;

        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.notEmpty(command.getSdate())) {
            builder.and(q.sdate.eq(command.getSdate().replaceAll("\\.", "-")));
        }

        if (StringUtils.notEmpty(command.getUserid())) {
            builder.and(q.userid.eq(command.getUserid()));
        }
        return lottoPaymentRepository.findAll(builder, pageable);
    }

    private boolean isPayment(String userid) {
        QLottoPayment q = QLottoPayment.lottoPayment;
        String today = DateUtils.format(new Date(), "yyyy-MM-dd");
        return lottoPaymentRepository.count(q.userid.eq(userid)
                .and(q.sdate.eq(today))) > 0;
    }
}
