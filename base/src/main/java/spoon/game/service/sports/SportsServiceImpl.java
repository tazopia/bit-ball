package spoon.game.service.sports;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import spoon.config.domain.Config;
import spoon.game.domain.SportsDto;
import spoon.game.entity.QSports;
import spoon.game.entity.Sports;
import spoon.game.repository.SportsRepository;
import spoon.support.web.FileUploadHandler;
import spoon.support.web.UploadType;

@AllArgsConstructor
@Service
public class SportsServiceImpl implements SportsService {

    private SportsRepository sportsRepository;

    private FileUploadHandler fileUploadHandler;

    private static QSports q = QSports.sports;

    @Override
    public Iterable<Sports> getAll() {
        return sportsRepository.findAll(new Sort("sort"));
    }

    @Transactional
    @Override
    public void addSports(String sportsName) {
        if (Config.getSportsMap().containsKey(sportsName)) return;

        Sports sports = new Sports();
        sports.setSportsName(sportsName);
        sports.setSportsFlag("sports.png");
        sports.setSort(getMaxSort());
        sportsRepository.saveAndFlush(sports);
        updateSportsMap(sports);
    }

    @Transactional
    @Override
    public void addSports(SportsDto.Add add, MultipartFile file) {
        if (Config.getSportsMap().containsKey(add.getSportsName())) return;

        String saveFileName = fileUploadHandler.saveFile(file, add.getSportsName(), UploadType.SPORTS);
        Sports sports = new Sports();
        sports.setSportsName(add.getSportsName());
        sports.setSportsFlag(saveFileName);
        sports.setSort(getMaxSort());
        sports.setHidden(add.isHidden());
        sportsRepository.saveAndFlush(sports);
        updateSportsMap(sports);
    }

    @Transactional
    @Override
    public void updateSports(SportsDto.Update update, MultipartFile file) {
        Sports sports = sportsRepository.findOne(update.getId());
        if (file != null && file.getSize() > 0) {
            String saveFileName = fileUploadHandler.saveFile(file, sports.getSportsName(), UploadType.SPORTS);
            sports.setSportsFlag(saveFileName);
        }
        sports.setHidden(update.isHidden());
        sportsRepository.saveAndFlush(sports);
        updateSportsMap(sports);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Sports sports = sportsRepository.findOne(id);
        sportsRepository.sortUpPrevAll(sports.getSort());
        sportsRepository.delete(sports);
        removeSportsMap(sports);
    }

    @Transactional
    @Override
    public void up(Long id) {
        Sports sports = sportsRepository.findOne(id);
        if (sports.getSort() == 1) return;

        sortDownNextSports(sports);
        sports.sortUp();
        sportsRepository.saveAndFlush(sports);
    }

    @Transactional
    @Override
    public void down(Long id) {
        Sports sports = sportsRepository.findOne(id);
        int max = sportsRepository.maxSort();
        if (sports.getSort() == max) return;

        sortUpPrevSports(sports);
        sports.sortDown();
        sportsRepository.saveAndFlush(sports);
    }

    @Override
    public Sports findOne(Long id) {
        return sportsRepository.findOne(id);
    }

    private void sortUpPrevSports(Sports sports) {
        int sort = sports.getSort() + 1;
        Sports up = sportsRepository.findOne(q.sort.eq(sort));
        up.setSort(up.getSort() - 1);
        sportsRepository.saveAndFlush(up);
    }

    private void sortDownNextSports(Sports sports) {
        int sort = sports.getSort() - 1;
        Sports down = sportsRepository.findOne(q.sort.eq(sort));
        down.setSort(down.getSort() + 1);
        sportsRepository.saveAndFlush(down);
    }

    private void updateSportsMap(Sports sports) {
        Config.getSportsMap().put(sports.getSportsName(), sports);
    }

    private void removeSportsMap(Sports sports) {
        Config.getSportsMap().remove(sports.getSportsName());
    }

    private int getMaxSort() {
        Integer maxValue = sportsRepository.maxSort();
        return maxValue == null ? 1 : maxValue + 1;
    }
}
