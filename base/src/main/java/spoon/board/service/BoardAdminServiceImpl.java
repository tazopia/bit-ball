package spoon.board.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.board.domain.BoardDto;
import spoon.board.entity.Board;
import spoon.common.utils.ErrorUtils;
import spoon.common.utils.StringUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.member.domain.Role;
import spoon.member.domain.User;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

import java.util.Arrays;

@Slf4j
@AllArgsConstructor
@Service
public class BoardAdminServiceImpl implements BoardAdminService {

    private BoardService boardService;

    private MemberService memberService;

    @Transactional
    @Override
    public boolean add(BoardDto.AdminAdd add) {
        try {
            Board board = new Board();

            if (add.getUserid().equals("_ADMIN_")) {
                board.setUserid(WebUtils.userid());
                board.setNickname(Config.getSiteConfig().getNickname());
                board.setRole(Role.ADMIN);
                board.setLevel(0);
                board.setAgency1("");
                board.setAgency2("");
                board.setAgency3("");
                board.setAgency4("");
                board.setShowTop(add.isShowTop());
            } else {
                User user = StringUtils.empty(add.getUserid()) ?
                        memberService.getRandomUser() : memberService.getUser(add.getUserid());
                board.setUserid(user.getUserid());
                board.setNickname(user.getNickname());
                board.setRole(user.getRole());
                board.setLevel(user.getLevel());
                board.setAgency1(user.getAgency1());
                board.setAgency2(user.getAgency2());
                board.setAgency3(user.getAgency3());
                board.setAgency4(user.getAgency4());
            }

            board.setAlarm(false);
            board.setCode(add.getCode());
            if (StringUtils.notEmpty(add.getBetId())) {
                board.setBetId(Arrays.stream(add.getBetId().split(",")).mapToLong(Long::valueOf).toArray());
            }
            board.setBet(StringUtils.notEmpty(add.getBetId()));
            board.setTitle(add.getTitle());
            board.setContents(add.getContents());
            board.setHit(add.getHit());
            board.setRegDate(add.getRegDate());
            board.setEnabled(true);

            boardService.update(board);
        } catch (RuntimeException e) {
            log.warn("관리자 게시물 등록에 실패하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }

        return true;
    }

    @Transactional
    @Override
    public boolean update(BoardDto.AdminUpdate update) {
        try {
            Board board = boardService.getOne(update.getId());

            if (update.getUserid().equals("_ADMIN_")) {
                board.setUserid(WebUtils.userid());
                board.setNickname(Config.getSiteConfig().getNickname());
                board.setRole(Role.ADMIN);
                board.setLevel(0);
                board.setAgency1("");
                board.setAgency2("");
                board.setAgency3("");
                board.setAgency4("");
            } else {
                User user = StringUtils.empty(update.getUserid()) ?
                        memberService.getRandomUser() : memberService.getUser(update.getUserid());
                board.setUserid(user.getUserid());
                board.setNickname(user.getNickname());
                board.setRole(user.getRole());
                board.setLevel(user.getLevel());
                board.setAgency1(user.getAgency1());
                board.setAgency2(user.getAgency2());
                board.setAgency3(user.getAgency3());
                board.setAgency4(user.getAgency4());
            }

            board.setShowTop(update.isShowTop());
            board.setCode(update.getCode());
            if (StringUtils.notEmpty(update.getBetId())) {
                board.setBetId(Arrays.stream(update.getBetId().split(",")).mapToLong(Long::valueOf).toArray());
            }
            board.setBet(StringUtils.notEmpty(update.getBetId()));
            board.setTitle(update.getTitle());
            board.setContents(update.getContents());
            board.setHit(update.getHit());
            board.setRegDate(update.getRegDate());


            boardService.update(board);
        } catch (RuntimeException e) {
            log.warn("관리자 게시물 수정에 실패하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }

        return true;
    }

    @Transactional
    @Override
    public AjaxResult delete(Long id) {
        boardService.delete(id);
        return new AjaxResult(true, "게시물을 삭제하였습니다.");
    }

    @Transactional
    @Override
    public AjaxResult hidden(BoardDto.Hidden hidden) {
        Board board = boardService.getOne(hidden.getId());
        board.setHidden(hidden.isHidden());
        return new AjaxResult(true, hidden.isHidden() ? "게시물을 숨김처리 하였습니다." : "게시물을 숨기해제 하였습니다.");
    }

    @Transactional
    @Override
    public AjaxResult showTop(BoardDto.Show show) {
        Board board = boardService.getOne(show.getId());
        board.setShowTop(show.isShowTop());
        return new AjaxResult(true, show.isShowTop() ? "게시물을 자유게시판 상단에 노출 하였습니다." : "게시물을 자유게시판에 숨김 하였습니다.");
    }
}
