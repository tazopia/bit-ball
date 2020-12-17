package spoon.board.service;

import spoon.board.domain.NoticeDto;
import spoon.board.entity.Notice;
import spoon.support.web.AjaxResult;

public interface NoticeService {

    Iterable<Notice> list();

    Iterable<Notice> siteList();

    boolean add(NoticeDto.Add add);

    Notice get(Long id);

    boolean update(NoticeDto.Update update);

    AjaxResult delete(Long id);

}
