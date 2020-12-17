package spoon.board.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spoon.board.domain.NoticeDto;
import spoon.board.entity.Notice;
import spoon.board.entity.QNotice;
import spoon.board.repository.NoticeRepository;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Service
public class NoticeServiceImpl implements NoticeService {

    private NoticeRepository noticeRepository;

    private static QNotice q = QNotice.notice;

    @Override
    public Iterable<Notice> list() {
        return noticeRepository.findAll(new Sort(Sort.Direction.DESC, "regDate"));
    }

    @Override
    public Iterable<Notice> siteList() {
        return noticeRepository.findAll(q.enabled.isTrue(), new Sort(Sort.Direction.DESC, "regDate"));
    }

    @Transactional
    @Override
    public boolean add(NoticeDto.Add add) {
        Notice notice = new Notice();
        notice.setTitle(add.getTitle());

        notice.setRegDate(add.getRegDate());
        notice.setEnabled(add.isEnabled());
        noticeRepository.saveAndFlush(notice);

        return true;
    }

    @Override
    public Notice get(Long id) {
        return noticeRepository.findOne(id);
    }

    @Transactional
    @Override
    public boolean update(NoticeDto.Update update) {
        Notice notice = get(update.getId());
        notice.setTitle(update.getTitle());
        notice.setRegDate(update.getRegDate());
        notice.setEnabled(update.isEnabled());
        return true;
    }

    @Transactional
    @Override
    public AjaxResult delete(Long id) {
        noticeRepository.delete(id);
        return new AjaxResult(true);
    }

}
