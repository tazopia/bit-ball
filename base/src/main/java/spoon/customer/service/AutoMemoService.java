package spoon.customer.service;

import spoon.customer.domain.AutoMemoDto;
import spoon.customer.entity.AutoMemo;
import spoon.support.web.AjaxResult;

public interface AutoMemoService {

    Iterable<AutoMemo> list(AutoMemoDto.Command command);

    AutoMemo getJoin();

    AjaxResult add(AutoMemo add);

    AjaxResult update(AutoMemoDto.Update update);

    AjaxResult updateJoin(AutoMemoDto.Join join);

    AjaxResult enabled(Long id);

    AjaxResult delete(Long id);

}
