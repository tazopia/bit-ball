package spoon.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spoon.login.entity.Login;

public interface LoginRepository extends JpaRepository<Login, String> {

}
