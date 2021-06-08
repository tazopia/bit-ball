package spoon.member.service;

import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import spoon.common.utils.DateUtils;
import spoon.common.utils.StringUtils;
import spoon.common.utils.WebUtils;
import spoon.mapper.MemberMapper;
import spoon.member.domain.MemberDto;
import spoon.member.domain.Role;
import spoon.member.domain.User;
import spoon.member.entity.Member;
import spoon.member.entity.QMember;
import spoon.member.repository.MemberRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class MemberListServiceImpl implements MemberListService {

    private MemberRepository memberRepository;

    private MemberMapper memberMapper;

    private static QMember q = QMember.member;

    @Override
    public Page<Member> list(MemberDto.Command command, Pageable pageable) {
        PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sortBy(command.getSort()));
        return memberRepository.findAll(getBuilder(command), pageRequest);
    }

    @Override
    public Page<Member> sellerList(MemberDto.AgencyCommand command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(q.role.eq(Role.USER));
        switch (command.getRole()) {
            case "AGENCY4":
                builder.and(q.agency4.eq(command.getAgency()));
                break;
            case "AGENCY3":
                builder.and(q.agency3.eq(command.getAgency()));
                break;
            case "AGENCY2":
                builder.and(q.agency2.eq(command.getAgency()));
                break;
            case "AGENCY1":
                builder.and(q.agency1.eq(command.getAgency()));
                break;
        }

        if (StringUtils.notEmpty(command.getUsername())) {
            if (command.isMatch()) {
                builder.and(q.userid.eq(command.getUsername()).or(q.nickname.eq(command.getUsername())));
            } else {
                builder.and(q.userid.like("%" + command.getUsername() + "%").or(q.nickname.like("%" + command.getUsername() + "%")));
            }
        }
        return memberRepository.findAll(builder, pageable);
    }

    @Override
    public List<User> getDummyList() {
        return memberMapper.getDummyList();
    }

    @Override
    public Iterable<Member> getExcel() {
        return memberRepository.findAll(q.role.eq(Role.USER));
    }

    private BooleanBuilder getBuilder(MemberDto.Command command) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.notEmpty(command.getUsername())) {
            if (command.isMatch()) {
                builder.and(q.userid.eq(command.getUsername()).or(q.nickname.eq(command.getUsername())));
            } else {
                builder.and(q.userid.like("%" + command.getUsername() + "%").or(q.nickname.like("%" + command.getUsername() + "%")));
            }
        }

        if (StringUtils.empty(command.getSearchValue())) {
            builderByMode(builder, command.getMode());
        } else {
            builderBySearch(builder, command);
        }

        if (StringUtils.notEmpty(command.getLevel())) {
            builder.and(q.level.eq(Integer.parseInt(command.getLevel())));
        }

        Role role = WebUtils.role();
        switch (role) {
            case SUPER:
                builder.and(q.role.ne(Role.GOD));
                break;
            case ADMIN:
                builder.and(q.role.notIn(Role.GOD, Role.SUPER));
                break;
        }

        return builder;
    }

    private void builderBySearch(BooleanBuilder builder, MemberDto.Command command) {
        if (StringUtils.empty(command.getSearchValue())) return;
        switch (command.getSearchType()) {
            case "agency":
                builder.and(
                        q.agency1.contains(command.getSearchValue())
                                .or(q.agency2.contains(command.getSearchValue()))
                                .or(q.agency3.contains(command.getSearchValue()))
                                .or(q.agency4.contains(command.getSearchValue()))
                );
                break;
            case "recommender":
                builder.and(q.recommender.like(command.getSearchValue() + "%"));
                break;
            case "phone":
                builder.and(q.phone.eq(command.getSearchValue()));
                break;
            case "depositor":
                builder.and(q.depositor.eq(command.getSearchValue()));
                break;
            case "code":
                builder.and(q.joinCode.eq(command.getSearchValue()));
                break;
            case "ip":
                builder.and(q.loginIp.like(command.getSearchValue() + "%").or(q.joinIp.like(command.getSearchValue() + "%")));
                break;
        }
    }

    private void builderByMode(BooleanBuilder builder, String mode) {
        switch (mode) {
            case "":
                builder.and(q.role.eq(Role.USER)).and(q.secession.eq(false)).and(q.enabled.eq(true));
                break;
            case "dummy":
                builder.and(q.role.eq(Role.DUMMY));
                break;
            case "agency":
                builder.and(q.role.in(Role.AGENCY1, Role.AGENCY2, Role.AGENCY3, Role.AGENCY4))
                        .and(q.secession.eq(false)).and(q.enabled.eq(true));
                break;
            case "admin":
                Role role = WebUtils.role();
                if (role == Role.GOD) {
                    builder.and(q.role.in(Role.ADMIN, Role.SUPER, Role.SUPER));
                } else if (role == Role.SUPER) {
                    builder.and(q.role.in(Role.ADMIN, Role.SUPER));
                } else {
                    builder.and(q.role.eq(Role.ADMIN));
                }
                break;
            case "disabled":
                builder.and(q.role.eq(Role.USER)).and(q.secession.eq(false)).and(q.enabled.eq(false));
                break;
            case "secession":
                builder.and(q.role.in(Role.USER, Role.AGENCY1, Role.AGENCY2, Role.AGENCY3, Role.AGENCY4)).and(q.secession.eq(true));
                break;
            case "profile":
                builder.and(q.memo.ne(""));
                break;
            case "block":
                builder.and(q.role.eq(Role.USER)).and(q.block.eq(true));
            case "7.login":
                builder.and(q.role.eq(Role.USER)).and(q.loginDate.before(DateUtils.beforeDays(7)));
                break;
            case "15.login":
                builder.and(q.role.eq(Role.USER)).and(q.loginDate.before(DateUtils.beforeDays(15)));
                break;
            case "30.login":
                builder.and(q.role.eq(Role.USER)).and(q.loginDate.before(DateUtils.beforeDays(30)));
                break;
            case "7.betting":
                builder.and(q.role.eq(Role.USER)).and(q.betDate.before(DateUtils.beforeDays(7)));
                break;
            case "15.betting":
                builder.and(q.role.eq(Role.USER)).and(q.betDate.before(DateUtils.beforeDays(15)));
                break;
            case "30.betting":
                builder.and(q.role.eq(Role.USER)).and(q.betDate.before(DateUtils.beforeDays(30)));
                break;
        }
    }

    private Sort sortBy(String sort) {
        switch (sort) {
            case "login":
                return new Sort(Sort.Direction.DESC, "loginDate");
            case "deposit":
                return new Sort(Sort.Direction.DESC, "deposit");
            case "withdraw":
                return new Sort(Sort.Direction.DESC, "withdraw");
            case "change":
                return new Sort(Sort.Direction.DESC, "change");
            case "betting":
                return new Sort(Sort.Direction.DESC, "betTotal");
            case "sports":
                return new Sort(Sort.Direction.DESC, "betSports");
            case "zone":
                return new Sort(Sort.Direction.DESC, "betZone");
            case "money":
                return new Sort(Sort.Direction.DESC, "money");
            case "money.asc":
                return new Sort("money");
            case "point":
                return new Sort(Sort.Direction.DESC, "point");
            case "point.asc":
                return new Sort("point");
            default:
                return new Sort(Sort.Direction.DESC, "joinDate");
        }
    }
}
