package spoon.mapper;

import org.apache.ibatis.annotations.Param;
import spoon.sale.domain.SaleDto;
import spoon.sale.entity.SaleItem;
import spoon.seller.domain.Seller;
import spoon.seller.domain.SellerDto;

import java.util.List;

public interface ShareMapper {

    List<Seller> sellerShare(@Param("command") SellerDto.Command command);

    void updateRateCode(@Param("update") SellerDto.Update update);

    List<SaleItem> currentSale(@Param("command") SaleDto.Command command);

}
