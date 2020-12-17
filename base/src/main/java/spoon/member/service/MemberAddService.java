package spoon.member.service;

import spoon.member.domain.MemberDto;
import spoon.support.web.AjaxResult;

public interface MemberAddService {

    AjaxResult adminAdd(MemberDto.Add add);

    AjaxResult sellerAdd(MemberDto.Agency add);

    AjaxResult adminAddDummy(MemberDto.Dummy dummy);

}
