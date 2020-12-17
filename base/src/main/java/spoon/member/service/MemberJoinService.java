package spoon.member.service;

import spoon.member.domain.MemberDto;
import spoon.support.web.AjaxResult;

public interface MemberJoinService {

    AjaxResult findJoinCode(String joinCode);

    AjaxResult join(MemberDto.Join join);
}
