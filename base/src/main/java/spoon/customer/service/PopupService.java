package spoon.customer.service;

import org.springframework.web.multipart.MultipartFile;
import spoon.customer.domain.PopupDto;
import spoon.customer.entity.Popup;
import spoon.support.web.AjaxResult;

public interface PopupService {

    Popup findOne(Long id);

    Iterable<Popup> getList();

    Iterable<Popup> getEnabled();

    void add(PopupDto.Add add, MultipartFile image);

    void update(PopupDto.Update update, MultipartFile image);

    AjaxResult delete(Long id);

}
