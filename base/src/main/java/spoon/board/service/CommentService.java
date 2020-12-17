package spoon.board.service;

import spoon.board.domain.CommentDto;
import spoon.support.web.AjaxResult;

public interface CommentService {

    boolean adminAdd(CommentDto.AdminAdd add);

    boolean add(CommentDto.Add add);

    AjaxResult deleteAdmin(Long id, Long boardId);

    AjaxResult delete(Long id);

    AjaxResult hidden(CommentDto.Hidden hidden);
}
