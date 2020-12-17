package spoon.inPlay.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spoon.inPlay.config.entity.InPlaySports;

public interface InPlaySportsRepository extends JpaRepository<InPlaySports, String> {

}
