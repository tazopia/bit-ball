package spoon.customer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.customer.domain.QnaDto;
import spoon.customer.entity.Qna;
import spoon.support.web.AjaxResult;

public interface QnaService {

    Page<Qna> page(String userid, Pageable pageable);

    Page<Qna> page(QnaDto.Command command, Pageable pageable);

    Qna getOne(Long id);

    boolean add(QnaDto.Add add);

    boolean reply(QnaDto.Reply reply);

    boolean delete(Long id);

    AjaxResult view(Long id);

    AjaxResult hidden(Long id);

    AjaxResult account();

}
