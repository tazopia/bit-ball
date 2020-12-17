package spoon.game.service.sports;

import org.springframework.web.multipart.MultipartFile;
import spoon.game.domain.SportsDto;
import spoon.game.entity.Sports;

public interface SportsService {

    Iterable<Sports> getAll();

    Sports findOne(Long id);

    void addSports(String sportsName);

    void addSports(SportsDto.Add add, MultipartFile file);

    void updateSports(SportsDto.Update update, MultipartFile file);

    void delete(Long id);

    void up(Long id);

    void down(Long id);
}
