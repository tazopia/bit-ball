package spoon.board.service;

import spoon.board.domain.BoardDto;
import spoon.support.web.AjaxResult;

public interface BoardAdminService {

    boolean add(BoardDto.AdminAdd add);

    boolean update(BoardDto.AdminUpdate update);

    AjaxResult delete(Long id);

    AjaxResult hidden(BoardDto.Hidden hidden);

    AjaxResult showTop(BoardDto.Show show);
}
