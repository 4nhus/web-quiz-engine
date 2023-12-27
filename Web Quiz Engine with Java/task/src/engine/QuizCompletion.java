package engine;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_completions")
public class QuizCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String user;
    private long quizId;
    private LocalDateTime completedAt;

    public QuizCompletion(String user, long quizId, LocalDateTime completedAt) {
        this.user = user;
        this.quizId = quizId;
        this.completedAt = completedAt;
    }

    public QuizCompletion(long id, String user, long quizId, LocalDateTime completedAt) {
        this.id = id;
        this.user = user;
        this.quizId = quizId;
        this.completedAt = completedAt;
    }

    public QuizCompletion() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getQuizId() {
        return quizId;
    }

    public void setQuizId(long quizId) {
        this.quizId = quizId;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
