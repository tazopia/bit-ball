package spoon.customer.service;

import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spoon.common.utils.DateUtils;
import spoon.common.utils.ErrorUtils;
import spoon.common.utils.StringUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.customer.domain.MemoDto;
import spoon.customer.entity.Memo;
import spoon.customer.entity.QMemo;
import spoon.customer.repository.MemoRepository;
import spoon.mapper.MemoMapper;
import spoon.member.domain.User;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Service
public class MemoServiceImpl implements MemoService {

    private MemoRepository memoRepository;

    private MemoMapper memoMapper;

    private MemberService memberService;

    private static QMemo q = QMemo.memo;

    @Override
    public Page<Memo> page(MemoDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.enabled.isTrue());

        if (StringUtils.notEmpty(command.getUsername())) {
            if (command.isMatch()) {
                builder.and(q.userid.eq(command.getUsername()).or(q.nickname.eq(command.getUsername())));
            } else {
                builder.and(q.userid.like("%" + command.getUsername() + "%").or(q.nickname.eq("%" + command.getUsername() + "%")));
            }
        }

        if (StringUtils.notEmpty(command.getChecked())) {
            if ("Y".equals(command.getChecked())) {
                builder.and(q.checked.isTrue());
            } else if ("N".equals(command.getChecked())) {
                builder.and(q.checked.isFalse());
            }
        }

        if (StringUtils.notEmpty(command.getAgency1())) {
            builder.and(q.agency1.eq(command.getAgency1()));
        }

        if (StringUtils.notEmpty(command.getAgency2())) {
            builder.and(q.agency2.eq(command.getAgency2()));
        }

        if (StringUtils.notEmpty(command.getAgency3())) {
            builder.and(q.agency3.eq(command.getAgency3()));
        }

        if (StringUtils.notEmpty(command.getAgency4())) {
            builder.and(q.agency4.eq(command.getAgency4()));
        }

        if (StringUtils.notEmpty(command.getRegDate())) {
            builder.and(q.regDate.goe(DateUtils.start(command.getRegDate()))).and(q.regDate.lt(DateUtils.end(command.getRegDate())));
        }

        return memoRepository.findAll(builder, pageable);
    }

    @Override
    public Page<Memo> page(String userid, Pageable pageable) {
        return memoRepository.findAll(q.userid.eq(userid).and(q.hidden.isFalse()).and(q.enabled.isTrue()), pageable);
    }

    @Transactional
    @Override
    public AjaxResult addOne(MemoDto.One one) {
        User user = memberService.getUser(one.getUserid());
        Memo memo = new Memo(user);
        memo.setTitle(one.getTitle());
        memo.setContents(one.getContents());
        memo.setWorker(WebUtils.userid());
        memo.setIp(WebUtils.ip());
        memoRepository.saveAndFlush(memo);

        return new AjaxResult(true, "쪽지를 발송 하였습니다.");
    }

    @Transactional
    @Override
    public void addJoin(User user) {
        Memo memo = new Memo(user);

        String title = Config.getJoinMemo().getTitle()
                .replaceAll("#아이디#", user.getUserid())
                .replaceAll("#닉네임#", user.getNickname())
                .replaceAll("#회사#", Config.getSiteConfig().getCompanyName());
        String contents = Config.getJoinMemo().getContents()
                .replaceAll("#아이디#", user.getUserid())
                .replaceAll("#닉네임#", user.getNickname())
                .replaceAll("#회사#", Config.getSiteConfig().getCompanyName());

        memo.setTitle(title);
        memo.setContents(contents);
        memo.setWorker(user.getUserid());
        memo.setIp(user.getLoginIp());
        memoRepository.saveAndFlush(memo);
    }

    @Transactional
    @Override
    public boolean add(MemoDto.Add add) {
        try {
            add.setWorker(WebUtils.userid());
            add.setIp(WebUtils.ip());
            memoMapper.addMemo(add);
        } catch (RuntimeException e) {
            log.warn("쪽지 보내기에 실패 하였습니다. - {}", e.getMessage());
            ErrorUtils.trace(e.getStackTrace());
            return false;
        }
        return true;
    }

    @Transactional
    @Override
    public AjaxResult view(Long id) {
        Memo memo = memoRepository.findOne(id);
        memo.setChecked(true);
        return new AjaxResult(true);
    }

    @Transactional
    @Override
    public AjaxResult delete(Long id) {
        Memo memo = memoRepository.findOne(id);
        memo.setHidden(true);
        return new AjaxResult(true);
    }

    @Transactional
    @Override
    public AjaxResult delete(Long[] id) {
        Iterable<Memo> memos = memoRepository.findAll(q.id.in(id));
        memos.forEach(x -> x.setHidden(true));
        return new AjaxResult(true);
    }

    @Transactional
    @Override
    public AjaxResult deleteAdmin(Long[] memoIds) {
        memoMapper.deleteMemo(memoIds);
        return new AjaxResult(true, "");
    }
}
