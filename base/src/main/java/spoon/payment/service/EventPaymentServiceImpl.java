package spoon.payment.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spoon.common.utils.DateUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.member.domain.CurrentUser;
import spoon.member.domain.Role;
import spoon.member.domain.User;
import spoon.payment.domain.PointCode;
import spoon.payment.entity.Point;
import spoon.payment.entity.QPoint;
import spoon.payment.repository.PointRepository;

@Slf4j
@AllArgsConstructor
@Service
public class EventPaymentServiceImpl implements EventPaymentService {

    private PaymentService paymentService;

    private PointRepository pointRepository;

    private static QPoint q = QPoint.point;

    @Transactional
    @Override
    public void joinPoint(User user, Long point) {
        Point joinPoint = new Point(user, PointCode.JOIN, 0L, 0, point, "");
        pointRepository.saveAndFlush(joinPoint);
    }

    @Transactional
    @Override
    public void loginPoint() {
        CurrentUser user = WebUtils.user();
        if (user == null) return;
        if (user.getRole() != Role.USER) return;

        // 정책을 가져온다.
        int point = Config.getSiteConfig().getPoint().getLogin();
        if (point <= 0) return;

        // 첫로그인시에만 지급정책
        String today = DateUtils.todayString();
        long count = pointRepository.count(q.userid.eq(user.getUserid())
                .and(q.pointCode.eq(PointCode.LOGIN))
                .and(q.regDate.goe(DateUtils.start(today))).and(q.regDate.lt(DateUtils.end(today))));
        if (count > 0) return;

        paymentService.addPoint(PointCode.LOGIN, 0L, user.getUserid(), point, "매일 첫로그인 포인트");
    }

    @Transactional
    @Override
    public void boardPoint() {
        CurrentUser user = WebUtils.user();
        if (user == null) return;
        if (user.getRole() != Role.USER) return;

        // 정책을 가져온다.
        int point = Config.getSiteConfig().getPoint().getBoard();
        if (point <= 0) return;

        // 하루 최대
        int max = Config.getSiteConfig().getPoint().getBoardMax();
        if (max <= 0) return;

        String today = DateUtils.todayString();
        long cnt = pointRepository.count(q.userid.eq(user.getUserid()).and(q.pointCode.eq(PointCode.BOARD)).and(q.regDate.between(DateUtils.start(today), DateUtils.end(today))));
        if (cnt < max) {
            paymentService.addPoint(PointCode.BOARD, 0L, user.getUserid(), point, String.format("게시판 글작성 %s 번째", cnt));
        }
    }

    @Override
    public void commentPoint() {
        CurrentUser user = WebUtils.user();
        if (user == null) return;
        if (user.getRole() != Role.USER) return;

        // 정책을 가져온다.
        int point = Config.getSiteConfig().getPoint().getComment();
        if (point <= 0) return;

        // 하루 최대
        int max = Config.getSiteConfig().getPoint().getCommentMax();
        if (max <= 0) return;

        String today = DateUtils.todayString();
        long cnt = pointRepository.count(q.userid.eq(user.getUserid()).and(q.pointCode.eq(PointCode.COMMENT)).and(q.regDate.between(DateUtils.start(today), DateUtils.end(today))));
        if (cnt < max) {
            paymentService.addPoint(PointCode.COMMENT, 0L, user.getUserid(), point, String.format("댓글작성 %s 번째", cnt));
        }
    }


}
