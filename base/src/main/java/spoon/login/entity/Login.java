package spoon.login.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Login {

    @Id
    private String username;

    private String password;

    public static Login of(String username, String password) {
        Login login = new Login();
        login.username = username;
        login.password = password;
        return login;
    }
}
