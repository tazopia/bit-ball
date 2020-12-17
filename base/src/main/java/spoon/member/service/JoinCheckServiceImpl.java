package spoon.member.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spoon.common.utils.DateUtils;
import spoon.config.domain.Config;
import spoon.member.domain.Role;
import spoon.member.entity.QMember;
import spoon.member.repository.MemberRepository;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Service
public class JoinCheckServiceImpl implements JoinCheckService {

    private MemberRepository memberRepository;

    private static QMember q = QMember.member;

    @Override
    public AjaxResult notExistUserid(String userid) {
        long count = memberRepository.count(q.userid.eq(userid));
        return new AjaxResult(count == 0);
    }

    @Override
    public AjaxResult notExistNickname(String nickname) {
        long count = memberRepository.count(q.nickname.eq(nickname));
        return new AjaxResult(count == 0);
    }

    @Override
    public AjaxResult notExistPhone(String phone) {
        phone = phone.replaceAll("[^0-9]", "");
        long count = memberRepository.count(q.phone.eq(phone).and(q.role.eq(Role.USER)));
        return new AjaxResult(count == 0);
    }

    @Override
    public AjaxResult notExistAccount(String account) {
        account = account.replaceAll("[^0-9]", "");
        long count = memberRepository.count(q.account.eq(account).and(q.role.eq(Role.USER)));
        return new AjaxResult(count == 0);
    }

    @Override
    public AjaxResult isExistRecommender(String recommender) {
        if (Config.getSiteConfig().getJoin().isCheckRecommend()) { // 추천인 체크
            long count = memberRepository.count(q.userid.eq(recommender).and(q.role.eq(Role.USER)));
            return new AjaxResult(count == 1);
        } else {
            return new AjaxResult(true);
        }
    }

    @Override
    public boolean enabledCheckIp(String ip) {
        return memberRepository.count(q.joinDate.after(DateUtils.beforeMinutes(Config.getSiteConfig().getJoin().getSameIp())).and(q.joinIp.eq(ip))) == 0;
    }
}
