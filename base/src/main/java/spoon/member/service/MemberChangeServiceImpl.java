package spoon.member.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.DateUtils;
import spoon.common.utils.ErrorUtils;
import spoon.common.utils.StringUtils;
import spoon.mapper.ChangeMapper;
import spoon.member.domain.UserChange;
import spoon.support.convert.CryptoConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class MemberChangeServiceImpl implements MemberChangeService {

    private ChangeMapper changeMapper;

    private BCryptPasswordEncoder passwordEncoder;

    private final CryptoConverter converter = new CryptoConverter();

    @Transactional
    @Override
    public List<String> addMember(String memberText) {
        String[] lines = memberText.split(System.getProperty("line.separator"));
        List<UserChange> list = new ArrayList<>();
        List<String> error = new ArrayList<>();

        // TODO agency
        // 0:userid, 1:nickname, 2:deposit, 3:withdraw, 4:level, 5:password, 6:bankPassword, 7:phone 8:account
        // 9:depositor, 10:bank, 11:money, 12:point, 13:recommender, 14:agency1, 15:agency2, 16:joinDate
        // 17:enabled, 18:loginDate, 19:joinDate, 20:memo
        for (String line : lines) {
            if (StringUtils.empty(line)) continue;
            String[] fields = Arrays.copyOf(line.trim().split("\\t"), 23);

            UserChange user = new UserChange();

            try {
                user.setRole("USER");
                user.setUserid(fields[0]);
                user.setNickname(fields[1]);
                user.setDeposit(Long.parseLong(fields[2].trim().replaceAll("\\.00", "")));
                user.setWithdraw(Long.parseLong(fields[3].trim().replaceAll("\\.00", "")));
                if (StringUtils.empty(fields[4])) {
                    user.setLevel(1);
                } else {
                    user.setLevel(Integer.parseInt(fields[4], 10));
                }
                user.setPassword(passwordEncoder.encode(fields[5]));
                user.setBankPassword(converter.convertToDatabaseColumn(fields[6]));
                user.setPhone(converter.convertToDatabaseColumn(fields[7]));
                user.setAccount(converter.convertToDatabaseColumn(fields[8]));
                user.setDepositor(converter.convertToDatabaseColumn(fields[9]));
                user.setBank(fields[10]);
                user.setMoney(Integer.parseInt(fields[11], 10));
                user.setPoint(Integer.parseInt(fields[12], 10));
                user.setRecommender(fields[13]);
                user.setAgency4(fields[14]);
                user.setAgency3(fields[15]);
                user.setAgency2(fields[16]);
                user.setAgency1(fields[17]);
                user.setJoinDate(DateUtils.parse(fields[18].substring(0, 19), "yyyy-MM-dd HH:mm:ss"));
                if ("정상".equals(fields[19])) {
                    user.setEnabled(true);
                    user.setSecession(false);
                } else {
                    user.setEnabled(false);
                    user.setSecession(true);
                }
                user.setLoginDate(DateUtils.parse(fields[20].substring(0, 19), "yyyy-MM-dd HH:mm:ss"));
                user.setMemo(fields[21]);

                user.setJoinDomain("");
                user.setJoinIp("");
                user.setLoginIp("");
                user.setJoinCode("");
                user.setRateCode("");
                user.setRateShare(0);
                user.setRateSports(0);
                user.setRateZone(0);
                user.setBetSports(0);
                user.setBetSportsCnt(0);
                user.setLoginCnt(0);
                user.setBalance(1);
                list.add(user);

            } catch (RuntimeException e) {
                log.error(line);
                log.error("리스트 변경 에러", e);
                error.add(fields[2] + " : " + e.getMessage());
            }

            if (list.size() == 30) {
                addMember(list);
                list.clear();
            }
        }

        if (list.size() > 0) addMember(list);

        return error;
    }

    private boolean addMember(List<UserChange> list) {
        try {
            changeMapper.addMember(list);
            return true;
        } catch (RuntimeException e) {
            log.error("{}", e.getMessage());
            log.error("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

}
