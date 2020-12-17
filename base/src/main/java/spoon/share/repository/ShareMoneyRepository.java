package spoon.share.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import spoon.common.utils.StringUtils;
import spoon.member.domain.Role;
import spoon.payment.domain.MoneyCode;
import spoon.payment.entity.Money;
import spoon.payment.entity.QMoney;
import spoon.payment.repository.MoneyRepository;
import spoon.sale.domain.SaleDto;

@RequiredArgsConstructor
@Repository
public class ShareMoneyRepository {

    private final JPAQueryFactory queryFactory;

    private final MoneyRepository moneyRepository;

    private static final QMoney q = QMoney.money;

    public Page<Money> page(SaleDto.SaleCommand command, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder(q.moneyCode.in(MoneyCode.getShareCode()));

        // 총판페이지
        if (StringUtils.notEmpty(command.getRole())) {
            switch (command.getRole()) {
                case "AGENCY4":
                    builder.and(q.role.in(Role.AGENCY4, Role.AGENCY3, Role.AGENCY2, Role.AGENCY1)).and(q.agency4.eq(command.getAgency()));
                    break;
                case "AGENCY3":
                    builder.and(q.role.in(Role.AGENCY3, Role.AGENCY2, Role.AGENCY1)).and(q.agency3.eq(command.getAgency()));
                    break;
                case "AGENCY2":
                    builder.and(q.role.in(Role.AGENCY2, Role.AGENCY1)).and(q.agency2.eq(command.getAgency()));
                    break;
                case "AGENCY1":
                    builder.and(q.role.eq(Role.AGENCY1)).and(q.agency1.eq(command.getAgency()));
                    break;
            }
        }

        // 관리자 페이지
        if (StringUtils.notEmpty(command.getAgency4()))
            builder.and(q.agency4.eq(command.getAgency4()));
        if (StringUtils.notEmpty(command.getAgency3()))
            builder.and(q.agency3.eq(command.getAgency3()));
        if (StringUtils.notEmpty(command.getAgency2()))
            builder.and(q.agency2.eq(command.getAgency2()));
        if (StringUtils.notEmpty(command.getAgency1()))
            builder.and(q.agency1.eq(command.getAgency1()));

        return moneyRepository.findAll(builder, pageable);
    }
}
