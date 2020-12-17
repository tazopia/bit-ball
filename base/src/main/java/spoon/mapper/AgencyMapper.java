package spoon.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.security.access.method.P;
import spoon.accounting.domain.AccountingDto;
import spoon.monitor.domain.MonitorDto;
import spoon.sale.domain.SaleDto;
import spoon.sale.entity.SaleItem;
import spoon.seller.domain.Seller;
import spoon.seller.domain.SellerDto;

import java.util.List;

public interface AgencyMapper {

    List<AccountingDto.Daily> daily(@Param("command") AccountingDto.Agency command);

    AccountingDto.Game gameAccount(@Param("command") AccountingDto.Agency command);

    AccountingDto.Game gameInplay(@Param("command") AccountingDto.Agency command);

    List<AccountingDto.Amount> money(@Param("command") AccountingDto.Agency command);

    List<AccountingDto.Amount> point(@Param("command") AccountingDto.Agency command);

    MonitorDto.Amount amount(@Param("command") AccountingDto.Agency command);

    List<Seller> sellerShare(@Param("command") SellerDto.AgencyCommand command);

    List<SaleItem> currentSale(@Param("command") SaleDto.AgencyCommand command);
}
