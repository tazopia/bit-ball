package spoon.mapper;

import spoon.board.domain.BoardDto;

import java.util.List;

public interface BoardMapper {

    List<BoardDto.Main> mainNotice();

    List<BoardDto.Main> mainEvent();

}
