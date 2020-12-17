package spoon.board.service;

import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.bet.entity.Bet;
import spoon.bet.service.BetListService;
import spoon.board.domain.BoardDto;
import spoon.board.entity.Board;
import spoon.board.entity.Comment;
import spoon.board.entity.QBoard;
import spoon.board.entity.QComment;
import spoon.board.repository.BoardRepository;
import spoon.board.repository.CommentRepository;
import spoon.common.utils.ErrorUtils;
import spoon.common.utils.StringUtils;
import spoon.common.utils.WebUtils;
import spoon.member.domain.User;
import spoon.member.service.MemberService;
import spoon.monitor.service.MonitorService;
import spoon.payment.service.EventPaymentService;

import java.util.Arrays;
import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

    private BetListService betListService;

    private MemberService memberService;

    private BoardRepository boardRepository;

    private CommentRepository commentRepository;

    private MonitorService monitorService;

    private EventPaymentService eventPaymentService;

    private static QBoard q = QBoard.board;

    @Override
    public Page<Board> page(BoardDto.Command command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.code.eq(command.getCode()));

        // 아이디 닉네임
        if (StringUtils.notEmpty(command.getUsername())) {
            if (command.isMatch()) { // 정확하게 일치
                builder.and(q.userid.eq(command.getUsername()).or(q.nickname.eq(command.getUsername())));
            } else {
                builder.and(q.userid.like("%" + command.getUsername() + "%").or(q.nickname.like("%" + command.getUsername() + "%")));
            }
        }

        if (StringUtils.notEmpty(command.getSearchType())) {
            switch (command.getSearchType()) {
                case "title":
                    builder.and(q.title.like("%" + command.getSearchValue() + "%"));
                    break;
                case "contents":
                    builder.and(q.contents.like("%" + command.getSearchValue() + "%"));
                    break;
            }
        }

        return boardRepository.findAll(builder, pageable);
    }

    @Override
    public Page<Board> page(String code, Pageable pageable) {
        return boardRepository.findAll(q.code.eq(code).and(q.enabled.isTrue()).and(q.hidden.isFalse()), pageable);
    }

    @Override
    public Iterable<Board> topList() {
        return boardRepository.findAll(q.showTop.isTrue().and(q.enabled.isTrue()).and(q.hidden.isFalse()), new Sort(Sort.Direction.DESC, "regDate"));
    }

    @Override
    public Board getOne(Long id) {
        return boardRepository.findOne(id);
    }

    @Transactional
    @Override
    public Board findView(BoardDto.Command command) {
        Board board = boardRepository.findOne(q.id.eq(command.getId()).and(q.code.eq(command.getCode())));
        if (board == null) return null;

        board.addHit();

        if (board.isAlarm()) {
            board.setAlarm(false);
            monitorService.checkBoard();
        }

        if (board.isBet()) {
            board.setBets(betListService.listByBoard(board.betIds()));
        }
        if (board.getComment() > 0) {
            board.setComments(commentsByBoard(board.getId(), true));
        }
        return board;
    }

    @Transactional
    @Override
    public Board findView(Long id) {
        Board board = boardRepository.findOne(q.id.eq(id).and(q.enabled.isTrue()).and(q.hidden.isFalse()));
        if (board == null) return null;

        board.addHit();
        if (board.isBet()) {
            board.setBets(betListService.listByBoard(board.betIds()));
        }
        if (board.getComment() > 0) {
            board.setComments(commentsByBoard(board.getId(), false));
        }
        return board;
    }

    @Transactional
    @Override
    public void update(Board board) {
        boardRepository.saveAndFlush(board);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        boardRepository.delete(id);
    }

    @Transactional
    @Override
    public void setComment(Long boardId) {
        QComment qc = QComment.comment;
        Board board = getOne(boardId);
        long cnt = commentRepository.count(qc.bid.eq(boardId).and(qc.enabled.isTrue()).and(qc.hidden.isFalse()));
        board.setComment(cnt);
    }

    @Transactional
    @Override
    public boolean add(BoardDto.Add add) {
        Board board = new Board();
        User user = memberService.getUser(WebUtils.userid());

        if (user == null || user.isBlock()) return false;

        try {
            board.setUserid(user.getUserid());
            board.setNickname(user.getNickname());
            board.setRole(user.getRole());
            board.setLevel(user.getLevel());
            board.setAgency1(user.getAgency1());
            board.setAgency2(user.getAgency2());
            board.setAgency3(user.getAgency3());
            board.setAgency4(user.getAgency4());
            board.setAlarm(true);
            board.setCode(add.getCode());
            if (StringUtils.notEmpty(add.getBetId())) {
                board.setBetId(Arrays.stream(add.getBetId().split(",")).mapToLong(Long::valueOf).toArray());
            }
            board.setBet(StringUtils.notEmpty(add.getBetId()));
            board.setTitle(add.getTitle());
            board.setContents(add.getContents());
            board.setRegDate(new Date());
            board.setEnabled(true);

            update(board);

            eventPaymentService.boardPoint();
        } catch (RuntimeException e) {
            log.warn("게시물 등록에 실패하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }

        monitorService.checkBoard();
        return true;
    }

    @Transactional
    @Override
    public Iterable<Bet> getBet(String betId) {
        return betListService.listByBoard(betId, WebUtils.userid());
    }

    @Override
    public String boardTitle(String code) {
        switch (code) {
            case "notice":
                return "공지사항";
            case "event":
                return "이벤트";
            default:
                return "자유게시판";
        }
    }

    private Iterable<Comment> commentsByBoard(Long id, boolean hidden) {
        QComment qc = QComment.comment;
        BooleanBuilder builder = new BooleanBuilder(qc.bid.eq(id).and(qc.enabled.isTrue()));
        if (!hidden) {
            builder.and(qc.hidden.isFalse());
        }
        return commentRepository.findAll(builder);
    }
}
