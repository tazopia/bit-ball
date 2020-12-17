package spoon.customer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.customer.domain.MemoDto;
import spoon.customer.entity.Memo;
import spoon.member.domain.User;
import spoon.support.web.AjaxResult;

public interface MemoService {

    Page<Memo> page(MemoDto.Command command, Pageable pageable);

    Page<Memo> page(String userid, Pageable pageable);

    AjaxResult addOne(MemoDto.One one);

    void addJoin(User user);

    boolean add(MemoDto.Add add);

    AjaxResult view(Long id);

    AjaxResult delete(Long id);

    AjaxResult delete(Long[] id);

    AjaxResult deleteAdmin(Long[] memoIds);
}
