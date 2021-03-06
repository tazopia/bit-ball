package spoon.seller.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.utils.ErrorUtils;
import spoon.common.utils.WebUtils;
import spoon.mapper.AgencyMapper;
import spoon.mapper.ShareMapper;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.seller.domain.Seller;
import spoon.seller.domain.SellerDto;
import spoon.support.web.AjaxResult;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ShareServiceImpl implements ShareService {

    private final ShareMapper shareMapper;

    private final AgencyMapper agencyMapper;

    private final MemberService memberService;

    @Override
    public List<Seller> sellerShare(SellerDto.Command command) {
        return shareMapper.sellerShare(command);
    }

    @Override
    public List<Seller> agencySellerShare(SellerDto.AgencyCommand command) {
        return agencyMapper.sellerShare(command);
    }


    @Transactional
    @Override
    public AjaxResult updateShare(SellerDto.Update update) {
        Member agency4 = memberService.getMember(update.getUserid());

        try {
            agency4.setPowerMin(update.getPowerMin());
            agency4.setPowerMax(update.getPowerMax());
            agency4.setPowerLadder(update.getPowerLadder());
            agency4.setKeno(update.getKeno());
            agency4.setKenoLadder(update.getKenoLadder());
            agency4.setBtc3Min(update.getBtc3Min());
            agency4.setBtc3Max(update.getBtc3Max());
            agency4.setBtc5Min(update.getBtc5Min());
            agency4.setBtc5Max(update.getBtc5Max());

            memberService.update(agency4);
        } catch (RuntimeException e) {
            log.error("총판 수수료율 수정에 실패하였습니다.: {}", update.toString(), e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new AjaxResult(false, update.getUserid() + " 총판의 지급율 수정에 실패 하였습니다.");
        }

        return new AjaxResult(true, update.getUserid() + " 총판의 지급율을 수정하였습니다.");
    }

    @Transactional
    @Override
    public AjaxResult updatePartnerShare(SellerDto.Update update) {
        Member agency = memberService.getMember(update.getUserid());
        final Member parent = memberService.getMember(WebUtils.userid());

        if (parent.getPowerMin() < update.getPowerMin() || parent.getPowerMax() < update.getPowerMax())
            return new AjaxResult(false, "파워볼 상위 최대 요율 이상은 지정하실 수 없습니다.");
        if (parent.getPowerLadder() < update.getPowerLadder())
            return new AjaxResult(false, "파다리 상위 최대 요율 이상은 지정하실 수 없습니다.");
        if (parent.getKeno() < update.getKeno())
            return new AjaxResult(false, "스피드키노 상위 최대 요율 이상은 지정하실 수 없습니다.");
        if (parent.getKenoLadder() < update.getKenoLadder())
            return new AjaxResult(false, "키다리 상위 최대 요율 이상은 지정하실 수 없습니다.");

        try {
            agency.setPowerMin(update.getPowerMin());
            agency.setPowerMax(update.getPowerMax());
            agency.setPowerLadder(update.getPowerLadder());
            agency.setKeno(update.getKeno());
            agency.setKenoLadder(update.getKenoLadder());
            agency.setBtc3Min(update.getBtc3Min());
            agency.setBtc3Max(update.getBtc3Max());
            agency.setBtc5Min(update.getBtc5Min());
            agency.setBtc5Max(update.getBtc5Max());

            memberService.update(agency);

        } catch (RuntimeException e) {
            log.error("{} - 대리점 수수료율 수정에 실패하였습니다.: {}", e.getMessage(), update.toString());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new AjaxResult(false, update.getUserid() + " 대리점 지급율 수정에 실패 하였습니다.");
        }

        return new AjaxResult(true, update.getUserid() + " 대리점 지급율을 수정하였습니다.");
    }

    private void updateShareAgency1(SellerDto.Update update) {
        shareMapper.updateRateCode(update);
    }
}
