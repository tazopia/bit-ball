package spoon.board.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.board.domain.CommentDto;
import spoon.board.entity.Comment;
import spoon.board.repository.CommentRepository;
import spoon.common.utils.ErrorUtils;
import spoon.common.utils.StringUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.member.domain.Role;
import spoon.member.domain.User;
import spoon.member.service.MemberService;
import spoon.payment.service.EventPaymentService;
import spoon.support.web.AjaxResult;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private MemberService memberService;

    private BoardService boardService;

    private EventPaymentService eventPaymentService;

    private CommentRepository commentRepository;

    @Transactional
    @Override
    public boolean adminAdd(CommentDto.AdminAdd add) {
        try {
            Comment comment = new Comment();

            if (add.getUserid().equals("_ADMIN_")) {
                comment.setUserid(WebUtils.userid());
                comment.setNickname(Config.getSiteConfig().getNickname());
                comment.setRole(Role.ADMIN);
                comment.setLevel(0);
                comment.setAgency1("");
                comment.setAgency2("");
                comment.setAgency3("");
                comment.setAgency4("");
            } else {
                User user = StringUtils.empty(add.getUserid()) ?
                        memberService.getRandomUser() : memberService.getUser(add.getUserid());
                comment.setUserid(user.getUserid());
                comment.setNickname(user.getNickname());
                comment.setRole(user.getRole());
                comment.setLevel(user.getLevel());
                comment.setAgency1(user.getAgency1());
                comment.setAgency2(user.getAgency2());
                comment.setAgency3(user.getAgency3());
                comment.setAgency4(user.getAgency4());
            }
            comment.setContents(add.getContents());
            comment.setRegDate(add.getRegDate());
            comment.setEnabled(true);
            comment.setBid(add.getBoardId());

            commentRepository.saveAndFlush(comment);
            boardService.setComment(add.getBoardId());

        } catch (RuntimeException e) {
            log.warn("관리자 댓글 등록에 실패하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }

        return true;
    }

    @Transactional
    @Override
    public boolean add(CommentDto.Add add) {
        try {
            Comment comment = new Comment();
            User user = memberService.getUser(WebUtils.userid());

            if (user == null || user.isBlock()) return false;

            comment.setUserid(user.getUserid());
            comment.setNickname(user.getNickname());
            comment.setRole(user.getRole());
            comment.setLevel(user.getLevel());
            comment.setAgency1(user.getAgency1());
            comment.setAgency2(user.getAgency2());
            comment.setAgency3(user.getAgency3());
            comment.setAgency4(user.getAgency4());
            comment.setContents(add.getContents());
            comment.setRegDate(new Date());
            comment.setEnabled(true);
            comment.setBid(add.getBoardId());

            commentRepository.saveAndFlush(comment);
            boardService.setComment(add.getBoardId());

            eventPaymentService.commentPoint();
        } catch (RuntimeException e) {
            log.warn("댓글 등록에 실패하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }

        return true;
    }

    @Transactional
    @Override
    public AjaxResult deleteAdmin(Long id, Long boardId) {
        commentRepository.delete(id);
        boardService.setComment(boardId);
        return new AjaxResult(true, "댓글을 삭제하였습니다.");
    }

    @Transactional
    @Override
    public AjaxResult delete(Long id) {
        Comment comment = commentRepository.findOne(id);
        if (!comment.getUserid().equals(WebUtils.userid())) {
            return new AjaxResult(false, "삭제를 할 수 없습니다.");
        }
        comment.setEnabled(false);
        boardService.setComment(comment.getBid());
        return new AjaxResult(true);
    }

    @Transactional
    @Override
    public AjaxResult hidden(CommentDto.Hidden hidden) {
        Comment comment = commentRepository.findOne(hidden.getId());
        comment.setHidden(hidden.isHidden());
        boardService.setComment(comment.getBid());
        return new AjaxResult(true, hidden.isHidden() ? "댓글을 숨김처리 하였습니다." : "댓글을 숨기해제 하였습니다.");
    }

}
