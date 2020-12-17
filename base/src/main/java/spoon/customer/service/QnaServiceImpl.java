package spoon.customer.service;

import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.bet.service.BetListService;
import spoon.common.utils.DateUtils;
import spoon.common.utils.ErrorUtils;
import spoon.common.utils.StringUtils;
import spoon.common.utils.WebUtils;
import spoon.customer.domain.QnaDto;
import spoon.customer.entity.QQna;
import spoon.customer.entity.Qna;
import spoon.customer.repository.QnaRepository;
import spoon.member.domain.User;
import spoon.member.service.MemberService;
import spoon.monitor.service.MonitorService;
import spoon.support.web.AjaxResult;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class QnaServiceImpl implements QnaService {

    private QnaRepository qnaRepository;

    private MemberService memberService;

    private MonitorService monitorService;

    private BetListService betListService;

    private static QQna q = QQna.qna;

    @Override
    public Page<Qna> page(String userid, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.userid.eq(userid).and(q.hidden.isFalse()).and(q.enabled.isTrue()));
        builder.and(q.reDate.isNull().or(q.reDate.after(DateUtils.beforeHours(12))));
        return qnaRepository.findAll(builder, pageable);
    }

    @Override
    public Page<Qna> page(QnaDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.notEmpty(command.getUsername())) {
            if (command.isMatch()) {
                builder.and((q.userid.eq(command.getUsername())).or(q.nickname.eq(command.getUsername())));
            } else {
                builder.and((q.userid.like("%" + command.getUsername() + "%")).or(q.nickname.like("%" + command.getUsername() + "%")));
            }
        }

        if (StringUtils.notEmpty(command.getSearchValue())) {
            switch (command.getSearchType()) {
                case "title":
                    builder.and(q.title.like("%" + command.getSearchValue() + "%"));
                    break;
                case "contents":
                    builder.and(q.contents.like("%" + command.getSearchValue() + "%"));
                    break;
                case "agency":
                    builder.and(
                            q.agency1.contains(command.getSearchValue())
                                    .or(q.agency2.contains(command.getSearchValue()))
                                    .or(q.agency3.contains(command.getSearchValue()))
                                    .or(q.agency4.contains(command.getSearchValue()))
                    );
                    break;
            }
        }

        return qnaRepository.findAll(builder, pageable);
    }

    @Transactional
    @Override
    public Qna getOne(Long id) {
        Qna qna = qnaRepository.findOne(id);
        qna.setAlarm(false);
        qnaRepository.saveAndFlush(qna);
        monitorService.checkQna();

        if (qna.isBet()) {
            qna.setBets(betListService.listByBoard(Arrays.stream(qna.getBetId()).mapToObj(Long::toString).collect(Collectors.joining(",")), qna.getUserid()));
        }

        return qna;
    }

    @Transactional
    @Override
    public boolean add(QnaDto.Add add) {
        User user = memberService.getUser(WebUtils.userid());
        if (user == null) return false;

        try {
            Qna qna = new Qna(user);
            qna.setTitle(add.getTitle());
            qna.setContents(add.getContents());
            if (StringUtils.notEmpty(add.getBetId())) {
                qna.setBetId(Arrays.stream(add.getBetId().split(",")).mapToLong(Long::valueOf).toArray());
            }
            qna.setBet(StringUtils.notEmpty(add.getBetId()));
            qnaRepository.saveAndFlush(qna);
        } catch (RuntimeException e) {
            log.error("고객상담 등록에 실패하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }

        monitorService.checkQna();
        return true;
    }

    @Transactional
    @Override
    public boolean reply(QnaDto.Reply reply) {
        Qna qna = qnaRepository.findOne(reply.getId());
        qna.setReTitle(reply.getReTitle());
        qna.setReply(reply.getReply());
        qna.setRe(true);
        qna.setReDate(new Date());
        qna.setWorker(WebUtils.userid());
        qnaRepository.saveAndFlush(qna);
        monitorService.checkQna();
        return true;
    }

    @Transactional
    @Override
    public boolean delete(Long id) {
        qnaRepository.delete(id);
        monitorService.checkQna();
        return true;
    }

    @Transactional
    @Override
    public AjaxResult view(Long id) {
        Qna qna = qnaRepository.findOne(id);
        AjaxResult result = new AjaxResult(true);
        if (qna.isRe()) { // 답변이 있는것만
            qna.setChecked(true);
            result.setValue("true");
        }
        return result;
    }

    @Transactional
    @Override
    public AjaxResult hidden(Long id) {
        Qna qna = qnaRepository.findOne(id);
        qna.setHidden(true);
        return new AjaxResult(true);
    }

    @Transactional
    @Override
    public AjaxResult account() {
        AjaxResult result = new AjaxResult();
        User user = memberService.getUser(WebUtils.userid());
        if (user == null) {
            result.setMessage("계좌문의에 실패하였습니다.\n\n잠시 후 다시 이용하세요.");
            return result;
        }

        // 답변하지 않고 삭제하지 않은 계좌번호 요청
        result.setSuccess(true);
        long count = qnaRepository.count(q.userid.eq(user.getUserid()).and(q.hidden.isFalse()).and(q.re.isFalse()).and(q.title.eq("계좌문의")));
        if (count > 0) {
            result.setMessage("현재 문의중인 계좌문의 요청이 있습니다.");
            return result;
        }

        Qna qna = new Qna(user);
        qna.setTitle("계좌문의");
        qna.setContents("입금 계좌번호 발급 문의합니다");
        qnaRepository.saveAndFlush(qna);
        monitorService.checkQna();

        return result;
    }
}
