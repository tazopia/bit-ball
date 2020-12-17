package spoon.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.sale.domain.SaleDto;
import spoon.sale.entity.Sale;
import spoon.sale.entity.SaleItem;
import spoon.support.web.AjaxResult;

import java.util.List;

public interface SaleService {

    List<SaleItem> currentSale(SaleDto.Command command);

    List<SaleItem> currentAgencySale(SaleDto.AgencyCommand command);

    AjaxResult balanceSale(String userid);

    Page<Sale> getPage(SaleDto.Command command, Pageable pageable);

    Page<SaleItem> getPageItem(SaleDto.Command command, Pageable pageable);

    AjaxResult payment(SaleDto.Payment payment);

}
