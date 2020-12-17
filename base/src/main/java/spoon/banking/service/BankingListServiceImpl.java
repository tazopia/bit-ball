package spoon.banking.service;

import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import spoon.banking.domain.BankingCode;
import spoon.banking.domain.BankingDto;
import spoon.banking.entity.Banking;
import spoon.banking.entity.QBanking;
import spoon.banking.repository.BankingRepository;
import spoon.common.utils.DateUtils;
import spoon.common.utils.StringUtils;
import spoon.mapper.BankingMapper;

@Slf4j
@AllArgsConstructor
@Service
public class BankingListServiceImpl implements BankingListService {

    private BankingRepository bankingRepository;

    private BankingMapper bankingMapper;

    private static QBanking q = QBanking.banking;

    @Override
    public Iterable<Banking> list(String userid, BankingCode bankingCode) {
        if (userid == null) return null;
        return bankingRepository.findAll(q.userid.eq(userid).and(q.bankingCode.eq(bankingCode)).and(q.hidden.isFalse()), new Sort(Sort.Direction.DESC, "regDate"));
    }

    @Override
    public Page<Banking> page(BankingDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.bankingCode.eq(command.getBankingCode()));

        if (command.isClosing()) {
            builder.and(q.closing.isTrue());
        } else {
            builder.and(q.closing.isFalse());
        }

        if (StringUtils.notEmpty(command.getDate())) {
            builder.and(q.regDate.goe(DateUtils.start(command.getDate())).and(q.regDate.lt(DateUtils.end(command.getDate()))));
        }

        if (StringUtils.notEmpty(command.getUsername())) {
            if (command.isMatch()) {
                builder.and(q.userid.eq(command.getUsername()).or(q.nickname.eq(command.getUsername())));
            } else {
                builder.and(q.userid.like("%" + command.getUsername() + "%").or(q.nickname.like("%" + command.getUsername() + "%")));
            }
        }

        if (StringUtils.notEmpty(command.getDepositor())) {
            builder.and(q.depositor.eq(command.getDepositor()));
        }

        return bankingRepository.findAll(builder, pageable);
    }

    @Override
    public BankingDto.Money bankingTotal(BankingDto.Date command) {
        return bankingMapper.bankingTotal(command);
    }

    @Override
    public Page<Banking> bankingPage(String userid, Pageable pageable) {
        return bankingRepository.findAll(q.userid.eq(userid), pageable);
    }

    @Override
    public Page<Banking> bankingPage(BankingDto.Seller command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.closing.isTrue().and(q.cancel.isFalse()));

        if (StringUtils.notEmpty(command.getBankingCode())) {
            builder.and(q.bankingCode.eq(BankingCode.valueOf(command.getBankingCode())));
        }

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
                builder.and(q.userid.like("%" + command.getUsername() + "%").or(q.nickname.like("%" + command.getUsername() + "%")));
            }
        }

        return bankingRepository.findAll(builder, pageable);
    }
}
