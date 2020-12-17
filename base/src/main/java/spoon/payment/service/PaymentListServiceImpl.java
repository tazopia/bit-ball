package spoon.payment.service;

import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import spoon.common.utils.StringUtils;
import spoon.payment.domain.MoneyCode;
import spoon.payment.domain.MoneyDto;
import spoon.payment.domain.PaymentDto;
import spoon.payment.domain.PointCode;
import spoon.payment.entity.Money;
import spoon.payment.entity.Point;
import spoon.payment.entity.QMoney;
import spoon.payment.entity.QPoint;
import spoon.payment.repository.MoneyRepository;
import spoon.payment.repository.PointRepository;

@Slf4j
@AllArgsConstructor
@Service
public class PaymentListServiceImpl implements PaymentListService {

    private MoneyRepository moneyRepository;

    private PointRepository pointRepository;

    @Override
    public Page<Money> moneyPage(PaymentDto.Command command, Pageable pageable) {
        QMoney q = QMoney.money;

        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.notEmpty(command.getUserid())) {
            builder.and(q.userid.eq(command.getUserid()));
        }

        if (StringUtils.notEmpty(command.getUsername())) {
            if (command.isMatch()) { // 정확하게 일치
                builder.and((q.userid.eq(command.getUsername())).or(q.nickname.eq(command.getUsername())));
            } else {
                builder.and((q.userid.like("%" + command.getUsername() + "%")).or(q.nickname.like("%" + command.getUsername() + "%")));
            }
        }

        if (StringUtils.notEmpty(command.getCode())) {
            switch (command.getCode()) {
                case "충환전":
                    builder.and(q.moneyCode.in(MoneyCode.getBankingCode()));
                    break;
                case "베팅":
                    builder.and(q.moneyCode.in(MoneyCode.getBettingCode()));
                    break;
                case "롤링":
                    builder.and(q.moneyCode.in(MoneyCode.getShareCode()));
                    break;
                case "관리자":
                    builder.and(q.moneyCode.in(MoneyCode.getAddRemoveCode()));
                    break;
                case "포인트전환":
                    builder.and(q.moneyCode.in(MoneyCode.getEtcCode()));
                    break;
            }
        }

        Sort sort;
        if (StringUtils.empty(command.getCode())) {
            sort = new Sort(Sort.Direction.DESC, "id");
        } else {
            sort = new Sort(Sort.Direction.DESC, "actionId").and(new Sort(Sort.Direction.DESC, "id"));
        }

        PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return moneyRepository.findAll(builder, pageRequest);
    }

    @Override
    public Page<Money> sellerMoneyPage(MoneyDto.SellerCommand command, Pageable pageable) {
        QMoney q = QMoney.money;

        BooleanBuilder builder = new BooleanBuilder(q.regDate.goe(command.getStart()).and(q.regDate.loe(command.getEnd())));
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


        if (StringUtils.notEmpty(command.getUserid())) {
            builder.and(q.userid.contains(command.getUserid()));
        }

        if (StringUtils.notEmpty(command.getCode())) {
            switch (command.getCode()) {
                case "충환전":
                    builder.and(q.moneyCode.in(MoneyCode.getBankingCode()));
                    break;
                case "베팅":
                    builder.and(q.moneyCode.in(MoneyCode.getBettingCode()));
                    break;
                case "롤링":
                    builder.and(q.moneyCode.in(MoneyCode.getShareCode()));
                    break;
                case "관리자":
                    builder.and(q.moneyCode.in(MoneyCode.getAddRemoveCode()));
                    break;
                case "포인트전환":
                    builder.and(q.moneyCode.in(MoneyCode.getEtcCode()));
                    break;
            }
        }

        return moneyRepository.findAll(builder, pageable);
    }

    @Override
    public Page<Point> pointPage(PaymentDto.Command command, Pageable pageable) {
        QPoint q = QPoint.point;

        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.notEmpty(command.getUserid())) {
            builder.and(q.userid.eq(command.getUserid()));
        }

        if (StringUtils.notEmpty(command.getUsername())) {
            if (command.isMatch()) { // 정확하게 일치
                builder.and((q.userid.eq(command.getUsername())).or(q.nickname.eq(command.getUsername())));
            } else {
                builder.and((q.userid.like("%" + command.getUsername() + "%")).or(q.nickname.like("%" + command.getUsername() + "%")));
            }
        }

        if (StringUtils.notEmpty(command.getCode())) {
            switch (command.getCode()) {
                case "충환전":
                    builder.and(q.pointCode.in(PointCode.getBankingCode()));
                    break;
                case "베팅":
                    builder.and(q.pointCode.in(PointCode.getBettingCode()));
                    break;
                case "포인트전환":
                    builder.and(q.pointCode.in(PointCode.getExchangeCode()));
                    break;
                case "관리자":
                    builder.and(q.pointCode.in(PointCode.getAddRemoveCode()));
                    break;
                case "총판":
                    builder.and(q.pointCode.in(PointCode.getSellerCode()));
                    break;
                case "기타":
                    builder.and(q.pointCode.in(PointCode.getEtcCode()));
                    break;
            }
        }

        Sort sort;
        if (StringUtils.empty(command.getCode()) || "총판".equals(command.getCode())) {
            sort = new Sort(Sort.Direction.DESC, "id");
        } else {

            sort = new Sort(Sort.Direction.DESC, "actionId").and(new Sort(Sort.Direction.DESC, "id"));
        }

        PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return pointRepository.findAll(builder, pageRequest);
    }

    @Override
    public Page<Point> pointPage(String userid, Pageable pageable) {
        QPoint q = QPoint.point;

        BooleanBuilder builder = new BooleanBuilder(q.userid.eq(userid));
        return pointRepository.findAll(builder, pageable);
    }
}
