package spoon.customer.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import spoon.common.utils.DateUtils;
import spoon.customer.domain.PopupDto;
import spoon.customer.entity.Popup;
import spoon.customer.entity.QPopup;
import spoon.customer.repository.PopupRepository;
import spoon.support.web.AjaxResult;
import spoon.support.web.FileUploadHandler;
import spoon.support.web.UploadType;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class PopupServiceImpl implements PopupService {

    private FileUploadHandler fileUploadHandler;

    private PopupRepository popupRepository;

    private static QPopup q = QPopup.popup;

    @Override
    public Popup findOne(Long id) {
        return popupRepository.findOne(id);
    }

    @Override
    public Iterable<Popup> getList() {
        return popupRepository.findAll(new Sort(Sort.Direction.DESC, "enabled").and(new Sort("sort")));
    }

    @Override
    public Iterable<Popup> getEnabled() {
        return popupRepository.findAll(q.enabled.isTrue(), new Sort("sort"));
    }

    @Transactional
    @Override
    public void add(PopupDto.Add add, MultipartFile image) {
        String saveImageName = fileUploadHandler.saveFile(image, "popup-" + getMaxId(), UploadType.NOTICE);

        Popup popup = new Popup();
        popup.setImage(saveImageName);
        popup.setEnabled(add.isEnabled());
        popup.setSummary(add.getSummary());
        popup.setSdate(DateUtils.format(new Date(), "yyyyMMddHHmmss"));
        popup.setSort(add.getSort());

        popupRepository.saveAndFlush(popup);
    }

    @Transactional
    @Override
    public void update(PopupDto.Update update, MultipartFile image) {
        Popup popup = findOne(update.getId());
        if (image != null && image.getSize() > 0) {
            String saveImageName = fileUploadHandler.saveFile(image, "popup-" + popup.getId(), UploadType.NOTICE);
            popup.setImage(saveImageName);
        }
        popup.setSummary(update.getSummary());
        popup.setEnabled(update.isEnabled());
        popup.setSdate(DateUtils.format(new Date(), "yyyyMMddHHmmss"));
        popup.setSort(update.getSort());

        popupRepository.saveAndFlush(popup);
    }

    @Transactional
    @Override
    public AjaxResult delete(Long id) {
        popupRepository.delete(id);
        return new AjaxResult(true);
    }

    private long getMaxId() {
        Long maxId = popupRepository.maxId();
        return maxId == null ? 1 : maxId + 1;
    }
}
