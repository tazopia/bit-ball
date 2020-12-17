package spoon.seller.service;

import spoon.seller.domain.Seller;
import spoon.seller.domain.SellerDto;
import spoon.support.web.AjaxResult;

import java.util.List;

public interface ShareService {

    List<Seller> sellerShare(SellerDto.Command command);

    List<Seller> agencySellerShare(SellerDto.AgencyCommand command);

    AjaxResult updateShare(SellerDto.Update update);

    AjaxResult updatePartnerShare(SellerDto.Update update);
}
