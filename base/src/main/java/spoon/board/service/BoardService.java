package spoon.board.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.bet.entity.Bet;
import spoon.board.domain.BoardDto;
import spoon.board.entity.Board;

public interface BoardService {

    Page<Board> page(BoardDto.Command command, Pageable pageable);

    Page<Board> page(String code, Pageable pageable);

    Iterable<Board> topList();

    Board getOne(Long id);

    Board findView(BoardDto.Command command);

    Board findView(Long id);

    void update(Board board);

    void delete(Long id);

    String boardTitle(String code);

    void setComment(Long boardId);

    boolean add(BoardDto.Add add);

    Iterable<Bet> getBet(String betId);
}
