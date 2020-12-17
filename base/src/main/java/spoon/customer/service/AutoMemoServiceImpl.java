package spoon.customer.service;

import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spoon.config.domain.Config;
import spoon.customer.domain.AutoMemoDto;
import spoon.customer.entity.AutoMemo;
import spoon.customer.entity.QAutoMemo;
import spoon.customer.repository.AutoMemoRepository;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Service
public class AutoMemoServiceImpl implements AutoMemoService {

    private AutoMemoRepository autoMemoRepository;

    private static QAutoMemo q = QAutoMemo.autoMemo;

    @Override
    public Iterable<AutoMemo> list(AutoMemoDto.Command command) {
        BooleanBuilder builder = new BooleanBuilder(q.code.eq(command.getCode()));
        if (command.isOnlyEnabled()) {
            builder.and(q.enabled.isTrue());
        }
        return autoMemoRepository.findAll(builder);
    }

    @Override
    public AutoMemo getJoin() {
        AutoMemo memo = autoMemoRepository.findOne(q.code.eq("가입쪽지"));
        return memo == null ? new AutoMemo() : memo;
    }

    @Transactional
    @Override
    public AjaxResult add(AutoMemo add) {
        autoMemoRepository.saveAndFlush(add);
        Config.getAutoMemoMap().put(add.getId(), add);
        return new AjaxResult(true, "고객 응대를 등록하였습니다.");
    }

    @Transactional
    @Override
    public AjaxResult update(AutoMemoDto.Update update) {
        AutoMemo memo = autoMemoRepository.findOne(update.getId());
        memo.setName(update.getName());
        memo.setTitle(update.getTitle());
        memo.setContents(update.getContents());
        autoMemoRepository.saveAndFlush(memo);

        Config.getAutoMemoMap().put(update.getId(), memo);
        return new AjaxResult(true, "고객 응대를 수정하였습니다.");
    }

    @Transactional
    @Override
    public AjaxResult updateJoin(AutoMemoDto.Join join) {
        AutoMemo memo = autoMemoRepository.findOne(q.code.eq("가입쪽지"));
        if (memo == null) {
            memo = new AutoMemo();
            memo.setCode("가입쪽지");
        }
        memo.setTitle(join.getTitle());
        memo.setContents(join.getContents());
        memo.setEnabled(join.isEnabled());
        autoMemoRepository.saveAndFlush(memo);

        Config.setJoinMemo(memo);
        return new AjaxResult(true, "회원가입 자동쪽지를 수정하였습니다.");
    }

    @Transactional
    @Override
    public AjaxResult enabled(Long id) {
        AutoMemo memo = autoMemoRepository.findOne(id);
        memo.setEnabled(!memo.isEnabled());
        autoMemoRepository.saveAndFlush(memo);

        Config.getAutoMemoMap().put(id, memo);
        AjaxResult result = new AjaxResult(true, "고객 응대를 수정하였습니다.");
        result.setValue(memo.isEnabled() ? "Y" : "N");
        return result;
    }

    @Transactional
    @Override
    public AjaxResult delete(Long id) {
        autoMemoRepository.delete(id);
        Config.getAutoMemoMap().remove(id);
        return new AjaxResult(true, "고객 응대를 삭제하였습니다.");
    }

}
