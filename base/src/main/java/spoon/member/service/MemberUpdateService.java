package spoon.member.service;


import spoon.member.domain.MemberDto;
import spoon.support.web.AjaxResult;

public interface MemberUpdateService {

    AjaxResult enabled(String userid);

    AjaxResult black(String userid);

    AjaxResult adminUpdate(MemberDto.Update update);
}
