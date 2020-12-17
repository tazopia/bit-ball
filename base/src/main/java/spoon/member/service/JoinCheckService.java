package spoon.member.service;

import spoon.support.web.AjaxResult;

public interface JoinCheckService {

    AjaxResult notExistUserid(String userid);

    AjaxResult notExistNickname(String nickname);

    AjaxResult notExistPhone(String phone);

    AjaxResult notExistAccount(String account);

    AjaxResult isExistRecommender(String recommender);

    boolean enabledCheckIp(String ip);
}
