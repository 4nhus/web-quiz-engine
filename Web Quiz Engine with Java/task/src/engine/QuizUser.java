package engine;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class QuizUser {
    @Id
    private String email;
    private String password;


    public QuizUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public QuizUser() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
