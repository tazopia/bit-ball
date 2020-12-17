package spoon.seller.service;

import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spoon.common.utils.StringUtils;
import spoon.seller.domain.JoinCodeDto;
import spoon.seller.entity.JoinCode;
import spoon.seller.entity.QJoinCode;
import spoon.seller.repository.JoinCodeRepository;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Service
public class JoinCodeServiceImpl implements JoinCodeService {

    private JoinCodeRepository joinCodeRepository;

    private static QJoinCode q = QJoinCode.joinCode;

    @Override
    public Iterable<JoinCode> getList(JoinCodeDto.Command command) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.notEmpty(command.getJoinCode())) {
            builder.and(q.code.like("%" + command.getJoinCode() + "%"));
        }

        if (!command.getAgency().equals("-")) {
            builder.and(q.agency4.eq(command.getAgency()));
        }

        return joinCodeRepository.findAll(builder, new Sort(Sort.Direction.ASC, "agency4", "agency3", "agency2", "agency1"));
    }

    @Override
    public Iterable<JoinCode> getPartner(JoinCodeDto.Partner command) {
        BooleanBuilder builder = new BooleanBuilder();

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


        return joinCodeRepository.findAll(builder, new Sort(Sort.Direction.ASC, "agency4", "agency3", "agency2", "agency1"));
    }

    @Transactional
    @Override
    public AjaxResult add(JoinCodeDto.Add add) {
        // 코드 중복 체크
        String code = add.getCode().trim().toUpperCase();
        long count = joinCodeRepository.count(q.code.eq(code));

        if (count > 0) {
            return new AjaxResult(false, "가입코드가 중복 되었습니다.");
        }

        JoinCode joinCode = new JoinCode();
        joinCode.setCode(code);
        joinCode.setAgency1(add.getAgency1());
        joinCode.setAgency2(add.getAgency2());
        joinCode.setAgency3(add.getAgency3());
        joinCode.setAgency4(add.getAgency4());
        joinCode.setEnabled(add.isEnabled());

        joinCodeRepository.saveAndFlush(joinCode);

        return new AjaxResult(true, "가입코드를 등록하였습니다.");
    }

    @Transactional
    @Override
    public AjaxResult update(JoinCodeDto.Update update) {
        JoinCode joinCode = joinCodeRepository.findOne(update.getId());
        joinCode.setEnabled(update.isEnabled());
        joinCode.setMemo(update.getMemo());
        joinCodeRepository.saveAndFlush(joinCode);

        return new AjaxResult(true, joinCode.getCode() + " 수정을 완료하였습니다.");
    }

    @Transactional
    @Override
    public AjaxResult delete(Long id) {
        joinCodeRepository.delete(id);

        return new AjaxResult(true, "가입코드를 삭제하였습니다.");
    }

    @Override
    public boolean existJoinCode(String code) {
        return joinCodeRepository.count(q.code.eq(code.trim().toUpperCase()).and(q.enabled.isTrue())) > 0;
    }

    @Override
    public JoinCode get(String code) {
        return joinCodeRepository.findOne(q.code.eq(code).and(q.enabled.isTrue()));
    }

}
