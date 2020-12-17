package spoon.seller.service;

import spoon.seller.domain.JoinCodeDto;
import spoon.seller.entity.JoinCode;
import spoon.support.web.AjaxResult;

public interface JoinCodeService {

    Iterable<JoinCode> getList(JoinCodeDto.Command command);

    Iterable<JoinCode> getPartner(JoinCodeDto.Partner command);

    AjaxResult add(JoinCodeDto.Add add);

    AjaxResult update(JoinCodeDto.Update update);

    AjaxResult delete(Long id);

    boolean existJoinCode(String code);

    JoinCode get(String code);

}
