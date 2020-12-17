package spoon.event.service;

import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.*;
import spoon.config.domain.Config;
import spoon.config.domain.EventConfig;
import spoon.config.entity.JsonConfig;
import spoon.config.repository.JsonRepository;
import spoon.event.domain.DailyCalendar;
import spoon.event.domain.DailyDto;
import spoon.event.entity.DailyPayment;
import spoon.event.entity.QDailyPayment;
import spoon.event.repository.DailyPaymentRepository;
import spoon.mapper.DailyEventMapper;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.payment.domain.PointCode;
import spoon.payment.service.PaymentService;
import spoon.support.web.AjaxResult;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class DailyEventServiceImpl implements DailyEventService {

    private JsonRepository jsonRepository;

    private PaymentService paymentService;

    private MemberService memberService;

    private DailyPaymentRepository dailyPaymentRepository;

    private DailyEventMapper dailyEventMapper;

    @Override
    public Page<DailyPayment> paymentPage(DailyDto.Command command, Pageable pageable) {
        QDailyPayment q = QDailyPayment.dailyPayment;

        BooleanBuilder builder = new BooleanBuilder(q.enabled.isTrue());

        if (StringUtils.notEmpty(command.getSdate())) {
            builder.and(q.sdate.eq(command.getSdate().replaceAll("\\.", "-")));
        }

        if (StringUtils.notEmpty(command.getUserid())) {
            builder.and(q.userid.eq(command.getUserid()));
        }

        return dailyPaymentRepository.findAll(builder, pageable);
    }

    @Transactional
    @Override
    public boolean updateConfig(EventConfig eventConfig) {
        JsonConfig jsonConfig = new JsonConfig("event");
        jsonConfig.setJson(JsonUtils.toString(eventConfig));
        jsonRepository.saveAndFlush(jsonConfig);
        Config.setEventConfig(eventConfig);

        return true;
    }

    @Transactional
    @Override
    public AjaxResult payment(Long id) {
        AjaxResult result = new AjaxResult();

        try {
            DailyPayment dp = dailyPaymentRepository.findOne(id);

            if (dp == null) {
                result.setMessage("삭제된 내역입니다. 다시 확인하여 주세요.");
                return result;
            }

            if (dp.isCancel()) {
                result.setMessage("지급 취소된 당첨 입니다.");
                return result;
            }

            if (dp.isClosing()) {
                result.setMessage("이미 지급 완료 되었습니다.");
                return result;
            }

            dp.setClosing(true);
            dailyPaymentRepository.saveAndFlush(dp);
            paymentService.addPoint(PointCode.EVENT, dp.getId(), dp.getUserid(), dp.getAmount(), dp.getMemo());
            result.setSuccess(true);
            result.setMessage("이벤트 지급을 완료 하였습니다.");

        } catch (RuntimeException e) {
            log.error("데일리 이벤트 지급에 실패하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            result.setMessage("베팅에 실패하였습니다. 잠시후 다시 시도하세요.");
        }

        return result;
    }

    @Transactional
    @Override
    public AjaxResult cancel(Long id) {
        try {
            DailyPayment dp = dailyPaymentRepository.findOne(id);
            if (dp.isClosing()) {
                return new AjaxResult(false, "이미 처리된 당첨입니다.");
            }

            dp.setClosing(true);
            dp.setCancel(true);
            dailyPaymentRepository.saveAndFlush(dp);

        } catch (RuntimeException e) {
            log.error("데일리 이벤트 지급에 실패하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new AjaxResult(false, "취소처리에 실패하였습니다. 잠시후 다시 시도하세요.");
        }

        return new AjaxResult(true, "취소처리를 완료하였습니다.");
    }

    @Transactional
    @Override
    public void checkEvent(String userid) {
        // 오늘 전체 충전 금액을 찾아서 이벤트 기준 머니보다 크지 않다면 이벤트를 하지 않는다.
        String today = DateUtils.format(new Date(), "yyyy-MM-dd");
        long todayCash = dailyEventMapper.todayCash(userid, today + " 00:00:00.000", today + " 23:59:59.997");
        if (todayCash < Config.getEventConfig().getAmount()) {
            return;
        }

        // 이벤트에 오늘 기록이 없다면 기록한다.
        long count = dailyEventMapper.hasCheckDaily(userid, DateUtils.format(new Date(), "yyyy-MM-dd"));
        if (count == 0) {
            addDaily(userid);
        }
    }

    @Override
    public List<DailyCalendar> calendar(String month) {
        return dailyEventMapper.calendar(WebUtils.userid(), month);
    }

    @Override
    public Iterable<DailyPayment> dailyPayment() {
        QDailyPayment q = QDailyPayment.dailyPayment;
        return dailyPaymentRepository.findAll(q.userid.eq(WebUtils.userid()).and(q.deleted.isFalse()));
    }

    @Override
    public int week(String month) {
        Date today = DateUtils.parse(month + "-01", "yyyy-MM-dd");
        return DateUtils.week(today);
    }

    @Override
    public String prev(String month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.parse(month + "-01", "yyyy-MM-dd"));
        calendar.add(Calendar.DATE, -2);
        return DateUtils.format(calendar.getTime(), "yyyy-MM");
    }

    @Override
    public String next(String month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.parse(month + "-01", "yyyy-MM-dd"));
        calendar.add(Calendar.DATE, 35);
        return DateUtils.format(calendar.getTime(), "yyyy-MM");
    }

    @Transactional
    @Override
    public AjaxResult enabled(Long id) {
        DailyPayment dailyPayment = dailyPaymentRepository.findOne(id);
        if (dailyPayment.getUserid().equals(WebUtils.userid())) {
            dailyPayment.setEnabled(true);
            dailyPaymentRepository.saveAndFlush(dailyPayment);
            return new AjaxResult(true, "이벤트 당첨금을 신청하였습니다.");
        }
        return new AjaxResult(false, "이벤트 당첨금을 신청 할 수 없습니다.");
    }

    @Transactional
    @Override
    public AjaxResult deleted(Long id) {
        DailyPayment dailyPayment = dailyPaymentRepository.findOne(id);
        if (dailyPayment.getUserid().equals(WebUtils.userid())) {
            dailyPayment.setDeleted(true);
            dailyPaymentRepository.saveAndFlush(dailyPayment);
            return new AjaxResult(true, "내역을 삭제하였습니다.");
        }
        return new AjaxResult(false, "내역을 삭제할 수 없습니다.");
    }

    private void addDaily(String userid) {
        Member member = memberService.getMember(userid);
        String today = DateUtils.format(new Date(), "yyyy-MM-dd");

        Integer daily = dailyEventMapper.getYesterday(userid, DateUtils.format(DateUtils.beforeDays(1), "yyyy-MM-dd"));
        if (daily == null) daily = 0;

        int maxDaily = Arrays.stream(Config.getEventConfig().getDaily()).max().getAsInt();
        if (daily >= maxDaily) daily = 0;

        dailyEventMapper.addDaily(userid, member.getNickname(), today, daily + 1);

        int[] eventDaily = Config.getEventConfig().getDaily();
        int index = -1;
        int cnt = 0;
        for (int i : eventDaily) {
            if (i > 0 && i == daily + 1) {
                index = cnt;
                break;
            }
            cnt++;
        }

        if (index < 0) return;

        long money = Config.getEventConfig().getMoney()[index];
        int day = Config.getEventConfig().getDaily()[index];
        DailyPayment dailyPayment = new DailyPayment();
        dailyPayment.setUserid(member.getUserid());
        dailyPayment.setNickname(member.getNickname());
        dailyPayment.setAgency1(member.getAgency1());
        dailyPayment.setAgency2(member.getAgency2());
        dailyPayment.setAgency3(member.getAgency3());
        dailyPayment.setAgency4(member.getAgency4());
        dailyPayment.setAccount(member.getAccount());
        dailyPayment.setBank(member.getBank());
        dailyPayment.setDepositor(member.getDepositor());
        dailyPayment.setMemo(day + "일간 연속 출석체크");
        dailyPayment.setAmount(money);
        dailyPayment.setSdate(today);

        dailyPaymentRepository.saveAndFlush(dailyPayment);
    }

}
